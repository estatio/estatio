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
package org.estatio.module.guarantee.contributions;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainService;
import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.financial.dom.FinancialAccountRepository;
import org.estatio.module.financial.dom.FinancialAccountTransaction;
import org.estatio.module.financial.dom.FinancialAccountTransactionRepository;
import org.estatio.module.guarantee.dom.Guarantee;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class GuaranteeFinancialAccountTransactionService
        extends UdoDomainService<GuaranteeFinancialAccountTransactionService> {

    public GuaranteeFinancialAccountTransactionService() {
        super(GuaranteeFinancialAccountTransactionService.class);
    }


    @Programmatic
    public List<FinancialAccountTransaction> transactions(Guarantee guarantee) {
        return financialAccountTransactionRepository.transactions(guarantee.getFinancialAccount());
    }


    // //////////////////////////////////////

    @Programmatic
    public Guarantee newTransaction(
            final Guarantee guarantee,
            final LocalDate transactionDate,
            final String description,
            final BigDecimal amount) {

        financialAccountTransactionRepository.newTransaction(guarantee.getFinancialAccount(), transactionDate, description, amount);
        return guarantee;
    }

    @Programmatic
    public boolean hideNewTransaction(final Guarantee guarantee) {
        return guarantee.getFinancialAccount() == null;
    }

    // //////////////////////////////////////


    @Programmatic
    public BigDecimal balance(FinancialAccount financialAccount) {
        BigDecimal balance = financialAccountTransactionRepository.balance(financialAccount);
        return balance;
    }

    // //////////////////////////////////////


    @Inject
    private FinancialAccountRepository financialAccountRepository;

    @Inject
    private FinancialAccountTransactionRepository financialAccountTransactionRepository;

}
