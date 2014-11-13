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

import org.isisaddons.module.security.seed.SeedUsersAndRolesFixtureScript;
import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;
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

        executeChild(new SeedUsersAndRolesFixtureScript(), executionContext);

        executeChild(new EstatioRolesAndPermissions(), executionContext);
        executeChild(new EstatioAdmin(), executionContext);
        executeChild(new EstatioUser(), executionContext);
        executeChild(new AllTenancies(), executionContext);

        // perms (role/features)
        executeChild(new EstatioUserRoleAndPermissions(), executionContext);
        executeChild(new EstatioAdminRoleAndPermissions(), executionContext);

        // user/role
        executeChild(new EstatioAdmin_Has_EstatioAdminRole(), executionContext);
        executeChild(new EstatioAdmin_Has_IsisSecurityModuleAdminRole(), executionContext);

        executeChild(new EstatioUser_Has_EstatioPoweruserRole(), executionContext);
        executeChild(new EstatioUser_Has_IsisSecurityModuleRegularRole(), executionContext);

    }

}
