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
package org.estatio.module.lease.fixtures.bankaccount.personas;

import org.estatio.module.assetfinancial.fixtures.bankaccountfafa.enums.BankAccountFaFa_enum;
import org.estatio.module.assetfinancial.fixtures.bankaccountfafa.personas.BankAccountAndFaFaForPoisonNl;
import org.estatio.module.lease.fixtures.lease.LeaseForKalPoison001Nl;
import org.estatio.module.bankmandate.dom.Scheme;
import org.estatio.module.bankmandate.dom.SequenceType;

public class BankAccountAndMandateForPoisonNl extends BankAccountAndMandateAbstract {

    public static final String BANK_ACCOUNT_REF = BankAccountFaFa_enum.PoisonNl.getBankAccount_d().getIban();
    public static final int SEQUENCE = 2;
    public static final SequenceType SEQUENCE_TYPE = SequenceType.FIRST;
    public static final Scheme SCHEME = Scheme.CORE;

    public BankAccountAndMandateForPoisonNl() {
        this(null, null);
    }

    public BankAccountAndMandateForPoisonNl(String friendlyName, String localName) {
        super(friendlyName, localName);
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new LeaseForKalPoison001Nl());
        executionContext.executeChild(this, new BankAccountAndFaFaForPoisonNl());

        // exec
        createBankMandate(LeaseForKalPoison001Nl.PARTY_REF_TENANT, BANK_ACCOUNT_REF, SEQUENCE, SEQUENCE_TYPE, SCHEME, executionContext);
    }

}
