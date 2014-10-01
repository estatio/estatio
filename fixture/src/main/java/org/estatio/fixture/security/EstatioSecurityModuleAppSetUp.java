/*
 *  Copyright 2014 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.fixture.security;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.isisaddons.module.security.seed.SeedUsersAndRolesFixtureScript;

import org.estatio.fixture.security.perms.EstatioAdminRoleAndPermissions;
import org.estatio.fixture.security.perms.EstatioRolesAndPermissions;
import org.estatio.fixture.security.perms.EstatioUserRoleAndPermissions;
import org.estatio.fixture.security.tenancy.AllTenancies;
import org.estatio.fixture.security.userrole.EstatioAdmin_Has_EstatioAdminRole;
import org.estatio.fixture.security.userrole.EstatioAdmin_Has_IsisSecurityModuleAdminRole;
import org.estatio.fixture.security.userrole.EstatioUser_Has_EstatioPoweruserRole;
import org.estatio.fixture.security.userrole.EstatioUser_Has_IsisSecurityModuleRegularRole;
import org.estatio.fixture.security.users.EstatioAdmin;
import org.estatio.fixture.security.users.EstatioUser;

public class EstatioSecurityModuleAppSetUp extends DiscoverableFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        execute(new SeedUsersAndRolesFixtureScript(), executionContext);

        execute(new EstatioRolesAndPermissions(), executionContext);
        execute(new EstatioAdmin(), executionContext);
        execute(new EstatioUser(), executionContext);
        execute(new AllTenancies(), executionContext);

        // perms (role/features)
        execute(new EstatioUserRoleAndPermissions(), executionContext);
        execute(new EstatioAdminRoleAndPermissions(), executionContext);

        // user/role
        execute(new EstatioAdmin_Has_EstatioAdminRole(), executionContext);
        execute(new EstatioAdmin_Has_IsisSecurityModuleAdminRole(), executionContext);

        execute(new EstatioUser_Has_EstatioPoweruserRole(), executionContext);
        execute(new EstatioUser_Has_IsisSecurityModuleRegularRole(), executionContext);

    }

}
