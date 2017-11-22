/*
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
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
package org.estatio.module.application.seed;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.classification.dom.impl.applicability.Applicability;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.lease.dom.occupancy.Occupancy;

import lombok.AllArgsConstructor;
import lombok.Getter;

@DomainService(nature = NatureOfService.DOMAIN, menuOrder = "99")
public class ClassificationApplicabilitySeedService {

    @PostConstruct
    public void init() {

        if(System.getProperty("isis.integTest") != null) {
            return;
        }

        fixtureScripts.runFixtureScript(new SeedApplicabilityDomainType(), null);
    }

    @Inject
    FixtureScripts fixtureScripts;

    @AllArgsConstructor
    @Getter
    enum Applicability_data {
        FRENCH_PROPERTY_CODE("CNCCFR_PROPERTYCODE", "/FRA", Property.class),
        FRENCH_PROPERTY_TYPE("CNCCFR_PROPERTYTYPE", "/FRA", Property.class),
        FRENCH_SECTOR("CNCCFR_SECTOR", "/FRA", Occupancy.class),
        ITALIAN_SECTOR("CNCCIT_SECTOR", "/ITA", Occupancy.class);

        private final String taxonomyReference;
        private final String atPath;
        private final Class<?> appliesTo;

        public boolean matches(final Applicability applicability) {
            return Objects.equals(applicability.getTaxonomy().getReference(), taxonomyReference) &&
                   Objects.equals(applicability.getAtPath(), atPath);
        }
    }

    static class SeedApplicabilityDomainType extends FixtureScript {

        @Override
        protected void execute(final ExecutionContext executionContext) {
            for (Applicability_data datum : Applicability_data.values()) {
                repositoryService.allInstances(Applicability.class).stream()
                        .filter(datum::matches)
                        .forEach(applicability -> applicability.setDomainType(datum.appliesTo.getName()));
            }
        }

        @Inject
        RepositoryService repositoryService;
    }
}

