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
package org.estatio.dom.party;

import org.junit.Before;
import org.junit.Test;

import org.incode.module.base.dom.with.WithIntervalMutable;
import org.incode.module.unittestsupport.dom.with.WithIntervalMutableContractTestAbstract_changeDates;

import static org.assertj.core.api.Assertions.assertThat;

public class PartyRegistrationTest {

    public static class ChangeDates extends WithIntervalMutableContractTestAbstract_changeDates<PartyRegistration> {

        private PartyRegistration partyRegistration;

        @Before
        public void setUp() throws Exception {
            partyRegistration = withIntervalMutable;
        }

        protected PartyRegistration doCreateWithIntervalMutable(final WithIntervalMutable.Helper<PartyRegistration> mockChangeDates) {
            return new PartyRegistration() {
                @Override
                WithIntervalMutable.Helper<PartyRegistration> getChangeDates() {
                    return mockChangeDates;
                }
            };
        }

        // //////////////////////////////////////

        @Test
        public void changeDatesDelegate() {
            partyRegistration = new PartyRegistration();
            assertThat(partyRegistration.getChangeDates()).isNotNull();
        }

    }
}