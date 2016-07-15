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
package org.estatio.dom.guarantee.contributed;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccountTransactions;
import org.estatio.dom.guarantee.Guarantees;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.math.BigDecimal;

public class GuaranteedFinancialAccountContributions {

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public void newAdjustment(
            final FinancialAccount financialAccount, // contributee
            final LocalDate transactionDate,
            final String description,
            final BigDecimal amount
            ) {
        financialAccountTransactions.newTransaction(
                financialAccount,
                transactionDate,
                description,
                amount);
    }

    public boolean hideNewAdjustment(
            final FinancialAccount financialAccount // contributee
    ) {
        // don't show if there is no guarantee pointing back to financialAccount
        return guarantees.findbyFinancialAccount(financialAccount) == null;
    }

    @Inject
    FinancialAccountTransactions financialAccountTransactions;

    @Inject
    Guarantees guarantees;

}
