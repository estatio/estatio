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

public class BankAccountAndMandateForTopModelGb extends BankAccountAndMandateAbstract {

//    public static final int SEQUENCE = 1;
//    public static final SequenceType SEQUENCE_TYPE = SequenceType.FIRST;
//    public static final Scheme SCHEME = Scheme.CORE;

    public BankAccountAndMandateForTopModelGb() {
        this(null, null);
    }

    public BankAccountAndMandateForTopModelGb(String friendlyName, String localName) {
        super(friendlyName, localName);
    }

    @Override
    protected void execute(ExecutionContext ec) {

        ec.executeChild(this, BankMandate_enum.OxfTopModel001Gb_1.builder());

//        // prereqs
//        ec.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
//        ec.executeChild(this, BankAccount_enum.TopModelGb.builder());
//
//        // exec
//        createBankMandate(
//                Lease_enum.OxfTopModel001Gb.getTenant_d().getRef(),
//                REF, SEQUENCE, SEQUENCE_TYPE, SCHEME,
//                ec);
    }

}
