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

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.value.Blob;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.lease.app.SalesAreaMenu;
import org.estatio.module.lease.dom.occupancy.salesarea.SalesAreaLicense;
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

        final SalesAreaLicense licenseOxfTopModel = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2)
                .getOccupancies().first().getCurrentSalesAreaLicense();
        Assertions.assertThat(licenseOxfTopModel).isNotNull();
        Assertions.assertThat(licenseOxfTopModel.getSalesAreaNonFood()).isEqualTo(new BigDecimal("200.25"));
        Assertions.assertThat(licenseOxfTopModel.getNext()).isNull();

        final SalesAreaLicense licenseOxfMediaX = Lease_enum.OxfMediaX002Gb.findUsing(serviceRegistry2)
                .getOccupancies().first().getCurrentSalesAreaLicense();
        Assertions.assertThat(licenseOxfMediaX).isNotNull();
        Assertions.assertThat(licenseOxfMediaX.getSalesAreaNonFood()).isEqualTo(new BigDecimal("111.11"));

        Assertions.assertThat(Lease_enum.OxfMiracl005Gb.findUsing(serviceRegistry2).getOccupancies().first().getCurrentSalesAreaLicense()).isNull();

        // when
        salesAreaMenu.uploadSalesAreaLicences(excelSheet);

        // then
        // Topmodel: next license
        Assertions.assertThat(licenseOxfTopModel.getNext()).isNotNull();
        final SalesAreaLicense licenseOxfTopModelNext = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry2)
                .getOccupancies().first().getCurrentSalesAreaLicense();
        Assertions.assertThat(licenseOxfTopModel.getNext()).isEqualTo(licenseOxfTopModelNext);
        Assertions.assertThat(licenseOxfTopModelNext.getSalesAreaNonFood()).isEqualTo("201.33");
        Assertions.assertThat(licenseOxfTopModelNext.getStartDate()).isEqualTo(new LocalDate(2020,2,15));
        // and previous unchanged
        Assertions.assertThat(licenseOxfTopModel.getSalesAreaNonFood()).isEqualTo(new BigDecimal("200.25"));

        // MediaX: changed license
        Assertions.assertThat(licenseOxfMediaX.getSalesAreaNonFood()).isEqualTo(new BigDecimal("100.0"));
        Assertions.assertThat(licenseOxfMediaX.getSalesAreaFood()).isEqualTo(new BigDecimal("10.0"));
        Assertions.assertThat(licenseOxfMediaX.getFoodAndBeveragesArea()).isEqualTo(new BigDecimal("15.0"));

        // Miracl: new license
        final SalesAreaLicense licenseOxfMiracl = Lease_enum.OxfMiracl005Gb.findUsing(serviceRegistry2)
                .getOccupancies().first().getCurrentSalesAreaLicense();
        Assertions.assertThat(licenseOxfMiracl).isNotNull();
        Assertions.assertThat(licenseOxfMiracl.getStartDate()).isEqualTo(new LocalDate(2014,1,1));
        Assertions.assertThat(licenseOxfMiracl.getSalesAreaNonFood()).isNull();
        Assertions.assertThat(licenseOxfMiracl.getSalesAreaFood()).isEqualTo(new BigDecimal("22.0"));
        Assertions.assertThat(licenseOxfMiracl.getFoodAndBeveragesArea()).isEqualTo(new BigDecimal("33.0"));
        
    }

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    SalesAreaMenu salesAreaMenu;

}