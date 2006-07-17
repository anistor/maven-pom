package org.apache.maven.continuum.web.action;

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

import org.apache.maven.continuum.Continuum;
import org.apache.maven.continuum.ContinuumException;

import java.util.Collection;

/**
 * @author Nik Gonzalez
 *
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="schedule"
 */
public class ScheduleAction
    extends AbstractContinuumAction
{
    /**
     * @plexus.requirement
     */
    private Continuum continuum;

    private Collection schedules;

    public String execute()
        throws Exception
    {
        try
        {
            schedules = continuum.getSchedules();
        }
        catch ( ContinuumException e )
        {
            e.printStackTrace();
        }

        return SUCCESS;
    }

    public Collection getSchedules()
    {
        return schedules;
    }

}