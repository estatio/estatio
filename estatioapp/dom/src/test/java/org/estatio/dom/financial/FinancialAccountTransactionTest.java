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
package org.estatio.dom.financial;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import static org.assertj.core.api.Assertions.assertThat;

public class FinancialAccountTransactionTest {

    public static class ChangeTransactionDetails  {

        FinancialAccountTransaction transaction;
        BigDecimal amount;
        LocalDate date;
        String description;

        @Before
        public void setup(){

            transaction = new FinancialAccountTransaction();
            amount = new BigDecimal("123.45");
            date = new LocalDate(2016, 01, 01);
            description = "Some description";
            transaction.setAmount(amount);
            transaction.setTransactionDate(date);
            transaction.setDescription(description);

        }

        @Test
        public void changeTest() {

            // given
            assertThat(transaction.getAmount()).isEqualTo(amount);
            assertThat(transaction.getTransactionDate()).isEqualTo(date);
            assertThat(transaction.getDescription()).isEqualTo(description);

            // when
            BigDecimal updAmount = new BigDecimal("123.99");
            LocalDate updDate = new LocalDate(2016, 01, 02);
            String updDescription = "Some updated description";
            transaction.changeTransactionDetails(updAmount,updDate,updDescription );

            // then
            assertThat(transaction.getAmount()).isEqualTo(updAmount);
            assertThat(transaction.getTransactionDate()).isEqualTo(updDate);
            assertThat(transaction.getDescription()).isEqualTo(updDescription);

        }

    }

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final FinancialAccountTransaction pojo = new FinancialAccountTransaction();
            newPojoTester()
                    .withFixture(pojos(FinancialAccount.class))
                    .exercise(pojo);
        }

    }

}
