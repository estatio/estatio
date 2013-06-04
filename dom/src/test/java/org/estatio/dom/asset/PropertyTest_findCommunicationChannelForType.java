package org.estatio.dom.asset;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import junit.framework.Assert;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.EmailAddress;
import org.estatio.dom.communicationchannel.PostalAddress;

public class PropertyTest_findCommunicationChannelForType {

    private Property p ;
    
    @Mock
    Properties mockProperties;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    private PostalAddress pa;

    private EmailAddress ea;

    @Before
    public void setup() {
        p =  new Property();
        
        pa = new PostalAddress();
        pa.setType(CommunicationChannelType.POSTAL_ADDRESS);
        pa.setCity("Amsterdam");
        
        ea = new EmailAddress();
        ea.setType(CommunicationChannelType.EMAIL_ADDRESS);
        ea.setAddress("joe@bloggs.com");
    }

    @Test
    public void happyCase() {
        p.getCommunicationChannels().add(pa);
        p.getCommunicationChannels().add(ea);

        PostalAddress pa = (PostalAddress) p.findCommunicationChannelForType(CommunicationChannelType.POSTAL_ADDRESS);
        assertThat(pa.getCity(), is("Amsterdam"));

        EmailAddress ea = (EmailAddress) p.findCommunicationChannelForType(CommunicationChannelType.EMAIL_ADDRESS);
        assertThat(ea.getAddress(), is("joe@bloggs.com"));
    }

    @Test
    public void noneAvailable() {

        PostalAddress pa = (PostalAddress) p.findCommunicationChannelForType(CommunicationChannelType.POSTAL_ADDRESS);
        assertThat(pa, is(nullValue()));
    }


}
