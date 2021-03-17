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
package org.estatio.module.financial.dom;

import org.junit.Before;
import org.junit.Test;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.module.financial.dom.utils.IBANHelper_Test;
import org.estatio.module.financial.dom.utils.IBANValidator;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyForTesting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BankAccount_Test {

    BankAccount account;

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(Country.class))
                    .withFixture(pojos(Party.class, PartyForTesting.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new BankAccount());
        }
    }

    public static class CheckAccount extends BankAccount_Test {

        private Country country;

        @Before
        public void setUp() throws Exception {
            account = new BankAccount();
            country = new Country("NLD", "NL", "Netherlands");
        }

        @Test
        public void happyCase() {
            account.setIban("NL31ABNA0580744434");
            account.verifyIban();
            assertThat(account.getAccountNumber()).isEqualTo("0580744434");
        }

    }

    public static class Change extends BankAccount_Test {

        @Test
        public void happyCase() throws Exception {
            // Given
            BankAccount bankAccount = new BankAccount();
            final String iban = "NL91RABO0145814734";
            assertTrue(IBANValidator.valid(iban));
            assertNull(bankAccount.validate0ChangeIban(iban));
            // When
            bankAccount.changeIban(iban);
            // Then
            assertThat(bankAccount.getIban()).isEqualTo(iban);
        }
    }

    public static class VerifyIban extends BankAccount_Test {

        private Country country;

        @Before
        public void setup() {
            country = new Country();
            country.setAlpha2Code("IT");
            country.setReference("ITA");
        }

        /**
         * See also {@link IBANHelper_Test}
         */
        @Test
        public void wrongInput() {

            BankAccount ba = new BankAccount();
            ba.setCountry(country);
            ba.setNationalBankCode("07074");
            ba.setBranchCode("36140");
            ba.setAccountNumber("500");
            ba.setNationalCheckCode(null);
            ba.verifyIban();
            assertNull(ba.getIban());
        }

    }

}