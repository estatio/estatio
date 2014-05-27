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

public class LeaseForOxfMiracl005 extends LeaseAbstract {

    public static final String LEASE_REFERENCE = "OXF-MIRACL-005";
    public static final String UNIT_REFERENCE = "OXF-005";

    @Override
    protected void execute(ExecutionContext executionContext) {
        Party manager = parties.findPartyByReference("JDOE");
        createLease(
                LEASE_REFERENCE, "Miracle lease",
                UNIT_REFERENCE, "Miracle", "FASHION", "ALL", "HELLOWORLD", "MIRACLE",
                ld(2013, 11, 7), ld(2023, 11, 6), false, true, manager,
                executionContext);
    }

}
