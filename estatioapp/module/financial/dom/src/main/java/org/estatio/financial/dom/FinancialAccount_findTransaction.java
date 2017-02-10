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

package org.estatio.financial.dom;

import java.math.BigInteger;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

@Mixin
public class FinancialAccount_findTransaction {

    private final FinancialAccount financialAccount;

    public FinancialAccount_findTransaction(final FinancialAccount financialAccount) {
        this.financialAccount = financialAccount;
    }

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    public FinancialAccountTransaction $$(
            @ParameterLayout(named = "Transaction date")
            final LocalDate transactionDate,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Sequence")
            final BigInteger sequence) {
        if(sequence == null) {
            return financialAccountTransactionRepository.findTransaction(financialAccount, transactionDate);
        } else {
            return financialAccountTransactionRepository.findTransaction(financialAccount, transactionDate, sequence);
        }
    }

    @Inject
    FinancialAccountTransactionRepository financialAccountTransactionRepository;
}
