package org.apache.maven.continuum.security;

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

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.rbac.profile.DefaultRoleProfileManager;
import org.codehaus.plexus.rbac.profile.DynamicRoleProfile;
import org.codehaus.plexus.rbac.profile.RoleProfileException;
import org.codehaus.plexus.security.rbac.Role;

/**
 * ContinuumRoleProfileManager:
 *
 * @author: Jesse McConnell <jmcconnell@apache.org>
 * @version: $ID:$
 *
 * @plexus.component
 *   role="org.codehaus.plexus.rbac.profile.RoleProfileManager"
 *   role-hint="continuum"
 */
public class ContinuumRoleProfileManager
    extends DefaultRoleProfileManager
{

    public void initialize()
        throws RoleProfileException
    {
        // make sure registered user and group administrator roles exist
        getRole( "continuum-group-administrator" );
        getRole( "registered-user" );

        // this should not be additive in the database, and will make sure they are in synced on updates
        mergeRoleProfiles( "system-administrator", "continuum-system-administrator" );
        mergeRoleProfiles( "user-administrator", "continuum-user-administrator" );
        mergeRoleProfiles( "guest", "continuum-guest" );

        setInitialized( true );
    }


    public Role getDynamicRole( String roleHint, String resource )
        throws RoleProfileException
    {
        if ( !isInitialized() )
        {
            initialize();
        }

        try
        {            
            DynamicRoleProfile roleProfile =  (DynamicRoleProfile)container.lookup( DynamicRoleProfile.ROLE, roleHint );

            return roleProfile.getRole( resource ); 
        }
        catch ( ComponentLookupException cle )
        {
            throw new RoleProfileException( "unable to locate dynamic role profile " + roleHint, cle );
        }
    }
}
