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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(repositoryFor = FinancialAccountTransaction.class, nature = NatureOfService.DOMAIN)
public class FinancialAccountTransactionRepository extends UdoDomainRepositoryAndFactory<FinancialAccountTransaction> {

    public FinancialAccountTransactionRepository() {
        super(FinancialAccountTransactionRepository.class, FinancialAccountTransaction.class);
    }

    @Override
    public String iconName() {
        return "FinancialAccount";
    }

    // //////////////////////////////////////

    @Programmatic
    public FinancialAccountTransaction newTransaction(
            final FinancialAccount financialAccount,
            final LocalDate transactionDate,
            final String description,
            final BigDecimal amount
    ) {

        final FinancialAccountTransaction transaction = newTransientInstance(FinancialAccountTransaction.class);
        transaction.setFinancialAccount(financialAccount);
        transaction.setTransactionDate(transactionDate);
        transaction.setDescription(description);
        transaction.setAmount(amount);
        persistIfNotAlready(transaction);
        return transaction;
    }

    // //////////////////////////////////////

    @Programmatic
    public List<FinancialAccountTransaction> allTransactions() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public FinancialAccountTransaction findTransaction(
            final FinancialAccount financialAccount,
            final LocalDate transactionDate,
            final BigInteger sequence) {
        return firstMatch("findByFinancialAccountAndTransactionDateAndSequence",
                "financialAccount", financialAccount,
                "transactionDate", transactionDate,
                "sequence", sequence);
    }

    @Programmatic
    public FinancialAccountTransaction findTransaction(
            final FinancialAccount financialAccount,
            final LocalDate transactionDate) {
        return firstMatch("findByFinancialAccountAndTransactionDate",
                "financialAccount", financialAccount,
                "transactionDate", transactionDate);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<FinancialAccountTransaction> transactions(
            final FinancialAccount financialAccount) {
        return allMatches("findByFinancialAccount", "financialAccount", financialAccount);
    }

    // //////////////////////////////////////

    @Programmatic
    public BigDecimal balance(FinancialAccount financialAccount) {
        BigDecimal balance = BigDecimal.ZERO;
        for (FinancialAccountTransaction transaction : transactions(financialAccount)) {
            balance = balance.add(transaction.getAmount());
        }
        return balance;
    }

}
