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

import org.estatio.fixture.security.tenancy.ApplicationTenancyForFrViv;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForFrVivDefault;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForFrVivTa;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGbOxf;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGbOxfDefault;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGbOxfTa;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForItGra;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForItGraDefault;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForItGraTa;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForNlKal;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForNlKalDefault;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForNlKalTa;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForSeHan;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForSeHanDefault;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForSeHanTa;
import org.estatio.fixture.security.userrole.EstatioUserInFrance_Has_IsisSecurityModuleRegularRole;
import org.estatio.fixture.security.userrole.EstatioUserInItaly_Has_IsisSecurityModuleRegularRole;
import org.estatio.fixture.security.userrole.EstatioUserInNetherlands_Has_IsisSecurityModuleRegularRole;
import org.estatio.fixture.security.userrole.EstatioUserInSweden_Has_IsisSecurityModuleRegularRole;
import org.estatio.fixture.security.users.EstatioUserInGreatBritain;

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
