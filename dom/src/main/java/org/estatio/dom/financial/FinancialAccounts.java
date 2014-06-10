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

import java.util.List;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.financial.utils.IBANValidator;
import org.estatio.dom.party.Party;

@DomainService(menuOrder = "30", repositoryFor = FinancialAccount.class)
@Named("Accounts")
public class FinancialAccounts extends EstatioDomainService<FinancialAccount> {

    public FinancialAccounts() {
        super(FinancialAccounts.class, FinancialAccount.class);
    }

    @Override
    public String iconName() {
        return "FinancialAccount";
    }
    
    // //////////////////////////////////////

    @Programmatic
    public FinancialAccount newFinancialAccount(
            final FinancialAccountType financialAccountType,
            final String reference,
            final String name,
            final Party owner) {
        FinancialAccount financialAccount = financialAccountType.create(getContainer());
        financialAccount.setReference(reference);
        financialAccount.setName(name);
        financialAccount.setOwner(owner);
        return financialAccount;
    }

    @Programmatic
    public BankAccount newBankAccount(
            final @Named("Owner") Party owner,
            final @Named("Reference") String reference,
            final @Named("Name") String name) {
        final BankAccount bankAccount = newTransientInstance(BankAccount.class);
        bankAccount.setReference(reference);
        bankAccount.setName(name);
        persistIfNotAlready(bankAccount);
        bankAccount.setOwner(owner);
        return bankAccount;
    }

    @NotContributed
    public BankAccount newBankAccount(
            final @Named("Owner") Party owner,
            final @Named("IBAN") @TypicalLength(JdoColumnLength.BankAccount.IBAN) String iban) {
        final BankAccount bankAccount = newTransientInstance(BankAccount.class);
        bankAccount.setReference(iban);
        bankAccount.setName(iban);
        bankAccount.setIban(iban);
        persistIfNotAlready(bankAccount);
        bankAccount.setOwner(owner);
        return bankAccount;
    }

    public String validateNewBankAccount(
            final Party owner,
            final String iban) {
        if (!IBANValidator.valid(iban)) {
            return "Not a valid IBAN number";
        }
        return null;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public FinancialAccount findAccountByReference(final @Named("Reference") String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    // //////////////////////////////////////

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Programmatic
    public List<BankAccount> findBankAccountsByOwner(final Party party) {
        return (List) allMatches("findByTypeAndOwner",
                "type", FinancialAccountType.BANK_ACCOUNT,
                "owner", party);
    }

    // //////////////////////////////////////

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Programmatic
    public List<FinancialAccount> findAccountsByOwner(final Party party) {
        return (List) allMatches("findByOwner", "owner", party);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @Prototype
    @MemberOrder(sequence = "99")
    public List<FinancialAccount> allAccounts() {
        return allInstances();
    }

}
