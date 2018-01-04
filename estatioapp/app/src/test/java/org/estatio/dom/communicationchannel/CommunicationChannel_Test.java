package org.estatio.dom.communicationchannel;

import java.util.List;

import org.junit.Test;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwner;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.unittestsupport.dom.bean.PojoTester;

public class CommunicationChannel_Test {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(CommunicationChannelOwner.class, CommunicationChannelOwnerForTesting.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new CommunicationChannelForTesting(), PojoTester.FilterSet.excluding("owner"));
        }
    }

    public static class CompareTo extends ComparableContractTest_compareTo<CommunicationChannel> {

        @SuppressWarnings("unchecked")
        @Override
        protected List<List<CommunicationChannel>> orderedTuples() {

            // the CCT enum is not in alphabetical order, as you can see
            return listOf(
                    listOf(
                            newCommunicationChannel(null),
                            newCommunicationChannel(CommunicationChannelType.POSTAL_ADDRESS),
                            newCommunicationChannel(CommunicationChannelType.POSTAL_ADDRESS),
                            newCommunicationChannel(CommunicationChannelType.FAX_NUMBER)
                    ),
                    listOf(
                            newCommunicationChannel(null),
                            newCommunicationChannel(CommunicationChannelType.POSTAL_ADDRESS),
                            newCommunicationChannel(CommunicationChannelType.POSTAL_ADDRESS),
                            newCommunicationChannel(CommunicationChannelType.EMAIL_ADDRESS)
                    )
            );
        }

        private CommunicationChannel newCommunicationChannel(CommunicationChannelType type) {
            final CommunicationChannel cc = new CommunicationChannel(){};
            cc.setType(type);
            return cc;
        }

    }

}