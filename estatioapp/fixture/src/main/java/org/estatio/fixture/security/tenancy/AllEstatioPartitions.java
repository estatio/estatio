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
package org.estatio.fixture.security.tenancy;

public class AllEstatioPartitions extends AbstractApplicationTenancyFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        executionContext.executeChild(this, new GlobalApplicationTenancy());

        executionContext.executeChild(this, new ApplicationTenancyForIta());
        executionContext.executeChild(this, new ApplicationTenancyForItaGra());
        executionContext.executeChild(this, new ApplicationTenancyForFra());
        executionContext.executeChild(this, new ApplicationTenancyForFraViv());
        executionContext.executeChild(this, new ApplicationTenancyForSwe());
        executionContext.executeChild(this, new ApplicationTenancyForSweHan());
        executionContext.executeChild(this, new ApplicationTenancyForNld());
        executionContext.executeChild(this, new ApplicationTenancyForNldKal());
        executionContext.executeChild(this, new ApplicationTenancyForGbr());
        executionContext.executeChild(this, new ApplicationTenancyForGbrOxf());
    }

}
