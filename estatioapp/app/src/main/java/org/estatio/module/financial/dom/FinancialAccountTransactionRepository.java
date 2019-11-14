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

import org.apache.isis.applib.query.QueryDefault;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;

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

        final FinancialAccountTransaction transaction = factoryService.instantiate(FinancialAccountTransaction.class);
        transaction.setFinancialAccount(financialAccount);
        transaction.setTransactionDate(transactionDate);
        transaction.setDescription(description);
        transaction.setAmount(amount);
        repositoryService.persistAndFlush(transaction);
        return transaction;
    }

    // //////////////////////////////////////

    @Programmatic
    public List<FinancialAccountTransaction> allTransactions() {
        return repositoryService.allInstances(FinancialAccountTransaction.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public FinancialAccountTransaction findTransaction(
            final FinancialAccount financialAccount,
            final LocalDate transactionDate,
            final BigInteger sequence) {
        List<FinancialAccountTransaction> list = repositoryService.allMatches(new QueryDefault<>(FinancialAccountTransaction.class,
                "findByFinancialAccountAndTransactionDateAndSequence",
                "financialAccount", financialAccount,
                "transactionDate", transactionDate,
                "sequence", sequence));
        return list.isEmpty() ? null : list.get(0);
    }

    @Programmatic
    public FinancialAccountTransaction findTransaction(
            final FinancialAccount financialAccount,
            final LocalDate transactionDate) {
        List<FinancialAccountTransaction> list = repositoryService.allMatches(new QueryDefault<>(FinancialAccountTransaction.class,
                "findByFinancialAccountAndTransactionDate",
                "financialAccount", financialAccount,
                "transactionDate", transactionDate));
        return list.isEmpty() ? null : list.get(0);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<FinancialAccountTransaction> transactions(
            final FinancialAccount financialAccount) {
        return repositoryService.allMatches(new QueryDefault<>(FinancialAccountTransaction.class,
                "findByFinancialAccount", "financialAccount", financialAccount));
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
