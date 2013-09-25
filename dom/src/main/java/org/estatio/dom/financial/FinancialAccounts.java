/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.StringUtils;

@Named("Accounts")
public class FinancialAccounts extends EstatioDomainService<FinancialAccount> {

    public FinancialAccounts() {
        super(FinancialAccounts.class, FinancialAccount.class);
    }

    // //////////////////////////////////////

    @Hidden
    public BankAccount newBankAccount(final @Named("Owner") Party owner, final @Named("IBAN") String iban) {
        final BankAccount bankAccount = newTransientInstance(BankAccount.class);
        bankAccount.setIBAN(iban);
        bankAccount.setName(iban);
        bankAccount.setReference(iban);
        persistIfNotAlready(bankAccount);
        bankAccount.setOwner(owner);
        return bankAccount;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public FinancialAccount findAccountByReference(final @Named("Reference") String reference) {
        String regex = StringUtils.wildcardToRegex(reference);
        return firstMatch("findByReference", "reference", regex);
    }
    
    // //////////////////////////////////////
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Hidden
    public List<BankAccount> findBankAccountsByParty(final Party party) {
        return (List)allMatches("findByTypeAndParty", "type", FinancialAccountType.BANK_ACCOUNT, "owner", party);
    }

    // //////////////////////////////////////
    
    @ActionSemantics(Of.SAFE)
    @Prototype
    @MemberOrder(sequence = "99")
    public List<FinancialAccount> allAccounts() {
        return allInstances();
    }

    
}
