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

import org.estatio.fixture.security.userrole.EstatioUserInFrance_Has_IsisSecurityModuleRegularRole;
import org.estatio.fixture.security.userrole.EstatioUserInItaly_Has_IsisSecurityModuleRegularRole;
import org.estatio.fixture.security.userrole.EstatioUserInNetherlands_Has_IsisSecurityModuleRegularRole;
import org.estatio.fixture.security.userrole.EstatioUserInSweden_Has_IsisSecurityModuleRegularRole;
import org.estatio.fixture.security.users.EstatioUserInGreatBritain;

public class EstatioSecurityModuleDemoFixture extends DiscoverableFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        // user/roles (users with country-specific app tenancy)
        executionContext.executeChild(this, new EstatioUserInFrance_Has_IsisSecurityModuleRegularRole());
        executionContext.executeChild(this, new EstatioUserInGreatBritain());
        executionContext.executeChild(this, new EstatioUserInItaly_Has_IsisSecurityModuleRegularRole());
        executionContext.executeChild(this, new EstatioUserInNetherlands_Has_IsisSecurityModuleRegularRole());
        executionContext.executeChild(this, new EstatioUserInSweden_Has_IsisSecurityModuleRegularRole());

    }

}
