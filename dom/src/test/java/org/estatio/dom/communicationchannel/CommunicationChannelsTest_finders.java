package org.estatio.dom.communicationchannel;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;

public class CommunicationChannelsTest_finders {

    private FinderInteraction finderInteraction;

    private CommunicationChannels communicationChannels;

    private CommunicationChannelType type;

    @Before
    public void setup() {
        
        type = CommunicationChannelType.EMAIL_ADDRESS;
        
        communicationChannels = new CommunicationChannels() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }
            @Override
            protected List<CommunicationChannel> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }
            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };
    }

    
    @Test
    public void findByReferenceAndType() {

        communicationChannels.findByReferenceAndType("REF-1", type);
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(CommunicationChannel.class));
        assertThat(finderInteraction.getQueryName(), is("findByReferenceAndType"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("reference"), is((Object)"REF-1"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("type"), is((Object)type));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(2));
    }

}
