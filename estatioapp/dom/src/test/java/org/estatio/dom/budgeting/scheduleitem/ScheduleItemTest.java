/*
 * Copyright 2015 Yodo Int. Projects and Consultancy
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.estatio.dom.budgeting.scheduleitem;

import java.math.BigDecimal;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.PropertyForTesting;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.budgeting.budgetitem.BudgetItemForTesting;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTableForTesting;
import org.estatio.dom.budgeting.schedule.Schedule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by jodo on 22/04/15.
 */
public class ScheduleItemTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final ScheduleItem pojo = new ScheduleItem();
            newPojoTester()
                    .withFixture(pojos(Property.class, PropertyForTesting.class))
                    .withFixture(pojos(KeyTable.class, KeyTableForTesting.class))
                    .withFixture(pojos(Schedule.class, Schedule.class))
                    .withFixture(pojos(BudgetItem.class, BudgetItemForTesting.class))
                    .exercise(pojo);
        }

    }

    public static class UpdatePercentage extends ScheduleItemTest {

        ScheduleItem scheduleItem = new ScheduleItem();

        @Test
        public void validate() {

            //given
            scheduleItem.setPercentage(new BigDecimal(100));

            //when then
            assertThat(scheduleItem.validateUpdatePercentage(new BigDecimal(100.01)), is("percentage should be in range 0 - 100"));
            assertThat(scheduleItem.validateUpdatePercentage(new BigDecimal(-0.01)), is("percentage should be in range 0 - 100"));
            assertThat(scheduleItem.validateUpdatePercentage(new BigDecimal(100)), is(nullValue()));
            assertThat(scheduleItem.validateUpdatePercentage(new BigDecimal(0)), is(nullValue()));
        }

    }

}
