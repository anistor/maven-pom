package org.apache.maven.continuum.web.action.notifier;

/*
 * Copyright 2004-2006 The Apache Software Foundation.
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

import org.apache.maven.continuum.ContinuumException;
import org.apache.maven.continuum.model.project.ProjectGroup;
import org.apache.maven.continuum.model.project.ProjectNotifier;
import org.apache.maven.continuum.web.action.ContinuumActionSupport;

/**
 * Action to delete a {@link ProjectNotifier} instance from a 
 * specified {@link ProjectGroup}.
 * 
 * @author <a href="mailto:evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id: DeleteNotifierAction.java 467122 2006-10-23 20:50:19Z jmcconnell $
 * @since 1.1
 * @plexus.component 
 *   role="com.opensymphony.xwork.Action"  
 *   role-hint="deleteGroupNotifier"
 */
public class DeleteGroupNotifierAction
    extends ContinuumActionSupport
{

    private int projectGroupId;

    private int notifierId;

    private String notifierType;

    public String execute()
        throws ContinuumException
    {
        getContinuum().removeGroupNotifier( projectGroupId, notifierId );
        return SUCCESS;
    }

    public String doDefault()
    {
        return "delete";
    }

    public void setNotifierId( int notifierId )
    {
        this.notifierId = notifierId;
    }

    public int getNotifierId()
    {
        return notifierId;
    }

    public void setNotifierType( String notifierType )
    {
        this.notifierType = notifierType;
    }

    public String getNotifierType()
    {
        return notifierType;
    }

    /**
     * @return the projectGroupId
     */
    public int getProjectGroupId()
    {
        return projectGroupId;
    }

    /**
     * @param projectGroupId the projectGroupId to set
     */
    public void setProjectGroupId( int projectGroupId )
    {
        this.projectGroupId = projectGroupId;
    }

}