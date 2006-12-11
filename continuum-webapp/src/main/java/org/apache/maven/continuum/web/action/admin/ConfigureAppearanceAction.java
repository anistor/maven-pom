package org.apache.maven.continuum.web.action.admin;

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

import com.opensymphony.xwork.ModelDriven;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.continuum.execution.maven.m2.MavenBuilderHelper;
import org.apache.maven.continuum.security.ContinuumRoleConstants;
import org.apache.maven.continuum.web.action.ContinuumActionSupport;
import org.apache.maven.model.Model;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.shared.app.company.CompanyPomHandler;
import org.apache.maven.shared.app.configuration.Configuration;
import org.apache.maven.shared.app.configuration.ConfigurationChangeException;
import org.apache.maven.shared.app.configuration.ConfigurationStore;
import org.apache.maven.shared.app.configuration.ConfigurationStoreException;
import org.apache.maven.shared.app.configuration.InvalidConfigurationException;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.ui.web.interceptor.SecureAction;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionBundle;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionException;

import java.io.IOException;

/**
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id: ConfigurationAction.java 480950 2006-11-30 14:58:35Z evenisse $
 * @plexus.component role="com.opensymphony.xwork.Action"
 * role-hint="configureAppearance"
 */
public class ConfigureAppearanceAction
    extends ContinuumActionSupport
    implements ModelDriven, SecureAction
{
    /**
     * @plexus.requirement
     */
    private ConfigurationStore configurationStore;

    /**
     * The configuration.
     */
    private Configuration configuration;

    private Model companyModel;

    /**
     * @plexus.requirement
     */
    private CompanyPomHandler companyPomHandler;

    /**
     * @plexus.requirement
     */
    private MavenBuilderHelper helper;

    public String execute()
        throws IOException, ConfigurationStoreException, InvalidConfigurationException, ConfigurationChangeException
    {
        configurationStore.storeConfiguration( configuration );

        return SUCCESS;
    }

    public Object getModel()
    {
        return configuration;
    }

    public void prepare()
        throws ConfigurationStoreException, ProjectBuildingException, ArtifactMetadataRetrievalException
    {
        configuration = configurationStore.getConfigurationFromStore();

        companyModel =
            companyPomHandler.getCompanyPomModel( configuration.getCompanyPom(), helper.getLocalRepository() );
    }

    public SecureActionBundle getSecureActionBundle()
        throws SecureActionException
    {
        SecureActionBundle bundle = new SecureActionBundle();
        bundle.setRequiresAuthentication( true );
        bundle.addRequiredAuthorization( ContinuumRoleConstants.CONTINUUM_MANAGE_CONFIGURATION, Resource.GLOBAL );

        return bundle;
    }

    public Model getCompanyModel()
    {
        return companyModel;
    }
}