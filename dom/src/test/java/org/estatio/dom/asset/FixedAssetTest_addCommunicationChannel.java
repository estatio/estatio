package org.estatio.dom.asset;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.CommunicationChannels;

public class FixedAssetTest_addCommunicationChannel {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private CommunicationChannels mockCommunicationChannels;

    @Mock
    private DomainObjectContainer mockContainer;
    
    private FixedAsset fixedAsset;
    
    
    @Before
    public void setUp() throws Exception {
        fixedAsset = new FixedAssetForTesting();
        fixedAsset.setContainer(mockContainer);
    }
    
    // behaviour not fully specified; see comments in code.
    @Ignore 
    @Test
    public void test() {
        final CommunicationChannel commChannel = fixedAsset.addCommunicationChannel(CommunicationChannelType.EMAIL_ADDRESS);
    }

}
