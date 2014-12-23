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
import org.estatio.dom.instance.EstatioPartition;
import org.estatio.dom.instance.EstatioPartitionFactory;
import org.estatio.dom.valuetypes.Hierarchy;

public abstract class AbstractEstatioPartitionFixtureScript extends FixtureScript {


    protected EstatioPartition create(
            final String name,
            final String path,
            final String countryReference,
            final ExecutionContext executionContext) {

        final Hierarchy node = Hierarchy.of(path);
        final Hierarchy parentNode = node != null? node.parent(): null;
        final ApplicationTenancy parentTenancy = applicationTenancies.findTenancyByPath(parentNode != null? parentNode.getPath(): null);

        final EstatioPartition estatioPartition = (EstatioPartition) applicationTenancies.newTenancy(name, path, parentTenancy);
        final Country country = countries.findCountry(countryReference);
        estatioPartition.setCountry(country);

        // make available
        this.estatioPartition = estatioPartition;
        executionContext.addResult(this, name, estatioPartition);
        return this.estatioPartition;
    }

    private EstatioPartition estatioPartition;

    /**
     * The partition created by this fixture
     */
    public EstatioPartition getEstatioPartition() {
        return estatioPartition;
    }


    @javax.inject.Inject
    protected ApplicationTenancies applicationTenancies;
    @javax.inject.Inject
    protected EstatioPartitionFactory estatioPartitionFactory;
    @javax.inject.Inject
    protected Countries countries;

}
