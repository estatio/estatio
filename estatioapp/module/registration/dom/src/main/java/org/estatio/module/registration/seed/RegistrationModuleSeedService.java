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

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.asset.dom.registration.FixedAssetRegistrationType;
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
        private final Class<?> implementationClass;

        public boolean matches(final FixedAssetRegistrationType fixedAssetRegistrationType) {
            return Objects.equals(fixedAssetRegistrationType.getTitle(), title);
        }
    }

    static class SeedFixedAssetRegistrationType extends FixtureScript {

        @Override
        protected void execute(final ExecutionContext executionContext) {
            for (FixedAssetRegistrationType_data datum : FixedAssetRegistrationType_data.values()) {
                repositoryService.allInstances(FixedAssetRegistrationType.class).stream()
                        .filter(datum::matches)
                        .forEach(fart -> fart.setFullyQualifiedClassName(datum.implementationClass.getName()));
            }
        }

        @Inject
        RepositoryService repository;
    }
}

