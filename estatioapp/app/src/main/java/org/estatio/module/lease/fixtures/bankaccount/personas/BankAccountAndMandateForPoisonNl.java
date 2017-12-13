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

import org.estatio.module.lease.fixtures.bankaccount.enums.BankMandate_enum;

public class BankAccountAndMandateForPoisonNl extends BankAccountAndMandateAbstract {


    public BankAccountAndMandateForPoisonNl() {
        this(null, null);
    }

    public BankAccountAndMandateForPoisonNl(String friendlyName, String localName) {
        super(friendlyName, localName);
    }

    @Override
    protected void execute(ExecutionContext ec) {

        ec.executeChild(this, BankMandate_enum.KalPoison001Nl_2.builder());

//        // prereqs
//        ec.executeChild(this, Lease_enum.KalPoison001Nl.builder());
//        ec.executeChild(this, BankAccount_enum.PoisonNl.builder());
//
//        // exec
//        createBankMandate(Lease_enum.KalPoison001Nl.getTenant_d().getRef(), BANK_ACCOUNT_REF, SEQUENCE, SEQUENCE_TYPE, SCHEME, ec);
    }

}
