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
package org.estatio.fixture.financial;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccountRepository;
import org.estatio.dom.financial.FinancialAccountTransaction;
import org.estatio.dom.financial.FinancialAccountTransactionRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;

public abstract class FinancialAccountTransactionAbstract extends FixtureScript {

    protected FinancialAccountTransactionAbstract(String friendlyName, String localName) {
        super(friendlyName, localName);
    }

    protected FinancialAccountTransaction createFinancialAccountTransaction(String partyStr, LocalDate date, BigDecimal amount, ExecutionContext executionContext) {
        Party party = partyRepository.findPartyByReference(partyStr);
        FinancialAccount financialAccount = financialAccountRepository.findAccountsByOwner(party).get(0);

        FinancialAccountTransaction financialAccountTransaction = financialAccountTransactionRepository.newTransaction(
                financialAccount,
                date,
                "Fixture transaction",
                amount);
        executionContext.addResult(this, financialAccountTransaction);

        return financialAccountTransaction;
    }

    // //////////////////////////////////////

    @Inject
    private PartyRepository partyRepository;

    @Inject
    private FinancialAccountRepository financialAccountRepository;

    @Inject
    private FinancialAccountTransactionRepository financialAccountTransactionRepository;

}
