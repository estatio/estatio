/*
 *
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
package org.estatio.module.lease.integtests.imports;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.value.Blob;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.lease.app.SalesAreaMenu;
import org.estatio.module.lease.fixtures.imports.SalesAreaLicencesImportFixture;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

public class SalesAreaLicencesImport_IntegTest extends LeaseModuleIntegTestAbstract {

    List<FixtureResult> fixtureResults;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new SalesAreaLicencesImportFixture());
                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb);
                executionContext.executeChild(this, Lease_enum.OxfPoison003Gb);
                executionContext.executeChild(this, Lease_enum.OxfMediaX002Gb);
                executionContext.executeChild(this, Lease_enum.OxfMiracl005Gb);
                fixtureResults = executionContext.getResults();
            }
        });
    }

    Property oxf;

    @Test
    public void import_lines_test() throws Exception {

        // given
        oxf = Property_enum.OxfGb.findUsing(serviceRegistry2);
        Blob excelSheet = (Blob) fixtureResults.get(0).getObject();
        Assertions.assertThat(Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2).getOccupancies().first().getCurrentSalesAreaLicense()).isNotNull();
        Assertions.assertThat(Lease_enum.OxfMediaX002Gb.findUsing(serviceRegistry2).getOccupancies().first().getCurrentSalesAreaLicense()).isNotNull();

        // when
        salesAreaMenu.uploadSalesAreaLicences(excelSheet);

        // then
        // TODO: finish ....

        
    }

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    SalesAreaMenu salesAreaMenu;



}