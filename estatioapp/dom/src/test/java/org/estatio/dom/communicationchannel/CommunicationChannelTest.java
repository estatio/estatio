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
package org.estatio.dom.communicationchannel;

import java.util.List;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;
import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.PojoTester;

public class CommunicationChannelTest {

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