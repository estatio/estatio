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

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.financial.dom.FinancialAccountTransactionRepository;
import org.estatio.module.guarantee.dom.GuaranteeRepository;

// REVIEW: this has been refactored to a mixin, but previously it wasn't annotated as @DomainService, so this is still not actually in use.
public class FinancialAccount_newAdjustmentUNUSED {

    private final FinancialAccount financialAccount;

    public FinancialAccount_newAdjustmentUNUSED(final FinancialAccount financialAccount) {
        this.financialAccount = financialAccount;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public void newAdjustment(
            final LocalDate transactionDate,
            final String description,
            final BigDecimal amount
    ) {
        financialAccountTransactionRepository.newTransaction(
                financialAccount,
                transactionDate,
                description,
                amount);
    }

    public boolean hideNewAdjustment() {
        // don't show if there is no guarantee pointing back to financialAccount
        return guaranteeRepository.findbyFinancialAccount(financialAccount) == null;
    }

    @Inject
    FinancialAccountTransactionRepository financialAccountTransactionRepository;

    @Inject
    GuaranteeRepository guaranteeRepository;

}
