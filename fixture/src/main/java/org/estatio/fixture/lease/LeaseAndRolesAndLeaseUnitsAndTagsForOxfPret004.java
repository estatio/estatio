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

public class LeaseAndRolesAndLeaseUnitsAndTagsForOxfPret004 extends LeaseAndRolesAndLeaseUnitsAndTagsAbstract {

    @Override
    protected void execute(ExecutionContext executionContext) {
        Party manager = parties.findPartyByReference("JDOE");
        createLease(
                "OXF-PRET-004", "Pret lease",
                "OXF-004", "Pret", "FASHION", "ALL", null, null,
                ld(2011, 7, 1), ld(2015, 6, 30), false, false, manager,
                executionContext);
    }

}
