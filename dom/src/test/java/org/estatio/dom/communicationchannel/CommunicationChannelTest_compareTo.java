package org.estatio.dom.communicationchannel;

import java.util.List;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;


public class CommunicationChannelTest_compareTo extends ComparableContractTest_compareTo<CommunicationChannel> {

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<CommunicationChannel>> orderedTuples() {
        
        // the CCT enum is not in alphabetical order, as you can see
        return listOf(
                listOf(
                        newCommunicationChannel(null),
                        newCommunicationChannel(CommunicationChannelType.ACCOUNTING_POSTAL_ADDRESS),
                        newCommunicationChannel(CommunicationChannelType.ACCOUNTING_POSTAL_ADDRESS),
                        newCommunicationChannel(CommunicationChannelType.FAX_NUMBER)
                    ),
                listOf(
                    newCommunicationChannel(CommunicationChannelType.ACCOUNTING_POSTAL_ADDRESS),
                    newCommunicationChannel(CommunicationChannelType.POSTAL_ADDRESS),
                    newCommunicationChannel(CommunicationChannelType.POSTAL_ADDRESS),
                    newCommunicationChannel(CommunicationChannelType.ACCOUNTING_EMAIL_ADDRESS)
                   )
            );
    }

    private CommunicationChannel newCommunicationChannel(CommunicationChannelType type) {
        final CommunicationChannel cc = new CommunicationChannel(){
            public String getName() {
                return null;
            }};
        cc.setType(type);
        return cc;
    }

}
