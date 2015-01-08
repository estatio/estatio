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

public class AllEstatioApplicationTenancies extends AbstractApplicationTenancyFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        executionContext.executeChild(this, new ApplicationTenancyForGlobal());
        executionContext.executeChild(this, new ApplicationTenancyForGlobalOnly());

        executionContext.executeChild(this, new ApplicationTenancyForFr());
        executionContext.executeChild(this, new ApplicationTenancyForFrOther());
        executionContext.executeChild(this, new ApplicationTenancyForFrViv());
        executionContext.executeChild(this, new ApplicationTenancyForFrVivDefault());
        executionContext.executeChild(this, new ApplicationTenancyForFrVivTa());

        executionContext.executeChild(this, new ApplicationTenancyForIt());
        executionContext.executeChild(this, new ApplicationTenancyForItOther());
        executionContext.executeChild(this, new ApplicationTenancyForItGra());
        executionContext.executeChild(this, new ApplicationTenancyForItGraDefault());
        executionContext.executeChild(this, new ApplicationTenancyForItGraTa());

        executionContext.executeChild(this, new ApplicationTenancyForNl());
        executionContext.executeChild(this, new ApplicationTenancyForNlOther());
        executionContext.executeChild(this, new ApplicationTenancyForNlKal());
        executionContext.executeChild(this, new ApplicationTenancyForNlKalDefault());
        executionContext.executeChild(this, new ApplicationTenancyForNlKalTa());

        executionContext.executeChild(this, new ApplicationTenancyForSe());
        executionContext.executeChild(this, new ApplicationTenancyForSeOther());
        executionContext.executeChild(this, new ApplicationTenancyForSeHan());
        executionContext.executeChild(this, new ApplicationTenancyForSeHanTa());
        executionContext.executeChild(this, new ApplicationTenancyForSeHanDefault());

        executionContext.executeChild(this, new ApplicationTenancyForGb());
        executionContext.executeChild(this, new ApplicationTenancyForGbOther());
        executionContext.executeChild(this, new ApplicationTenancyForGbOxf());
        executionContext.executeChild(this, new ApplicationTenancyForGbOxfTa());
        executionContext.executeChild(this, new ApplicationTenancyForGbOxfDefault());
    }

}
