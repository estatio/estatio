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
import org.apache.log4j.Level;
import org.isisaddons.module.excel.dom.ExcelService;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.apache.isis.objectstore.jdo.applib.service.exceprecog.ExceptionRecognizerCompositeForJdoObjectStore;
import org.apache.isis.objectstore.jdo.datanucleus.DataNucleusPersistenceMechanismInstaller;
import org.apache.isis.objectstore.jdo.datanucleus.IsisConfigurationForJdoIntegTests;

public class EstatioIntegTestBuilder extends IsisSystemForTest.Builder {

    public EstatioIntegTestBuilder() {

        // no need to add, because each test will set up its own test fixtures
        // anyway.
        withLoggingAt(Level.DEBUG);
        with(testConfiguration());
        with(new DataNucleusPersistenceMechanismInstaller());

        withServicesIn(
                "org.estatio",
                "org.isisaddons"
        );

        withServices(
                new ExceptionRecognizerCompositeForJdoObjectStore());
    }

    private static IsisConfiguration testConfiguration() {
        final IsisConfigurationForJdoIntegTests testConfiguration = new IsisConfigurationForJdoIntegTests();
        testConfiguration.addRegisterEntitiesPackagePrefix("org.estatio");

        // uncomment to use log4jdbc instead
        // testConfiguration.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionDriverName",
        // "net.sf.log4jdbc.DriverSpy");

        // testConfiguration.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionURL",
        // "jdbc:hsqldb:mem:test;sqllog=3");

        //
        // testConfiguration.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionURL",
        // "jdbc:sqlserver://localhost:1433;instance=.;databaseName=estatio");
        // testConfiguration.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionDriverName",
        // "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        // testConfiguration.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionUserName",
        // "estatio");
        // testConfiguration.put("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionPassword",
        // "estatio");

        return testConfiguration;
    }

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
