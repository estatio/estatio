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

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AgreementRoleCommunicationChannelTypeTest_matchingCommunicationChannel extends AbstractBeanPropertiesTest {

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
        assertThat(arcct.matchingCommunicationChannel().apply(null), is(false));
    }

    @Test
    public void whenTypeDifferent() {
        arcc.setType(arcct2);
        assertThat(arcct.matchingCommunicationChannel().apply(arcc), is(false));
    }

    @Test
    public void whenTypeSame() {
        arcc.setType(arcct);
        assertThat(arcct.matchingCommunicationChannel().apply(arcc), is(true));
    }

}
