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

package org.estatio.module.financial.app;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.estatio.module.party.dom.Person;

public class BankAccountMenu_Test {

    public static class NewBankAccount extends BankAccountMenu_Test {

        Person owner;
        String error;
        BankAccountMenu bankAccountMenu;

        @Before
        public void setUp() throws Exception {
            bankAccountMenu = new BankAccountMenu();
        }

        @Test
        public void happyCase() throws Exception {
            // given
            owner = new Person();

            // when
            error = bankAccountMenu.validateNewBankAccount(owner, "NL07CJSS6089084611", "BIC");

            // then
            Assertions.assertThat(error).isNull();
        }

        @Test
        public void sadCase() throws Exception {
            // given
            owner = new Person();

            // when
            error = bankAccountMenu.validateNewBankAccount(owner, "Invalid iban", "BIC");

            // then
            Assertions.assertThat(error).isEqualTo("Not a valid IBAN number");
        }
    }
}
