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
package org.estatio.dom.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.services.clock.ClockService;

public class LeaseItemTest_getCurrentValue {

    private final LocalDate now = LocalDate.now();

    private LeaseItem leaseItem;

    private LeaseTermForTesting leaseTerm;

    private LocalDate getCurrentValueDateArgument;

    @Mock
    private ClockService mockClockService;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setUp() throws Exception {
        context.checking(new Expectations() {
            {
                oneOf(mockClockService).now();
                will(returnValue(now));
            }
        });

        leaseTerm = new LeaseTermForTesting();
        leaseTerm.setValue(BigDecimal.TEN);

        leaseItem = new LeaseItem() {
            @Override
            @Hidden
            public LeaseTerm currentTerm(LocalDate date) {
                LeaseItemTest_getCurrentValue.this.getCurrentValueDateArgument = date;
                return leaseTerm;
            }
        };
        leaseItem.injectClockService(mockClockService);
    }

    @Test
    public void test() {
        assertThat(leaseItem.getValue(), is(BigDecimal.TEN));
        assertThat(getCurrentValueDateArgument, is(now));
    }

}
