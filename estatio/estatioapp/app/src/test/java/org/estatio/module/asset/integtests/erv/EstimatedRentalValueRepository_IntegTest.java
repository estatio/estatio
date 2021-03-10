/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.asset.dom.erv.EstimatedRentalValue;
import org.estatio.module.asset.dom.erv.EstimatedRentalValueRepository;
import org.estatio.module.asset.dom.erv.Type;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.asset.integtests.AssetModuleIntegTestAbstract;

public class EstimatedRentalValueRepository_IntegTest extends AssetModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChildren(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb);
            }
        });

    }

    @Test
    public void upsert_works() throws Exception {

        // given
        Property oxf = Property_enum.OxfGb.findUsing(serviceRegistry);
        Unit unit = unitRepository.findByProperty(oxf).get(0);
        final LocalDate date = new LocalDate(2019, 01, 01);
        final BigDecimal value = new BigDecimal("123.45");
        final Type type = Type.VALUED_INTERNALLY;
        Assertions.assertThat(estimatedRentalValueRepository.listAll()).isEmpty();

        // when
        EstimatedRentalValue erv = estimatedRentalValueRepository.upsert(
                unit,
                date,
                type,
                value
        );

        // then
        Assertions.assertThat(estimatedRentalValueRepository.listAll()).hasSize(1);
        EstimatedRentalValue erv1 = estimatedRentalValueRepository.findUnique(unit, date, type);
        Assertions.assertThat(erv1).isSameAs(erv);

        // and when
        final BigDecimal newValue = new BigDecimal("1234.56");
        estimatedRentalValueRepository.upsert(
                unit,
                date,
                type,
                newValue);
        // then
        Assertions.assertThat(estimatedRentalValueRepository.listAll()).hasSize(1);
        Assertions.assertThat(erv.getValue()).isEqualTo(newValue);

    }

    @Inject EstimatedRentalValueRepository estimatedRentalValueRepository;

    @Inject UnitRepository unitRepository;


}
