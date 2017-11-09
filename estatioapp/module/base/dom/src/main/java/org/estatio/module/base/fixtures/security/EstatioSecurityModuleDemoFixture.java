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
package org.estatio.module.base.fixtures.security;

import org.apache.isis.applib.fixturescripts.DiscoverableFixtureScript;

import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForFrViv;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForFrVivDefault;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForFrVivTa;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForGbOxf;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForGbOxfDefault;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForGbOxfTa;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForItGra;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForItGraDefault;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForItGraTa;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForNlKal;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForNlKalDefault;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForNlKalTa;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForSeHan;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForSeHanDefault;
import org.estatio.module.base.fixtures.security.apptenancy.personas.ApplicationTenancyForSeHanTa;
import org.estatio.module.base.fixtures.security.userrole.personas.EstatioUserInFrance_Has_IsisSecurityModuleRegularRole;
import org.estatio.module.base.fixtures.security.userrole.personas.EstatioUserInItaly_Has_IsisSecurityModuleRegularRole;
import org.estatio.module.base.fixtures.security.userrole.personas.EstatioUserInNetherlands_Has_IsisSecurityModuleRegularRole;
import org.estatio.module.base.fixtures.security.userrole.personas.EstatioUserInSweden_Has_IsisSecurityModuleRegularRole;
import org.estatio.module.base.fixtures.security.users.personas.EstatioUserInGreatBritain;

public class EstatioSecurityModuleDemoFixture extends DiscoverableFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        executionContext.executeChild(this, new ApplicationTenancyForFrViv());
        executionContext.executeChild(this, new ApplicationTenancyForFrVivDefault());
        executionContext.executeChild(this, new ApplicationTenancyForFrVivTa());

        executionContext.executeChild(this, new ApplicationTenancyForItGra());
        executionContext.executeChild(this, new ApplicationTenancyForItGraDefault());
        executionContext.executeChild(this, new ApplicationTenancyForItGraTa());

        executionContext.executeChild(this, new ApplicationTenancyForNlKal());
        executionContext.executeChild(this, new ApplicationTenancyForNlKalDefault());
        executionContext.executeChild(this, new ApplicationTenancyForNlKalTa());

        executionContext.executeChild(this, new ApplicationTenancyForSeHan());
        executionContext.executeChild(this, new ApplicationTenancyForSeHanTa());
        executionContext.executeChild(this, new ApplicationTenancyForSeHanDefault());

        executionContext.executeChild(this, new ApplicationTenancyForGbOxf());
        executionContext.executeChild(this, new ApplicationTenancyForGbOxfTa());
        executionContext.executeChild(this, new ApplicationTenancyForGbOxfDefault());

        // user/roles (users with country-specific app tenancy)
        executionContext.executeChild(this, new EstatioUserInFrance_Has_IsisSecurityModuleRegularRole());
        executionContext.executeChild(this, new EstatioUserInGreatBritain());
        executionContext.executeChild(this, new EstatioUserInItaly_Has_IsisSecurityModuleRegularRole());
        executionContext.executeChild(this, new EstatioUserInNetherlands_Has_IsisSecurityModuleRegularRole());
        executionContext.executeChild(this, new EstatioUserInSweden_Has_IsisSecurityModuleRegularRole());

    }

}
