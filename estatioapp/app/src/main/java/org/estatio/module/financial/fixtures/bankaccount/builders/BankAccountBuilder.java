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
package org.estatio.module.financial.fixtures.bankaccount.builders;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.party.dom.Party;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"party", "iban"}, callSuper = false)
@Accessors(chain = true)
public final class BankAccountBuilder extends BuilderScriptAbstract<BankAccount, BankAccountBuilder> {


    @Getter @Setter
    Party party;

    @Getter @Setter
    String iban;

    /**
     * Optional.
     */
    @Getter @Setter
    String bic;

    @Getter
    BankAccount object;

    @Override
    protected void doExecute(final ExecutionContext executionContext) {

        checkParam("party", executionContext, Party.class);
        checkParam("iban", executionContext, Party.class);

        this.object = bankAccountRepository.newBankAccount(party, iban, bic);
        executionContext.addResult(this, object.getReference(), object);
    }

    @Inject
    BankAccountRepository bankAccountRepository;

}
