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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotContributed.As;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;

@DomainService(menuOrder = "30", repositoryFor = FinancialAccountTransaction.class)
@Hidden
public class FinancialAccountTransactions extends EstatioDomainService<FinancialAccountTransaction> {

    public FinancialAccountTransactions() {
        super(FinancialAccountTransactions.class, FinancialAccountTransaction.class);
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
        final BigInteger sequence = nextSequenceFor(financialAccount, transactionDate);

        final FinancialAccountTransaction transaction = newTransientInstance(FinancialAccountTransaction.class);
        transaction.setFinancialAccount(financialAccount);
        transaction.setTransactionDate(transactionDate);
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setSequence(sequence);
        persistIfNotAlready(transaction);
        return transaction;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @Prototype
    @MemberOrder(sequence = "99")
    public List<FinancialAccountTransaction> allTransactions() {
        return allInstances();
    }

    // //////////////////////////////////////

    private BigInteger nextSequenceFor(final FinancialAccount financialAccount, final LocalDate transactionDate) {
        BigInteger sequence = BigInteger.ONE;
        do {
            if (findTransaction(financialAccount, transactionDate, sequence) == null) {
                return sequence;
            }
            sequence = sequence.add(BigInteger.ONE);
        } while (true);

    }

    // //////////////////////////////////////
    @ActionSemantics(Of.SAFE)
    @Prototype
    @MemberOrder(sequence = "99")
    public FinancialAccountTransaction findTransaction(
            final FinancialAccount financialAccount,
            final LocalDate transactionDate,
            final BigInteger sequence) {
        return uniqueMatch("findByFinancialAccountAndTransactionDateAndSequence",
                "financialAccount", financialAccount,
                "transactionDate", transactionDate,
                "sequence", sequence);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @Prototype
    @MemberOrder(sequence = "99")
    public List<FinancialAccountTransaction> findTransactions(
            final FinancialAccount financialAccount,
            final LocalDate transactionDate) {
        return allMatches("findByFinancialAccountAndTransactionDate",
                "financialAccount", financialAccount,
                "transactionDate", transactionDate);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "99")
    @NotContributed(As.ACTION)
    public List<FinancialAccountTransaction> transactions(
            final FinancialAccount financialAccount) {
        return allMatches("findByFinancialAccount", "financialAccount", financialAccount);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    public BigDecimal balance(FinancialAccount financialAccount) {
        BigDecimal balance = BigDecimal.ZERO;
        for (FinancialAccountTransaction transaction : transactions(financialAccount)) {
            balance = balance.add(transaction.getAmount());
        }
        return balance;
    }

}
