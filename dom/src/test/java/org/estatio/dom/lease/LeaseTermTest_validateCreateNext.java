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
package org.estatio.dom.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

public class LeaseTermTest_validateCreateNext {

    private LeaseTermForTesting leaseTerm;


    @Before
    public void setUp() throws Exception {
        leaseTerm = new LeaseTermForTesting();
        leaseTerm.setStartDate(new LocalDate(2014,6,1));
        leaseTerm.setEndDate(new LocalDate(2014,8,31));
    }


    @Test
    public void happy() throws Exception {
        assertThat(leaseTerm.validateCreateNext(new LocalDate(2014,7,1)), is(nullValue()));
    }

    @Test
    public void canStartOnStartDateAsThis() throws Exception {
        assertThat(leaseTerm.validateCreateNext(new LocalDate(2014,6,1)), is(nullValue()));
    }
    
    @Test
    public void canEndOnEndDateAsThis() throws Exception {
        assertThat(leaseTerm.validateCreateNext(new LocalDate(2014,8,31)), is(nullValue()));
    }
    
    @Test
    public void tooEarly() throws Exception {
        assertThat(leaseTerm.validateCreateNext(new LocalDate(2014,5,31)), is("Cannot start before this start date"));
    }
    
    @Test
    public void canStartAfterThisEnds() throws Exception {
        // because the action itself will auto-align
        assertThat(leaseTerm.validateCreateNext(new LocalDate(2014,9,1)), is(nullValue()));
    }
    

}
