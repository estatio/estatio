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
package org.estatio.integtests.financial;

import java.util.List;
import javax.inject.Inject;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertiesAndUnitsForKal;
import org.estatio.fixture.asset.PropertiesAndUnitsForOxf;
import org.estatio.fixture.financial.BankAccountsAndMandatesForAll;
import org.estatio.fixture.lease.LeasesEtcForAll;
import org.estatio.fixture.party.*;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;

import static org.hamcrest.CoreMatchers.is;

public class FinancialAccountTest_owner extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new CompositeFixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);

                // execute("parties", new PersonsAndOrganisationsAndCommunicationChannelsForAll(), executionContext);
                execute(new OrganisationAndCommunicationChannelsForAcme(), executionContext);
                execute(new OrganisationAndCommunicationChannelsForHelloWorld(), executionContext);
                execute(new OrganisationAndCommunicationChannelsForTopModel(), executionContext);
                execute(new OrganisationAndCommunicationChannelsForMediaX(), executionContext);
                execute(new OrganisationAndCommunicationChannelsForPoison(), executionContext);
                execute(new OrganisationAndCommunicationChannelsForPret(), executionContext);
                execute(new OrganisationAndCommunicationChannelsForMiracle(), executionContext);
                execute(new PersonForJohnDoe(), executionContext);
                execute(new PersonForLinusTorvalds(), executionContext);

                // execute("properties", new PropertiesAndUnitsForAll(), executionContext);
                execute(new PropertiesAndUnitsForOxf(), executionContext);
                execute(new PropertiesAndUnitsForKal(), executionContext);

                execute("leases", new LeasesEtcForAll(), executionContext);
                execute("bank-accounts", new BankAccountsAndMandatesForAll(), executionContext);
            }
        });
    }

    @Inject
    private Parties parties;
    @Inject
    private FinancialAccounts financialAccounts;

    private Party party;

    @Before
    public void setUp() throws Exception {
        party = parties.findPartyByReference("HELLOWORLD");
    }

    // this test really just makes an assertion about the fixture.
    @Test
    public void atLeastOneAccountIsOwnedByParty() throws Exception {

        // given
        List<FinancialAccount> allAccounts = financialAccounts.allAccounts();

        // when
        List<FinancialAccount> partyAccounts = Lists.newArrayList(Iterables.filter(allAccounts, new Predicate<FinancialAccount>() {
            public boolean apply(FinancialAccount fa) {
                return fa.getOwner() == party;
            }
        }));

        // then
        Assert.assertThat(partyAccounts.size(), is(1));
    }

}
