/*
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.integspecs.glue;

import cucumber.api.java.After;
import cucumber.api.java.Before;

import org.jmock.Expectations;

import org.apache.isis.core.specsupport.scenarios.InMemoryDB;
import org.apache.isis.core.specsupport.scenarios.ScenarioExecutionScope;
import org.apache.isis.core.specsupport.specs.CukeGlueAbstract;

import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.PartyForTesting;

public class BootstrappingForUnitScopeGlue extends CukeGlueAbstract {

    @Before(value={"@unit"}, order=100)
    public void beforeScenarioUnitScope() {
        before(ScenarioExecutionScope.UNIT);
    }
    
    @Before("@unit")
    public void unitFixtures() throws Throwable {
        final InMemoryDB inMemoryDB = new InMemoryDBForEstatio(this.scenarioExecution());
        checking(new Expectations() {
            {
                allowing(service(Leases.class)).findLeaseByReference(with(any(String.class)));
                will(inMemoryDB.finds(Lease.class));
                
                allowing(service(Parties.class)).matchPartyByReferenceOrName(with(any(String.class)));
                will(inMemoryDB.finds(PartyForTesting.class));
                
                allowing(service(AgreementRoleTypes.class)).findByTitle(with(any(String.class)));
                will(inMemoryDB.finds(AgreementRoleType.class));
            }
        });
    }

    @After("@unit")
    public void afterScenario(cucumber.api.Scenario sc) {
        assertMocksSatisfied();
        after(sc);
    }


}
