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

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.factory.FactoryService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.party.dom.Party;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = FinancialAccount.class)
public class FinancialAccountRepository extends UdoDomainRepositoryAndFactory<FinancialAccount> {

    public FinancialAccountRepository() {
        super(FinancialAccountRepository.class, FinancialAccount.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public FinancialAccount newFinancialAccount(
            final FinancialAccountType financialAccountType,
            final String reference,
            final String name,
            final Party owner) {
        FinancialAccount financialAccount = financialAccountType.create(factoryService);
        financialAccount.setReference(reference);
        financialAccount.setName(name);
        financialAccount.setOwner(owner);
        persistIfNotAlready(financialAccount);
        return financialAccount;
    }

    // //////////////////////////////////////

    @Programmatic
    public FinancialAccount findByOwnerAndReference(final Party owner, final String reference) {
        return firstMatch("findByOwnerAndReference", "owner", owner, "reference", reference);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<FinancialAccount> findAccountsByOwner(final Party party) {
        return allMatches("findByOwner", "owner", party);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<FinancialAccount> findAccountsByTypeOwner(final FinancialAccountType accountType, final Party party) {
        return allMatches("findByTypeAndOwner",
                "type", accountType,
                "owner", party);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<FinancialAccount> allAccounts() {
        return allInstances();
    }

    @Inject
    FactoryService factoryService;

}
