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
package org.estatio.dom.asset;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelForTesting;
import org.estatio.dom.communicationchannel.EmailAddress;
import org.estatio.dom.communicationchannel.PhoneNumber;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class FixedAssetTest_communicationChannels {

    private FixedAsset fixedAsset;

    private CommunicationChannel cc1;
    private CommunicationChannel cc2;
    
    @Before
    public void setUp() throws Exception {
        cc1 = new CommunicationChannelForTesting();
        cc1.setDescription("A");
        cc2 = new CommunicationChannelForTesting();
        cc2.setDescription("B");
        
        fixedAsset = new FixedAssetForTesting();
    }
    
    @Test
    public void addTo_whenDoesNotContain() {
        assertThat(fixedAsset.getCommunicationChannels(), Matchers.empty());
        fixedAsset.addToCommunicationChannels(cc1);
        assertThat(fixedAsset.getCommunicationChannels(), Matchers.contains(cc1));
    }
    
    @Test
    public void addTo_whenDoesNotContain_canAddAnother() {
        // given
        fixedAsset.addToCommunicationChannels(cc1);
        // when
        fixedAsset.addToCommunicationChannels(cc2);
        assertThat(fixedAsset.getCommunicationChannels(), Matchers.contains(cc1, cc2));
    }
    
    @Test
    public void addRole_whenContains() {
        // given
        fixedAsset.addToCommunicationChannels(cc1);
        // when
        fixedAsset.addToCommunicationChannels(cc1);

        // then
        assertThat(fixedAsset.getCommunicationChannels(), Matchers.contains(cc1));
        assertThat(fixedAsset.getCommunicationChannels().size(), is(1));
    }

    @Test
    public void addRole_whenNull() {
        // when
        fixedAsset.addToCommunicationChannels(null);
        
        // then
        assertThat(fixedAsset.getCommunicationChannels(), Matchers.empty());
    }
    
    
    @Test
    public void removeRole_whenContains() {
        // given
        fixedAsset.addToCommunicationChannels(cc1);

        // when
        fixedAsset.removeFromCommunicationChannels(cc1);
        
        // then
        assertThat(fixedAsset.getCommunicationChannels(), Matchers.empty());
    }
    
    @Test
    public void removeRole_whenDoesNotContain() {
        
        // when
        fixedAsset.removeFromCommunicationChannels(cc1);
        
        // then
        assertThat(fixedAsset.getCommunicationChannels(), Matchers.empty());
    }
    
    
    @Test
    public void removeRole_whenDoesNull() {
        
        // when
        fixedAsset.removeFromCommunicationChannels(null);
        
        // then
        assertThat(fixedAsset.getCommunicationChannels(), Matchers.empty());
    }
    
}
