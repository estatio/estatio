/*
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.integtests;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import org.apache.log4j.Level;

import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.apache.isis.core.security.authentication.AuthenticationRequestNameOnly;

import org.estatio.app.EstatioAppManifest;
import org.estatio.integtests.fakes.EstatioIntegTestFakeServicesModule;
import org.estatio.integtests.fakes.FakeEmailService;
import org.estatio.integtests.fakes.FakeLookupLocationService;

/**
 * Holds an instance of an {@link IsisSystemForTest} as a {@link ThreadLocal} on
 * the current thread, initialized with Estatio's domain services and with
 * {@link org.estatio.fixture.EstatioBaseLineFixture reference data fixture}.
 */
public class EstatioSystemInitializer {

    private EstatioSystemInitializer() {
    }

    public static IsisSystemForTest initIsft() {
        IsisSystemForTest isft = IsisSystemForTest.getElseNull();
        if (isft == null) {
            isft = new IsisSystemForTest.Builder()
                    .withLoggingAt(Level.DEBUG)
                    .with(new AuthenticationRequestNameOnly("estatio-admin"))
                    .with(new EstatioAppManifest() {

                        @Override public List<Class<?>> getModules() {
                            final List<Class<?>> modules = super.getModules();
                            modules.add(EstatioIntegTestFakeServicesModule.class);
                            return modules;
                        }

                        @Override
                        public Map<String, String> getConfigurationProperties() {
                            Map<String, String> props = super.getConfigurationProperties();
                            Util.withIsisIntegTestProperties(props);
                            Util.withJavaxJdoRunInMemoryProperties(props);
                            Util.withDataNucleusProperties(props);
                            return props;
                        }
                        @Override
                        public List<Class<?>> getAdditionalServices() {
                            List<Class<?>> additionalServices = Lists.newArrayList();
                            appendEstatioCalendarService(additionalServices);
                            appendOptionalServicesForSecurityModule(additionalServices);
                            return additionalServices;
                        }
                    })
                    .build()
                    .setUpSystem();
            IsisSystemForTest.set(isft);
        }
        return isft;
    }

}