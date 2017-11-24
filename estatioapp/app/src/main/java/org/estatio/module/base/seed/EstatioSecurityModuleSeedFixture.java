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
package org.estatio.module.base.seed;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.isisaddons.module.security.seed.SeedUsersAndRolesFixtureScript;
import org.isisaddons.module.togglz.glue.seed.TogglzModuleAdminRole;

import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForFr;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForFrOther;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForGb;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForGbOther;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForGlobal;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForGlobalOnly;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForIt;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForItOther;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForNl;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForNlOther;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForSe;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForSeOther;
import org.estatio.module.base.fixtures.security.perms.personas.EstatioRolesAndPermissions;
import org.estatio.module.base.fixtures.security.userrole.personas.EstatioAdmin_Has_EstatioAdminRole;
import org.estatio.module.base.fixtures.security.userrole.personas.EstatioAdmin_Has_EstatioSuperuserRole;
import org.estatio.module.base.fixtures.security.userrole.personas.EstatioAdmin_Has_IsisSecurityModuleAdminRole;
import org.estatio.module.base.fixtures.security.userrole.personas.EstatioAdmin_Has_TogglzAdminRole;
import org.estatio.module.base.fixtures.security.userrole.personas.EstatioUserInFrance_Has_IsisSecurityModuleRegularRole;
import org.estatio.module.base.fixtures.security.userrole.personas.EstatioUserInItaly_Has_IsisSecurityModuleRegularRole;
import org.estatio.module.base.fixtures.security.userrole.personas.EstatioUserInNetherlands_Has_IsisSecurityModuleRegularRole;
import org.estatio.module.base.fixtures.security.userrole.personas.EstatioUserInSweden_Has_IsisSecurityModuleRegularRole;
import org.estatio.module.base.fixtures.security.userrole.personas.EstatioUser_Has_IsisSecurityModuleRegularRole;
import org.estatio.module.base.fixtures.security.users.personas.EstatioAdmin;
import org.estatio.module.base.fixtures.security.users.personas.EstatioUser;
import org.estatio.module.base.fixtures.security.users.personas.EstatioUserInFrance;
import org.estatio.module.base.fixtures.security.users.personas.EstatioUserInGreatBritain;
import org.estatio.module.base.fixtures.security.users.personas.EstatioUserInItaly;
import org.estatio.module.base.fixtures.security.users.personas.EstatioUserInNetherlands;
import org.estatio.module.base.fixtures.security.users.personas.EstatioUserInSweden;

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

        // perms (role/features)
        executionContext.executeChild(this, new EstatioRolesAndPermissions());

        executionContext.executeChild(this, new EstatioAdmin());
        executionContext.executeChild(this, new EstatioUser());
        executionContext.executeChild(this, new EstatioUserInFrance());
        executionContext.executeChild(this, new EstatioUserInGreatBritain());
        executionContext.executeChild(this, new EstatioUserInItaly());
        executionContext.executeChild(this, new EstatioUserInNetherlands());
        executionContext.executeChild(this, new EstatioUserInSweden());

        executionContext.executeChild(this, new TogglzModuleAdminRole());

        // user/role (users with global app tenancy)
        executionContext.executeChild(this, new EstatioAdmin_Has_EstatioAdminRole());
        executionContext.executeChild(this, new EstatioAdmin_Has_EstatioSuperuserRole());
        executionContext.executeChild(this, new EstatioAdmin_Has_IsisSecurityModuleAdminRole());
        executionContext.executeChild(this, new EstatioAdmin_Has_TogglzAdminRole());


        executionContext.executeChild(this, new EstatioUser_Has_IsisSecurityModuleRegularRole());

        // user/roles (users with country-specific app tenancy)
        executionContext.executeChild(this, new EstatioUserInFrance_Has_IsisSecurityModuleRegularRole());
        executionContext.executeChild(this, new EstatioUserInItaly_Has_IsisSecurityModuleRegularRole());
        executionContext.executeChild(this, new EstatioUserInNetherlands_Has_IsisSecurityModuleRegularRole());
        executionContext.executeChild(this, new EstatioUserInSweden_Has_IsisSecurityModuleRegularRole());

    }

}
