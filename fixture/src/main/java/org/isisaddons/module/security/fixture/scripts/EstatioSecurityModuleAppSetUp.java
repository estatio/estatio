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
package org.isisaddons.module.security.fixture.scripts;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.isisaddons.module.security.fixture.scripts.perms.EstatioAdminRoleAndPermissions;
import org.isisaddons.module.security.fixture.scripts.perms.EstatioRolesAndPermissions;
import org.isisaddons.module.security.fixture.scripts.perms.EstatioUserRoleAndPermissions;
import org.isisaddons.module.security.fixture.scripts.tenancy.AllTenancies;
import org.isisaddons.module.security.fixture.scripts.userrole.AdminUser_Has_EstatioAdminRole;
import org.isisaddons.module.security.fixture.scripts.userrole.AdminUser_Has_IsisSecurityModuleUserRole;
import org.isisaddons.module.security.fixture.scripts.userrole.UserUser_Has_EstatioPoweruserRole;
import org.isisaddons.module.security.fixture.scripts.userrole.UserUser_Has_IsisSecurityModuleRegularRole;
import org.isisaddons.module.security.fixture.scripts.users.AdminUser;
import org.isisaddons.module.security.fixture.scripts.users.UserUser;
import org.isisaddons.module.security.seed.SeedUsersAndRolesFixtureScript;

public class EstatioSecurityModuleAppSetUp extends DiscoverableFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        execute(new EstatioSecurityModuleAppTearDown(), executionContext);
        execute(new SeedUsersAndRolesFixtureScript(), executionContext);

        execute(new EstatioRolesAndPermissions(), executionContext);
        execute(new AdminUser(), executionContext);
        execute(new UserUser(), executionContext);
        execute(new AllTenancies(), executionContext);

        // perms (role/features)
        execute(new EstatioUserRoleAndPermissions(), executionContext);
        execute(new EstatioAdminRoleAndPermissions(), executionContext);

        // user/role
        execute(new AdminUser_Has_EstatioAdminRole(), executionContext);
        execute(new AdminUser_Has_IsisSecurityModuleUserRole(), executionContext);

        execute(new UserUser_Has_EstatioPoweruserRole(), executionContext);
        execute(new UserUser_Has_IsisSecurityModuleRegularRole(), executionContext);

    }

}
