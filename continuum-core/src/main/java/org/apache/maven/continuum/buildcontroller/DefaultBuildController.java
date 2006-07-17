package org.apache.maven.continuum.buildcontroller;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.continuum.core.action.AbstractContinuumAction;
import org.apache.maven.continuum.model.project.BuildDefinition;
import org.apache.maven.continuum.model.project.BuildResult;
import org.apache.maven.continuum.model.project.Project;
import org.apache.maven.continuum.model.scm.ChangeFile;
import org.apache.maven.continuum.model.scm.ChangeSet;
import org.apache.maven.continuum.model.scm.ScmResult;
import org.apache.maven.continuum.notification.ContinuumNotificationDispatcher;
import org.apache.maven.continuum.project.ContinuumProjectState;
import org.apache.maven.continuum.scm.ContinuumScmException;
import org.apache.maven.continuum.store.ContinuumStore;
import org.apache.maven.continuum.store.ContinuumStoreException;
import org.apache.maven.continuum.utils.ContinuumUtils;
import org.apache.maven.continuum.utils.WorkingDirectoryService;
import org.codehaus.plexus.action.ActionManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultBuildController
    extends AbstractLogEnabled
    implements BuildController
{
    /**
     * @plexus.requirement
     */
    private ContinuumStore store;

    /**
     * @plexus.requirement
     */
    private ContinuumNotificationDispatcher notifierDispatcher;

    /**
     * @plexus.requirement
     */
    private ActionManager actionManager;

    /**
     * @plexus.requirement
     */
    private WorkingDirectoryService workingDirectoryService;

    // ----------------------------------------------------------------------
    // BuildController Implementation
    // ----------------------------------------------------------------------

    /**
     * @todo structure of this method is a bit of a mess (too much exception/finally code)
     */
    public void build( int projectId, int buildDefinitionId, int trigger )
    {
        long startTime = System.currentTimeMillis();

        // ----------------------------------------------------------------------
        // Initialize the context
        // ----------------------------------------------------------------------

        // if these calls fail we're screwed anyway
        // and it will only be logged through the logger.

        Project project;

        BuildDefinition buildDefinition;

        BuildResult oldBuildResult = null;

        BuildResult build = null;

        try
        {
            project = store.getProject( projectId );

            project.setOldState( project.getState() );

            project.setState( ContinuumProjectState.BUILDING );

            store.updateProject( project );

            buildDefinition = store.getBuildDefinition( buildDefinitionId );

            notifierDispatcher.buildStarted( project );
        }
        catch ( ContinuumStoreException ex )
        {
            getLogger().error( "Internal error while getting the project.", ex );

            return;
        }

        try
        {
            oldBuildResult = store.getBuildResult( buildDefinition.getLatestBuildId() );
        }
        catch ( ContinuumStoreException ex )
        {
            // Nothing to do
        }

        ScmResult oldScmResult = null;

        if ( oldBuildResult != null )
        {
            oldScmResult = getOldScmResult( project, oldBuildResult.getEndTime() );
        }

        // ----------------------------------------------------------------------
        // TODO: Centralize the error handling from the SCM related actions.
        // ContinuumScmResult should return a ContinuumScmResult from all
        // methods, even in a case of failure.
        // ----------------------------------------------------------------------

        try
        {
            Map actionContext = new HashMap();

            actionContext.put( AbstractContinuumAction.KEY_PROJECT_ID, new Integer( projectId ) );

            actionContext.put( AbstractContinuumAction.KEY_PROJECT, project );

            actionContext.put( AbstractContinuumAction.KEY_BUILD_DEFINITION_ID, new Integer( buildDefinitionId ) );

            actionContext.put( AbstractContinuumAction.KEY_BUILD_DEFINITION, buildDefinition );

            actionContext.put( AbstractContinuumAction.KEY_TRIGGER, new Integer( trigger ) );

            actionContext.put( AbstractContinuumAction.KEY_FIRST_RUN, Boolean.valueOf( oldBuildResult == null ) );

            ScmResult scmResult = null;

            try
            {
                actionManager.lookup( "check-working-directory" ).execute( actionContext );

                boolean workingDirectoryExists = AbstractContinuumAction.getBoolean( actionContext,
                                                                                     AbstractContinuumAction.KEY_WORKING_DIRECTORY_EXISTS );

                if ( workingDirectoryExists )
                {
                    actionManager.lookup( "update-working-directory-from-scm" ).execute( actionContext );

                    scmResult = AbstractContinuumAction.getUpdateScmResult( actionContext, null );
                }
                else
                {
                    actionContext.put( AbstractContinuumAction.KEY_WORKING_DIRECTORY,
                                       workingDirectoryService.getWorkingDirectory( project ).getAbsolutePath() );

                    actionManager.lookup( "checkout-project" ).execute( actionContext );

                    scmResult = AbstractContinuumAction.getCheckoutResult( actionContext, null );
                }

                // ----------------------------------------------------------------------
                // Check to see if there was a error while checking out/updating the project
                // ----------------------------------------------------------------------

                // Merge scm results so we'll have all changes since last execution of current build definition
                scmResult = mergeScmResults( oldScmResult, scmResult );

                if ( scmResult == null || !scmResult.isSuccess() )
                {
                    // scmResult must be converted before sotring it because jpox modify value of all fields to null
                    String error = convertScmResultToError( scmResult );

                    build = makeAndStoreBuildResult( project, scmResult, startTime, trigger );

                    build.setError( error );

                    store.updateBuildResult( build );

                    build = store.getBuildResult( build.getId() );

                    project.setState( build.getState() );

                    store.updateProject( project );

                    return;
                }

                actionContext.put( AbstractContinuumAction.KEY_UPDATE_SCM_RESULT, scmResult );

                List changes = scmResult.getChanges();

                Iterator iterChanges = changes.iterator();

                ChangeSet changeSet;

                List changeFiles;

                Iterator iterFiles;

                ChangeFile changeFile;

                boolean allChangesUnknown = true;

                while ( iterChanges.hasNext() )
                {
                    changeSet = (ChangeSet) iterChanges.next();

                    changeFiles = changeSet.getFiles();

                    iterFiles = changeFiles.iterator();

                    while ( iterFiles.hasNext() )
                    {
                        changeFile = (ChangeFile) iterFiles.next();

                        if ( !"unknown".equalsIgnoreCase( changeFile.getStatus() ) )
                        {
                            allChangesUnknown = false;
                            break;
                        }
                    }

                    if ( !allChangesUnknown )
                    {
                        break;
                    }
                }

                if ( oldBuildResult != null && allChangesUnknown &&
                    project.getOldState() != ContinuumProjectState.NEW &&
                    project.getOldState() != ContinuumProjectState.CHECKEDOUT &&
                    trigger != ContinuumProjectState.TRIGGER_FORCED &&
                    project.getState() != ContinuumProjectState.NEW &&
                    project.getState() != ContinuumProjectState.CHECKEDOUT )
                {
                    if ( changes.size() > 0 )
                    {
                        getLogger().info( "The project was not built because all changes are unknown." );
                    }
                    else
                    {
                        getLogger().info( "The project was not built because there are no changes." );
                    }

                    project.setState( project.getOldState() );

                    project.setOldState( 0 );

                    store.updateProject( project );

                    return;
                }

                actionManager.lookup( "update-project-from-working-directory" ).execute( actionContext );

                actionManager.lookup( "execute-builder" ).execute( actionContext );

                actionManager.lookup( "deploy-artifact" ).execute( actionContext );

                String s = (String) actionContext.get( AbstractContinuumAction.KEY_BUILD_ID );

                if ( s != null )
                {
                    build = store.getBuildResult( Integer.valueOf( s ).intValue() );
                }
            }
            catch ( Throwable e )
            {
                getLogger().error( "Error while building project.", e );

                String s = (String) actionContext.get( AbstractContinuumAction.KEY_BUILD_ID );

                if ( s != null )
                {
                    build = store.getBuildResult( Integer.valueOf( s ).intValue() );
                }
                else
                {
                    build = makeAndStoreBuildResult( project, scmResult, startTime, trigger );
                }

                // This can happen if the "update project from scm" action fails

                String error = null;

                if ( e instanceof ContinuumScmException )
                {
                    ContinuumScmException ex = (ContinuumScmException) e;

                    ScmResult result = ex.getResult();

                    if ( result != null )
                    {
                        error = convertScmResultToError( result );
                    }

                }
                if ( error == null )
                {
                    error = ContinuumUtils.throwableToString( e );
                }

                build.setError( error );

                store.updateBuildResult( build );

                build = store.getBuildResult( build.getId() );

                project.setState( build.getState() );

                store.updateProject( project );
            }
        }
        catch ( Exception ex )
        {
            if ( !Thread.interrupted() )
            {
                getLogger().error( "Internal error while building the project.", ex );
            }

            String error = ContinuumUtils.throwableToString( ex );

            build.setError( error );

            try
            {
                store.updateBuildResult( build );

                build = store.getBuildResult( build.getId() );

                project.setState( build.getState() );

                store.updateProject( project );
            }
            catch ( Exception e )
            {
                getLogger().error( "Can't store updating project.", e );
            }
        }
        finally
        {
            if ( project.getState() != ContinuumProjectState.NEW &&
                project.getState() != ContinuumProjectState.CHECKEDOUT &&
                project.getState() != ContinuumProjectState.OK && project.getState() != ContinuumProjectState.FAILED &&
                project.getState() != ContinuumProjectState.ERROR )
            {
                try
                {
                    project.setState( ContinuumProjectState.ERROR );

                    store.updateProject( project );
                }
                catch ( ContinuumStoreException e )
                {
                    getLogger().error( "Internal error while storing the project.", e );
                }
            }

            notifierDispatcher.buildComplete( project, build );
        }
    }

    private String convertScmResultToError( ScmResult result )
    {
        String error = "";

        if ( result == null )
        {
            error = "Scm result is null.";
        }
        else
        {
            if ( result.getCommandLine() != null )
            {
                error = "Command line: " + StringUtils.clean( result.getCommandLine() ) +
                    System.getProperty( "line.separator" );
            }

            if ( result.getProviderMessage() != null )
            {
                error = "Provider message: " + StringUtils.clean( result.getProviderMessage() ) +
                    System.getProperty( "line.separator" );
            }

            if ( result.getCommandOutput() != null )
            {
                error += "Command output: " + System.getProperty( "line.separator" );
                error += "-------------------------------------------------------------------------------" +
                    System.getProperty( "line.separator" );
                error += StringUtils.clean( result.getCommandOutput() ) + System.getProperty( "line.separator" );
                error += "-------------------------------------------------------------------------------" +
                    System.getProperty( "line.separator" );
            }

            if ( result.getException() != null )
            {
                error += "Exception:" + System.getProperty( "line.separator" );
                error += result.getException();
            }
        }

        return error;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private BuildResult makeAndStoreBuildResult( Project project, ScmResult scmResult, long startTime, int trigger )
        throws ContinuumStoreException
    {
        BuildResult build = new BuildResult();

        build.setState( ContinuumProjectState.ERROR );

        build.setTrigger( trigger );

        build.setStartTime( startTime );

        build.setEndTime( System.currentTimeMillis() );

        build.setScmResult( scmResult );

        store.addBuildResult( project, build );

        return store.getBuildResult( build.getId() );
    }

    private ScmResult getOldScmResult( Project project, long fromDate )
    {
        List results = store.getBuildResultsForProject( project.getId(), fromDate );

        ScmResult res = new ScmResult();

        if ( results != null )
        {
            for ( Iterator i = results.iterator(); i.hasNext(); )
            {
                BuildResult result = (BuildResult) i.next();

                ScmResult scmResult = result.getScmResult();

                if ( scmResult != null )
                {
                    List changes = scmResult.getChanges();

                    if ( changes != null )
                    {
                        for ( Iterator j = changes.iterator(); j.hasNext(); )
                        {
                            ChangeSet changeSet = (ChangeSet) j.next();

                            if ( changeSet.getDate() < fromDate )
                            {
                                continue;
                            }

                            if ( !res.getChanges().contains( changeSet ) )
                            {
                                res.addChange( changeSet );
                            }
                        }
                    }
                }
            }
        }

        return res;
    }

    private ScmResult mergeScmResults( ScmResult oldScmResult, ScmResult newScmResult )
    {
        if ( oldScmResult != null )
        {
            if ( newScmResult == null )
            {
                return oldScmResult;
            }

            List oldChanges = oldScmResult.getChanges();

            List newChanges = newScmResult.getChanges();

            for ( Iterator i = newChanges.iterator(); i.hasNext(); )
            {
                ChangeSet change = (ChangeSet) i.next();

                if ( !oldChanges.contains( change ) )
                {
                    oldChanges.add( change );
                }
            }

            newScmResult.setChanges( oldChanges );
        }

        return newScmResult;
    }
}