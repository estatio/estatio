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

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.apache.isis.core.security.authentication.AuthenticationRequestNameOnly;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.wicket.gmap3.cpt.applib.Location;
import org.isisaddons.wicket.gmap3.cpt.service.LocationLookupService;

import org.estatio.app.EstatioAppManifest;

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
                            appendServicesForAddonsWithServicesThatAreCurrentlyMissingModules(additionalServices);
                            additionalServices.add(FakeLookupLocationService.class);
                            return additionalServices;
                        }
                    })
                    .build()
                    .setUpSystem();
            IsisSystemForTest.set(isft);
        }
        return isft;
    }

    // REVIEW: may not need anymore since appManifest refactoring...
    @DomainService
    public static class FakeExcelService extends ExcelService {
        public String getId() {
            return getClass().getName();
        }
        @Override
        public <T> Blob toExcel(List<T> domainObjects, Class<T> cls, String fileName) throws Exception {
            return null;
        }
        @Override
        public <T> List<T> fromExcel(Blob excelBlob, Class<T> cls) throws Exception {
            return null;
        }
    }

    @DomainService(nature = NatureOfService.DOMAIN, menuOrder = "1")
    public static class FakeLookupLocationService extends LocationLookupService {
        public String getId() {
            return getClass().getName();
        }
        @Override
        public Location lookup(final String description) {
            return null;
        }
    }

}