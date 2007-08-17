package org.apache.maven.doxia.module.twiki;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.doxia.module.site.SiteModule;

/**
 * {@link org.apache.maven.doxia.module.site.SiteModule} implementation for
 * the twiki file format.
 *
 * @author Juan F. Codagnone
 * @version $Id$
 * @since 1.0
 * @plexus.component role="org.apache.maven.doxia.module.site.SiteModule" role-hint="twiki"
 */
public final class TWikiSiteModule implements SiteModule
{

    /**
     * @see SiteModule#getSourceDirectory()
     */
    public String getSourceDirectory()
    {
        return "twiki";
    }

    /**
     * @see SiteModule#getExtension()
     */
    public String getExtension()
    {
        return "twiki";
    }

    /**
     * @see SiteModule#getParserId()
     */
    public String getParserId()
    {
        return "twiki";
    }

}