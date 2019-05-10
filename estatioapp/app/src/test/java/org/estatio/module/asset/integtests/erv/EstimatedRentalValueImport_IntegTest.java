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
package org.estatio.module.asset.integtests.erv;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.asset.dom.erv.EstimatedRentalValue;
import org.estatio.module.asset.dom.erv.EstimatedRentalValueRepository;
import org.estatio.module.asset.dom.erv.Type;
import org.estatio.module.asset.fixtures.erv.ERVImportXlsxFixture;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.asset.integtests.AssetModuleIntegTestAbstract;
import org.estatio.module.currency.fixtures.enums.Currency_enum;

public class EstimatedRentalValueImport_IntegTest extends AssetModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb);
                executionContext.executeChild(this, new ERVImportXlsxFixture());
            }
        });
    }

    @Test
    public void import_succeeded() throws Exception {

        final List<EstimatedRentalValue> allErvs = estimatedRentalValueRepository.listAll();
        Assertions.assertThat(allErvs).hasSize(3);

        Unit unit0001 = unitRepository.findUnitByReference("OXF-001");
        EstimatedRentalValue recentValueInternally = estimatedRentalValueRepository.findByUnitAndType(unit0001, Type.VALUED_INTERNALLY).get(0);
        EstimatedRentalValue valueValuer = estimatedRentalValueRepository.findByUnitAndType(unit0001, Type.VALUED_BY_VALUER).get(0);

        Assertions.assertThat(recentValueInternally.getDate()).isEqualTo(new LocalDate(2019,01,01));
        Assertions.assertThat(recentValueInternally.getValue()).isEqualTo(new BigDecimal("14814.68"));
        Assertions.assertThat(recentValueInternally.getCurrency()).isEqualTo(Currency_enum.EUR.findUsing(serviceRegistry2));

        Assertions.assertThat(valueValuer.getDate()).isEqualTo(new LocalDate(2019,04,01));
        Assertions.assertThat(valueValuer.getValue()).isEqualTo(new BigDecimal("2345.60"));
        Assertions.assertThat(valueValuer.getCurrency()).isEqualTo(Currency_enum.GBP.findUsing(serviceRegistry2));
    }

    @Inject EstimatedRentalValueRepository estimatedRentalValueRepository;

    @Inject UnitRepository unitRepository;

    @Inject ServiceRegistry2 serviceRegistry2;
    
}