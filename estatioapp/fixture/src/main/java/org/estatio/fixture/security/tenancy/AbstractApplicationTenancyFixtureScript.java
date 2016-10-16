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

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.country.dom.impl.CountryRepository;
import org.estatio.dom.valuetypes.ApplicationTenancyLevel;

public abstract class AbstractApplicationTenancyFixtureScript extends FixtureScript {

    public static final String ONLY = "_";

    protected ApplicationTenancy create(
            final String name,
            final String path,
            final ExecutionContext executionContext) {

        final ApplicationTenancyLevel node = ApplicationTenancyLevel.of(path);
        final ApplicationTenancyLevel parentNode = node != null ? node.parent() : null;
        final ApplicationTenancy parentTenancy = applicationTenancyRepository.findByPath(parentNode != null ? parentNode.getPath() : null);

        ApplicationTenancy existingApplicationTenancy = applicationTenancyRepository.findByPath(path);
        if (existingApplicationTenancy != null) {
            executionContext.addResult(this, name, existingApplicationTenancy);
            return existingApplicationTenancy;
        } else {
            final ApplicationTenancy applicationTenancy = applicationTenancyRepository.newTenancy(name, path, parentTenancy);
            executionContext.addResult(this, name, applicationTenancy);
            return applicationTenancy;
        }
    }

    @javax.inject.Inject
    protected ApplicationTenancyRepository applicationTenancyRepository;
    @javax.inject.Inject
    protected CountryRepository countryRepository;

}
