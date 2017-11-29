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
package org.estatio.module.asset.integtests.asset;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.asset.fixtures.property.personas.PropertyAndUnitsAndOwnerAndManagerForKalNl;
import org.estatio.module.asset.fixtures.property.personas.PropertyAndUnitsAndOwnerAndManagerForOxfGb;
import org.estatio.module.asset.integtests.AssetModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UnitRepository_IntegTest extends AssetModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {

                executionContext.executeChild(this, new PropertyAndUnitsAndOwnerAndManagerForOxfGb());
                executionContext.executeChild(this, new PropertyAndUnitsAndOwnerAndManagerForKalNl());
            }
        });
    }

    @Inject
    UnitRepository unitRepository;

    @Inject
    PropertyRepository propertyRepository;

    public static class FindUnitByReference extends UnitRepository_IntegTest {

        @Test
        public void findByReference() throws Exception {
            final Unit unit = unitRepository.findUnitByReference(
                    Property_enum.OxfGb.unitRef("001"));
            // then
            Assert.assertEquals("OXF-001", unit.getReference());
        }

        @Test
        public void findByReferenceOrName() throws Exception {
            // given
            assertThat(unitRepository.findUnits("*XF*", false).size(), is(25));

            // when
            Unit unit = unitRepository.findUnitByReference(Property_enum.OxfGb.unitRef("001"));
            unit.setEndDate(new LocalDate(2014, 1, 1));

            // then
            assertThat(unitRepository.findUnits("*XF*", false).size(), is(24));
            assertThat(unitRepository.findUnits("*XF*", true).size(), is(25));
        }

    }

    public static class FindByPropertyAndActiveOnDate extends UnitRepository_IntegTest {

        @Test
        public void findActiveByProperty() throws Exception {
            // given
            Property propertyForOxf = Property_enum.OxfGb.findUsing(serviceRegistry);

            // when
            Unit unit = unitRepository.findUnitByReference(Property_enum.OxfGb.getRef() + "-001");
            LocalDate startDate = new LocalDate(2013, 1, 1);
            LocalDate endDate = new LocalDate(2013, 12, 31);
            unit.setEndDate(endDate);
            unit.setStartDate(startDate);

            // then
            assertThat(unitRepository.findByPropertyAndActiveOnDate(propertyForOxf, startDate).size(), is(25));
            assertThat(unitRepository.findByPropertyAndActiveOnDate(propertyForOxf, startDate.minusDays(1)).size(), is(24));
            assertThat(unitRepository.findByPropertyAndActiveOnDate(propertyForOxf, endDate).size(), is(25));
            assertThat(unitRepository.findByPropertyAndActiveOnDate(propertyForOxf, endDate.plusDays(1)).size(), is(24));
        }
    }

    public static class FindByProperty extends UnitRepository_IntegTest {

        @Test
        public void findActiveByProperty() throws Exception {
            // given, when
            Property propertyForOxf = propertyRepository.findPropertyByReference(
                    Property_enum.OxfGb.getRef());
            // then
            assertThat(unitRepository.findByProperty(propertyForOxf).size(), is(25));
        }
    }
}
