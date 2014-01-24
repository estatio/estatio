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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.services.clock.ClockService;

public class AgreementTest_isCurrent  {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
    
    @Mock
    private ClockService mockClockService;
    
    private Agreement agreement;
    
    @Before
    public void setUp() throws Exception {
        agreement = new AgreementForTesting();
        agreement.injectClockService(mockClockService);
    }
    
    @Test
    public void whenNoTerminationDate() {
        agreement.setStartDate(new LocalDate(2013,4,1));
        agreement.setEndDate(new LocalDate(2013,6,30));
        
        
        // before
        expectClockNowToReturn(new LocalDate(2013,3,31));
        assertThat(agreement.isCurrent(), is(false));
        
        // within
        expectClockNowToReturn(new LocalDate(2013,4,1));
        assertThat(agreement.isCurrent(), is(true));
        
        expectClockNowToReturn(new LocalDate(2013,5,15));
        assertThat(agreement.isCurrent(), is(true));
        
        expectClockNowToReturn(new LocalDate(2013,6,30));
        assertThat(agreement.isCurrent(), is(true));
        
        // after
        expectClockNowToReturn(new LocalDate(2013,7,1));
        assertThat(agreement.isCurrent(), is(false));
    }

    @Ignore // TO REVIEW
    @Test
    public void whenTerminationDate() {
        agreement.setStartDate(new LocalDate(2013,4,1));
        agreement.setEndDate(new LocalDate(2013,5,20));
        
        // before
        expectClockNowToReturn(new LocalDate(2013,3,31));
        assertThat(agreement.isCurrent(), is(false));
        
        // within
        expectClockNowToReturn(new LocalDate(2013,4,1));
        assertThat(agreement.isCurrent(), is(true));
        
        expectClockNowToReturn(new LocalDate(2013,5,15));
        assertThat(agreement.isCurrent(), is(true));
        
        expectClockNowToReturn(new LocalDate(2013,5,20));
        assertThat(agreement.isCurrent(), is(true));
        
        // after
        expectClockNowToReturn(new LocalDate(2013,5,21));
        assertThat(agreement.isCurrent(), is(false));
    }

    
    private void expectClockNowToReturn(final LocalDate result) {
        context.checking(new Expectations() {
            {
                oneOf(mockClockService).now();
                will(returnValue(result));
            }
        });
    }

}
