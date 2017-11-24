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
package org.estatio.module.registration.seed;

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

import org.estatio.module.asset.dom.registration.FixedAssetRegistration;
import org.estatio.module.asset.dom.registration.FixedAssetRegistrationType;
import org.estatio.module.asset.dom.registration.FixedAssetRegistrationTypeRepository;
import org.estatio.module.registration.dom.LandRegister;

import lombok.AllArgsConstructor;
import lombok.Getter;

@DomainService(nature = NatureOfService.DOMAIN, menuOrder = "99")
public class RegistrationModuleSeedService {

    @PostConstruct
    public void init() {

        if(System.getProperty("isis.integTest") != null) {
            return;
        }

        fixtureScripts.runFixtureScript(new SeedFixedAssetRegistrationType(), null);
    }

    @Inject
    FixtureScripts fixtureScripts;

    @AllArgsConstructor
    @Getter
    enum FixedAssetRegistrationType_data {
        LAND_REGISTER("LandRegister", LandRegister.class);

        private final String title;
        private final Class<? extends FixedAssetRegistration> implementationClass;

        public boolean matches(final FixedAssetRegistrationType fixedAssetRegistrationType) {
            return Objects.equals(fixedAssetRegistrationType.getTitle(), title);
        }

        public void upsertUsing(final ServiceRegistry2 serviceRegistry2) {
            final FixedAssetRegistrationTypeRepository repository =
                    serviceRegistry2.lookupService(FixedAssetRegistrationTypeRepository.class);
            final RepositoryService  repositoryService =
                    serviceRegistry2.lookupService(RepositoryService.class);

            final List<FixedAssetRegistrationType> types = repositoryService.allInstances(FixedAssetRegistrationType.class).stream()
                    .filter(this::matches)
                    .collect(Collectors.toList());
            switch (types.size()) {
            case 0:
                repository.create(title, implementationClass);
                break;
            case 1:
                final FixedAssetRegistrationType fart = types.get(0);
                fart.setFullyQualifiedClassName(implementationClass.getName());
                break;
            default:
                throw new IllegalArgumentException("Found " + types.size() + " matching " + this);
            }
        }

    }

    static class SeedFixedAssetRegistrationType extends FixtureScript {

        @Override
        protected void execute(final ExecutionContext executionContext) {
            Arrays.stream(FixedAssetRegistrationType_data.values()).forEach(datum -> datum.upsertUsing(serviceRegistry));
        }

    }
}

