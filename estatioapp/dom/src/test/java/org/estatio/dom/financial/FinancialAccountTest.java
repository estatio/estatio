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

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

import static org.assertj.core.api.Assertions.assertThat;

public class FinancialAccountTest {

    public static class CompareTo extends ComparableContractTest_compareTo<FinancialAccount> {

        @SuppressWarnings("unchecked")
        @Override
        protected List<List<FinancialAccount>> orderedTuples() {
            return listOf(
                    listOf(
                            newFinancialAccount(null, null),
                            newFinancialAccount(null, "ABC"),
                            newFinancialAccount(null, "ABC"),
                            newFinancialAccount(null, "DEF")
                    ),
                    listOf(
                            newFinancialAccount(FinancialAccountType.BANK_ACCOUNT, null),
                            newFinancialAccount(FinancialAccountType.BANK_ACCOUNT, "ABC"),
                            newFinancialAccount(FinancialAccountType.BANK_ACCOUNT, "ABC"),
                            newFinancialAccount(FinancialAccountType.BANK_ACCOUNT, "DEF")
                    )
            );
        }

        private FinancialAccount newFinancialAccount(
                FinancialAccountType type, String reference) {
            final FinancialAccount fa = new FinancialAccount() {
            };
            fa.setType(type);
            fa.setReference(reference);
            return fa;
        }

    }

    public static class ChangeName {

        FinancialAccount financialAccount;
        String name;

        @Before
        public void setup(){

            financialAccount = new FinancialAccount();
            name = "some name";
            financialAccount.setName(name);

        }

        @Test
        public void changeName(){

            // given
            assertThat(financialAccount.getName()).isEqualTo(name);
            // when
            financialAccount.changeName("some other name");
            // then
            assertThat(financialAccount.getName()).isEqualTo("some other name");


        }

    }

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final FinancialAccount pojo = new FinancialAccount();
            newPojoTester()
                    .withFixture(pojos(Party.class, PartyForTesting.class))
                    .exercise(pojo);
        }

    }
}