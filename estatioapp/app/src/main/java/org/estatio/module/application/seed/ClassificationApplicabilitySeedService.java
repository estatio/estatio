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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.util.Enums;

import org.incode.module.classification.dom.impl.applicability.Applicability;
import org.incode.module.classification.dom.impl.category.CategoryRepository;
import org.incode.module.classification.dom.impl.category.taxonomy.Taxonomy;

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

        public void upsertUsing(final ServiceRegistry2 serviceRegistry2) {
            final RepositoryService repositoryService =
                    serviceRegistry2.lookupService(RepositoryService.class);

            final List<Applicability> types = repositoryService.allInstances(Applicability.class).stream()
                    .filter(this::matches)
                    .collect(Collectors.toList());

            final Applicability applicability;
            switch (types.size()) {
            case 0:
                final CategoryRepository categoryRepository =
                        serviceRegistry2.lookupService(CategoryRepository.class);

                Taxonomy taxonomy = (Taxonomy) categoryRepository.findByReference(taxonomyReference);
                if(taxonomy == null) {
                    final String taxonomyName =
                            Enums.getFriendlyNameOf(this.taxonomyReference);
                    taxonomy = categoryRepository.createTaxonomy(taxonomyName);
                    taxonomy.setReference(this.taxonomyReference);
                }
                applicability = new Applicability(taxonomy, atPath, appliesTo.getName());
                repositoryService.persistAndFlush(applicability);
                break;
            case 1:
                applicability = types.get(0);
                applicability.setDomainType(appliesTo.getName());
                break;
            default:
                throw new IllegalArgumentException("Found " + types.size() + " matching " + this);
            }
        }

    }

    static class SeedApplicabilityDomainType extends FixtureScript {
        @Override
        protected void execute(final ExecutionContext executionContext) {
            Arrays.stream(Applicability_data.values())
                    .forEach(datum -> datum.upsertUsing(serviceRegistry));
        }
    }
}

