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

import com.google.common.collect.Lists;

import org.apache.log4j.Level;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.apache.isis.objectstore.jdo.datanucleus.DataNucleusPersistenceMechanismInstaller;
import org.apache.isis.objectstore.jdo.datanucleus.IsisConfigurationForJdoIntegTests;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.app.EstatioAppManifest;

public class EstatioIntegTestBuilder extends IsisSystemForTest.Builder {

    public EstatioIntegTestBuilder() {

        // no need to add, because each test will set up its own test fixtures
        // anyway.
        withLoggingAt(Level.DEBUG);
        with(new IsisConfigurationForJdoIntegTests());
        with(new DataNucleusPersistenceMechanismInstaller());

        with(new EstatioAppManifestForIntegTests());
    }


    public static class EstatioAppManifestForIntegTests extends EstatioAppManifest {
        @Override
        public List<Class<?>> getAdditionalServices() {
            List<Class<?>> additionalServices = Lists.newArrayList();
            appendEstatioClockService(additionalServices);
            appendOptionalServicesForSecurityModule(additionalServices);
             appendServicesForAddonsWithServicesThatAreCurrentlyMissingModules(additionalServices);
            return additionalServices;
        }
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
}
