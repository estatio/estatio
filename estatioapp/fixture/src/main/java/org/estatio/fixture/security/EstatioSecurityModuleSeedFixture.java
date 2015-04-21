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
import org.estatio.fixture.security.tenancy.ApplicationTenancyForFr;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForFrOther;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGb;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGbOther;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGlobal;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGlobalOnly;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForIt;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForItOther;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForNl;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForNlOther;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForSe;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForSeOther;
import org.estatio.fixture.security.userrole.EstatioAdmin_Has_EstatioAdminRole;
import org.estatio.fixture.security.userrole.EstatioAdmin_Has_IsisSecurityModuleAdminRole;
import org.estatio.fixture.security.userrole.EstatioUserInFrance_Has_IsisSecurityModuleRegularRole;
import org.estatio.fixture.security.userrole.EstatioUserInItaly_Has_IsisSecurityModuleRegularRole;
import org.estatio.fixture.security.userrole.EstatioUserInNetherlands_Has_IsisSecurityModuleRegularRole;
import org.estatio.fixture.security.userrole.EstatioUserInSweden_Has_IsisSecurityModuleRegularRole;
import org.estatio.fixture.security.userrole.EstatioUser_Has_EstatioPoweruserRole;
import org.estatio.fixture.security.userrole.EstatioUser_Has_IsisSecurityModuleRegularRole;
import org.estatio.fixture.security.users.EstatioAdmin;
import org.estatio.fixture.security.users.EstatioUser;
import org.estatio.fixture.security.users.EstatioUserInFrance;
import org.estatio.fixture.security.users.EstatioUserInGreatBritain;
import org.estatio.fixture.security.users.EstatioUserInItaly;
import org.estatio.fixture.security.users.EstatioUserInNetherlands;
import org.estatio.fixture.security.users.EstatioUserInSweden;

public class EstatioSecurityModuleSeedFixture extends DiscoverableFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        // security module
        executionContext.executeChild(this, new SeedUsersAndRolesFixtureScript());

        // estatio app tenancy, users, roles etc
        executionContext.executeChild(this, new ApplicationTenancyForGlobal());
        executionContext.executeChild(this, new ApplicationTenancyForGlobalOnly());
        executionContext.executeChild(this, new ApplicationTenancyForFr());
        executionContext.executeChild(this, new ApplicationTenancyForFrOther());
        executionContext.executeChild(this, new ApplicationTenancyForIt());
        executionContext.executeChild(this, new ApplicationTenancyForItOther());
        executionContext.executeChild(this, new ApplicationTenancyForNl());
        executionContext.executeChild(this, new ApplicationTenancyForNlOther());
        executionContext.executeChild(this, new ApplicationTenancyForSe());
        executionContext.executeChild(this, new ApplicationTenancyForSeOther());
        executionContext.executeChild(this, new ApplicationTenancyForGb());
        executionContext.executeChild(this, new ApplicationTenancyForGbOther());

        executionContext.executeChild(this, new EstatioRolesAndPermissions());
        executionContext.executeChild(this, new EstatioAdmin());
        executionContext.executeChild(this, new EstatioUser());
        executionContext.executeChild(this, new EstatioUserInFrance());
        executionContext.executeChild(this, new EstatioUserInGreatBritain());
        executionContext.executeChild(this, new EstatioUserInItaly());
        executionContext.executeChild(this, new EstatioUserInNetherlands());
        executionContext.executeChild(this, new EstatioUserInSweden());

        // perms (role/features)
        executionContext.executeChild(this, new EstatioUserRoleAndPermissions());
        executionContext.executeChild(this, new EstatioUser());
        executionContext.executeChild(this, new EstatioAdminRoleAndPermissions());

        // user/role (users with global app tenancy)
        executionContext.executeChild(this, new EstatioAdmin_Has_EstatioAdminRole());
        executionContext.executeChild(this, new EstatioAdmin_Has_IsisSecurityModuleAdminRole());

        executionContext.executeChild(this, new EstatioUser_Has_EstatioPoweruserRole());
        executionContext.executeChild(this, new EstatioUser_Has_IsisSecurityModuleRegularRole());

        // user/roles (users with country-specific app tenancy)
        executionContext.executeChild(this, new EstatioUserInFrance_Has_IsisSecurityModuleRegularRole());
        executionContext.executeChild(this, new EstatioUserInGreatBritain());
        executionContext.executeChild(this, new EstatioUserInItaly_Has_IsisSecurityModuleRegularRole());
        executionContext.executeChild(this, new EstatioUserInNetherlands_Has_IsisSecurityModuleRegularRole());
        executionContext.executeChild(this, new EstatioUserInSweden_Has_IsisSecurityModuleRegularRole());

    }

}
