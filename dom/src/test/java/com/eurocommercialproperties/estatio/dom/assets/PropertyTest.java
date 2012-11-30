package com.eurocommercialproperties.estatio.dom.assets;

import junit.framework.Assert;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;
import com.eurocommercialproperties.estatio.dom.asset.Properties;
import com.eurocommercialproperties.estatio.dom.asset.Property;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannelType;
import com.eurocommercialproperties.estatio.dom.communicationchannel.PostalAddress;

public class PropertyTest {

    private Property p ;
    
    @Mock
    Properties mockProperties;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {
        p =  new Property();
        PostalAddress a = new PostalAddress();
        a.setType(CommunicationChannelType.POSTAL_ADDRESS);
        a.setCity("Amsterdam");
        p.getCommunicationChannels().add(a);
    }

    @Test
    public void testGetCommunicationChannel() {
        PostalAddress a2 = (PostalAddress) p.getCommunicationChannel(CommunicationChannelType.POSTAL_ADDRESS);  
        Assert.assertTrue("Amsterdam", a2.getCity().equals("Amsterdam"));
    }
    

}
