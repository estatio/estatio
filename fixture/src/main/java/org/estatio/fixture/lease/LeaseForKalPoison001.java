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
package org.estatio.fixture.lease;

import org.estatio.dom.party.Party;

import static org.estatio.integtests.VT.ld;

public class LeaseForKalPoison001 extends LeaseAbstract {

    public static final String LEASE_REFERENCE = "KAL-POISON-001";
    public static final String UNIT_REFERENCE = "KAL-001";

    @Override
    protected void execute(ExecutionContext executionContext) {
        Party manager = parties.findPartyByReference("JDOE");
        createLease(
                LEASE_REFERENCE, "Poison Amsterdam",
                UNIT_REFERENCE, "Poison", "HEALT&BEAUTY", "PERFUMERIE", "ACME", "POISON",
                ld(2011, 1, 1), ld(2020, 12, 31), true, true, manager,
                executionContext);
    }

}
