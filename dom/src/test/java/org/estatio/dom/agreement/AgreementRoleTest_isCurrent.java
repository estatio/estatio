/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.services.clock.ClockService;

public class AgreementRoleTest_isCurrent {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private ClockService clockService;

    private LocalDate now;
    
    private AgreementRole agreementRole;

    @Before
    public void setUp() throws Exception {
        now = LocalDate.now();

        context.checking(new Expectations() {
            {
                oneOf(clockService).now();
                will(returnValue(now));
            }
        });

        agreementRole = new AgreementRole();
        agreementRole.injectClockService(clockService);
    }
    
    @Test
    public void whenWithinOnStart() {
        assertIsCurrent(now, now.plusDays(1), true);
    }
    
    @Test
    public void whenJustWithinOnEnd() {
        assertIsCurrent(now.minusDays(1), now, true);
    }

    @Test
    public void whenOpenEnd() {
        assertIsCurrent(now, null, true);
    }
    
    @Test
    public void whenOpenStart() {
        assertIsCurrent(null, now, true);
    }

    @Test
    public void whenJustBefore() {
        assertIsCurrent(now.plusDays(1), now.plusDays(2), false);
    }

    @Test
    public void whenJustAfter() {
        assertIsCurrent(now.minusDays(2), now.minusDays(1), false);
    }
    
    private void assertIsCurrent(final LocalDate start, final LocalDate end, final boolean expect) {
        agreementRole.setStartDate(start);
        agreementRole.setEndDate(end);
        assertThat(agreementRole.isCurrent(), is(expect));
    }

}
