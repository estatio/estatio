/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.integtest.testing;

import static org.hamcrest.CoreMatchers.is;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.party.Party;
import org.junit.Assert;
import org.junit.Test;

public class PartyIntegrationTest extends AbstractEstatioIntegrationTest {

    @Test
    public void partyCanBeFound() throws Exception {
        Assert.assertNotNull(parties.findPartyByReferenceOrName("HELLOWORLD"));
    }

    @Test
    public void partyCanNotBeFound() throws Exception {
        Assert.assertNull(parties.findPartyByReferenceOrName("HELLO"));
    }

    @Test
    public void partyHasFourCommunicationChannels() throws Exception {
        Party party = parties.findPartyByReferenceOrName("HELLOWORLD");
        Assert.assertThat(party.getCommunicationChannels().size(), is(4));
    }

    @Test
    public void partyHasOneFinancialAccount() throws Exception {
        final Party party = parties.findPartyByReferenceOrName("HELLOWORLD");
        List<FinancialAccount> allAccounts = financialAccounts.allAccounts();
        List<FinancialAccount> partyAccounts = Lists.newArrayList(Iterables.filter(allAccounts, new Predicate<FinancialAccount>() {
            public boolean apply(FinancialAccount fa) {
                return fa.getOwner() == party;
            }
        }));
        Assert.assertThat(partyAccounts.size(), is(1));
    }

    @Test
    public void partyCanBeFoundOnPartialReference() {
        Assert.assertThat(parties.findParties("*LLOWOR*").size(), is(1));
    }

    @Test
    public void partyCanBeFoundOnPartialName1() {
        Assert.assertThat(parties.findParties("*ello Wor*").size(), is(1));
    }

    @Test
    public void partyCanBeFoundOnPartialName2() {
        Assert.assertThat(parties.findParties("Doe, Jo*").size(), is(1));
    }

    @Test
    public void partyCanBeFoundCaseInsensitive() {
        Assert.assertThat(parties.findParties("*OE, jO*").size(), is(1));
    }

}
