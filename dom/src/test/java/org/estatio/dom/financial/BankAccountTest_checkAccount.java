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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.geography.Country;

public class BankAccountTest_checkAccount {

    private BankAccount account;
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
        assertThat(account.getAccountNumber(), is("0580744434"));
    }

    @Test
    public void happyCase2() {
        account.setCountry(country);
        account.setNationalBankCode("ABNA");
        account.setAccountNumber("0580744434");
        account.verifyIban();
        assertThat(account.getIban(), is("NL31ABNA0580744434"));
    }

}
