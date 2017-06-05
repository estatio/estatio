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

package org.estatio.dom.financial.bankaccount;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.financial.FinancialAccountRepository;
import org.estatio.dom.financial.FinancialAccountType;
import org.estatio.dom.party.Party;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = BankAccount.class)
public class BankAccountRepository extends UdoDomainRepositoryAndFactory<BankAccount> {

    public BankAccountRepository() {
        super(BankAccountRepository.class, BankAccount.class);
    }

    @Programmatic
    public BankAccount newBankAccount(
            final Party owner,
            final String iban,
            final String bic) {
        final BankAccount bankAccount = newTransientInstance(BankAccount.class);
        bankAccount.setOwner(owner);
        bankAccount.setReference(iban);
        bankAccount.setName(iban);
        bankAccount.setIban(iban);
        bankAccount.setBic(bic);
        bankAccount.refresh();
        persistIfNotAlready(bankAccount);
        return bankAccount;
    }

    @Programmatic
    public List<BankAccount> findBankAccountsByOwner(final Party party) {
        return Lists.newArrayList(
                Iterables.filter(financialAccountRepository.findAccountsByTypeOwner(FinancialAccountType.BANK_ACCOUNT, party),
                        BankAccount.class));
    }

    @Programmatic
    public BankAccount findBankAccountByReference(final Party owner, final String reference) {
        return (BankAccount) financialAccountRepository.findByOwnerAndReference(owner, reference);
    }

    @Programmatic
    public List<BankAccount> allBankAccounts() {
        return allInstances();
    }

    @Programmatic
    public List<BankAccount> findByReference(final String reference) {
        return allMatches("findByReference",
                "reference", reference);
    }

    @Inject
    private FinancialAccountRepository financialAccountRepository;
}
