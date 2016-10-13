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

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.FatalException;

import org.estatio.dom.PowerType;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.utils.StringUtils;

public enum FinancialAccountType implements PowerType<FinancialAccount> {

    BANK_ACCOUNT(BankAccount.class),
    BANK_GUARANTEE(FinancialAccount.class),
    GUARANTEE_DEPOSIT(FinancialAccount.class);
    
    private final Class<? extends FinancialAccount> clss;

    private FinancialAccountType(final Class<? extends FinancialAccount> clss) {
        this.clss = clss;
    }

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    // //////////////////////////////////////

    public FinancialAccount create(final DomainObjectContainer container) {
        try {
            FinancialAccount account = container.newTransientInstance(clss);
            account.setType(this);
            return account;
        } catch (Exception ex) {
            throw new FatalException(ex);
        }
    }

    public static class Meta {
        private Meta(){}

        public final static int MAX_LEN = 30;
    }

}
