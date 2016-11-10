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
package org.estatio.dom.agreement;

import org.junit.Before;
import org.junit.Test;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import static org.assertj.core.api.Assertions.assertThat;

public class AgreementRoleCommunicationChannelTypeTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final AgreementRoleCommunicationChannelType agreementRoleCommunicationChannelType = new AgreementRoleCommunicationChannelType();
            newPojoTester()
                    .withFixture(pojos(AgreementType.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(agreementRoleCommunicationChannelType);
        }

    }

    public static class MatchingCommunicationChannel extends AgreementRoleCommunicationChannelTypeTest {

        private AgreementRoleCommunicationChannelType arcct;
        private AgreementRoleCommunicationChannelType arcct2;
        private AgreementRoleCommunicationChannel arcc;

        @Before
        public void setUp() throws Exception {
            arcc = new AgreementRoleCommunicationChannel();
            arcct = new AgreementRoleCommunicationChannelType();
            arcct2 = new AgreementRoleCommunicationChannelType();
        }

        @Test
        public void whenNull() {
            assertThat(arcct.matchingCommunicationChannel().apply(null)).isFalse();
        }

        @Test
        public void whenTypeDifferent() {
            arcc.setType(arcct2);
            assertThat(arcct.matchingCommunicationChannel().apply(arcc)).isFalse();
        }

        @Test
        public void whenTypeSame() {
            arcc.setType(arcct);
            assertThat(arcct.matchingCommunicationChannel().apply(arcc)).isTrue();
        }
    }

}