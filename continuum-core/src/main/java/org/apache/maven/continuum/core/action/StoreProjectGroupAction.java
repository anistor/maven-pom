package org.apache.maven.continuum.core.action;

import org.apache.maven.continuum.ContinuumException;
import org.apache.maven.continuum.model.project.ProjectGroup;
import org.apache.maven.continuum.store.ContinuumStore;
import org.apache.maven.continuum.store.ContinuumStoreException;

import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class StoreProjectGroupAction
    extends AbstractContinuumAction
{
    private ContinuumStore store;

    public void execute( Map context )
        throws ContinuumException, ContinuumStoreException
    {
        ProjectGroup projectGroup = getUnvalidatedProjectGroup( context );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        projectGroup = store.addProjectGroup( projectGroup );

        context.put( KEY_PROJECT_GROUP_ID, Integer.toString( projectGroup.getId() ) );
    }
}