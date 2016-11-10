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

import org.junit.Before;
import org.junit.Test;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.estatio.dom.asset.Property;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.utils.IBANValidator;
import org.incode.module.country.dom.impl.Country;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BankAccountTest {

    BankAccount account;

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(Country.class))
                    .withFixture(pojos(Party.class, PartyForTesting.class))
                    .withFixture(pojos(Property.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new BankAccount());
        }
    }

    public static class CheckAccount extends BankAccountTest {

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

        @Test
        public void happyCase2() {
            account.setCountry(country);
            account.setNationalBankCode("ABNA");
            account.setAccountNumber("0580744434");
            account.verifyIban();
            assertThat(account.getIban()).isEqualTo("NL31ABNA0580744434");
        }

    }

    public static class Change extends BankAccountTest {

        @Test
        public void happyCase() throws Exception {
            // Given
            BankAccount bankAccount = new BankAccount();
            final String iban = "NL91RABO0145814734";
            assertTrue(IBANValidator.valid(iban));
            assertNull(bankAccount.validateChange(iban, null, null));
            // When
            bankAccount.change(iban, "BIC", "EXT");
            // Then
            assertThat(bankAccount.getIban()).isEqualTo(iban);
            assertThat(bankAccount.getBic()).isEqualTo("BIC");
            assertThat(bankAccount.getExternalReference()).isEqualTo("EXT");
        }
    }

    public static class VerifyIban extends BankAccountTest {

        private Country country;

        @Before
        public void setup() {
            country = new Country();
            country.setAlpha2Code("IT");
            country.setReference("ITA");
        }

        /**
         * See also {@link IBANHelperTest}
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