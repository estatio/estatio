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

import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.utils.IBANHelper;
import org.estatio.dom.geography.Country;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;

public class IBANHelperTest {

    public static class VerifyAndUpdate extends IBANHelperTest {

        private Country country;

        @Before
        public void setup() {
            country = new Country();
            country.setAlpha2Code("IT");
            country.setReference("ITA");
        }

        @Test
        public void testDutchAccount() {
            BankAccount ba = new BankAccount();
            ba.setIban("NL31ABNA0580744434");
            IBANHelper.verifyAndUpdate(ba);
            assertThat(ba.getNationalBankCode()).isEqualTo("ABNA");
            assertThat(ba.getAccountNumber()).isEqualTo("0580744434");
        }

        @Test
        public void testItalianAccount() {
            BankAccount ba = new BankAccount();
            ba.setIban("IT69N0347501601000051986922");
            IBANHelper.verifyAndUpdate(ba);
            assertThat(ba.getNationalBankCode()).isEqualTo("03475");
            assertThat(ba.getBranchCode()).isEqualTo("01601");
            assertThat(ba.getAccountNumber()).isEqualTo("000051986922");
        }

        @Test
        public void testEmptyAccount() {
            BankAccount ba = new BankAccount();
            IBANHelper.verifyAndUpdate(ba);
            assertNull(ba.getNationalBankCode());
            assertNull(ba.getBranchCode());
            assertNull(ba.getAccountNumber());
        }

        @Test
        public void testFalseAccount() {
            BankAccount ba = new BankAccount();
            ba.setIban("IT1231231");
            IBANHelper.verifyAndUpdate(ba);
            assertNull(ba.getNationalBankCode());
            assertNull(ba.getBranchCode());
            assertNull(ba.getAccountNumber());
        }

        @Test
        public void testCreateIBANfromFields() {
            BankAccount ba = new BankAccount();
            ba.setCountry(country);
            ba.setNationalBankCode("03475");
            ba.setBranchCode("01601");
            ba.setAccountNumber("000051986922");
            ba.setNationalCheckCode("N");
            IBANHelper.verifyAndUpdate(ba);
            assertThat(ba.getIban()).isEqualTo("IT69N0347501601000051986922");
        }


    }
}