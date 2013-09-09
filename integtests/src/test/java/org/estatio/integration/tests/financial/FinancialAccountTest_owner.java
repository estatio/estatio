/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.integration.tests.financial;

import static org.hamcrest.CoreMatchers.is;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;

public class FinancialAccountTest_owner extends EstatioIntegrationTest {

    private Parties parties;
    private FinancialAccounts financialAccounts;
    private Party party;

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioTransactionalObjectsFixture());
    }

    @Before
    public void setUp() throws Exception {
        parties = service(Parties.class);
        party = parties.findPartyByReference("HELLOWORLD");
        
        financialAccounts = service(FinancialAccounts.class);
    }
    
    @Test
    public void accounts() throws Exception {
        List<FinancialAccount> allAccounts = financialAccounts.allAccounts();
        List<FinancialAccount> partyAccounts = Lists.newArrayList(Iterables.filter(allAccounts, new Predicate<FinancialAccount>() {
            public boolean apply(FinancialAccount fa) {
                return fa.getOwner() == party;
            }
        }));
        Assert.assertThat(partyAccounts.size(), is(1));
    }

}
