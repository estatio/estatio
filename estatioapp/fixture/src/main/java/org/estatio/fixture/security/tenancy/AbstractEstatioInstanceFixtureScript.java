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
import org.estatio.dom.geography.Country;
import org.estatio.dom.instance.EstatioInstance;
import org.estatio.dom.instance.EstatioInstances;
import org.estatio.dom.valuetypes.Hierarchy;

public abstract class AbstractEstatioInstanceFixtureScript extends FixtureScript {

    private EstatioInstance estatioInstance;

    /**
     * The instance created by this fixture
     */
    public EstatioInstance getEstatioInstance() {
        return estatioInstance;
    }

    protected EstatioInstance create(
            final String name,
            final String path,
            final String countryReference,
            final ExecutionContext executionContext) {

        final ApplicationTenancy applicationTenancy = applicationTenancies.newTenancy(name);
        final Country country = countries.findCountry(countryReference);

        final EstatioInstance parentInstance = estatioInstances.findEstatioInstanceByHierarchy(Hierarchy.of(path).parent());

        this.estatioInstance = estatioInstances.newInstance(path, applicationTenancy, country, parentInstance);
        executionContext.addResult(this, name, estatioInstance);
        executionContext.addResult(this, name, applicationTenancy);
        return estatioInstance;
    }

    @javax.inject.Inject
    protected ApplicationTenancies applicationTenancies;
    @javax.inject.Inject
    protected EstatioInstances estatioInstances;
    @javax.inject.Inject
    protected Countries countries;

}
