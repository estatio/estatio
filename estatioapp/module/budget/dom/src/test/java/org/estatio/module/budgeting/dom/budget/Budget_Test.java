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

package org.estatio.module.budgeting.dom.budget;

import org.joda.time.LocalDate;
import org.junit.Test;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.dom.asset.Property;

import static org.assertj.core.api.Assertions.assertThat;

public class Budget_Test {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final Budget pojo = new Budget();
            newPojoTester()
                    .withFixture(pojos(Property.class, Property.class))
                    .exercise(pojo);
        }

    }

    public static class TitleTest extends Budget_Test {

        @Test
        public void title() {
            // given
            final Property property = new Property();
            property.setName("Property");
            property.setReference("PROP");

            final Budget budget = new Budget();
            budget.setProperty(property);
            LocalDate startDate = new LocalDate(2015, 1, 1);
            LocalDate endDate = new LocalDate(2016, 1, 1);
            budget.setStartDate(startDate);
            budget.setEndDate(endDate);

            // when
            String budgetTitle = budget.title();

            // then
            assertThat(budgetTitle).isEqualTo(
                    property.getClass().getSimpleName()
                            + " [" + property.getReference() + "]"
                            + " > " + budget.getBudgetYear());
        }
    }

}
