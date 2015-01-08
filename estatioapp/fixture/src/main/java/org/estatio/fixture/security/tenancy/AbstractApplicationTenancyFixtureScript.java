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

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.valuetypes.ApplicationTenancyLevel;

public abstract class AbstractApplicationTenancyFixtureScript extends FixtureScript {

    static public String ONLY = "_";

    protected ApplicationTenancy create(
            final String name,
            final String path,
            final ExecutionContext executionContext) {

        final ApplicationTenancyLevel node = ApplicationTenancyLevel.of(path);
        final ApplicationTenancyLevel parentNode = node != null? node.parent(): null;
        final ApplicationTenancy parentTenancy = applicationTenancies.findTenancyByPath(parentNode != null? parentNode.getPath(): null);

        final ApplicationTenancy applicationTenancy = applicationTenancies.newTenancy(name, path, parentTenancy);

        // make available
        this.applicationTenancy = applicationTenancy;
        executionContext.addResult(this, name, applicationTenancy);
        return this.applicationTenancy;
    }

    private ApplicationTenancy applicationTenancy;

    /**
     * The partition created by this fixture
     */
    public ApplicationTenancy getApplicationTenancy() {
        return applicationTenancy;
    }


    @javax.inject.Inject
    protected ApplicationTenancies applicationTenancies;
    @javax.inject.Inject
    protected Countries countries;

}
