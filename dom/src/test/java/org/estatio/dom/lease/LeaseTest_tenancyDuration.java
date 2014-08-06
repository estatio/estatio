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
import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.ArrayList;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.invoice.PaymentMethod;

public class LeaseTest_tenancyDuration {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DomainObjectContainer mockContainer;

    private Lease lease;


    @Before
    public void setUp() throws Exception {
        lease = new Lease();
    }
    
    @Test
    public void testAllDatesSet() {
        LocalDate startDate = new LocalDate(2012, 8, 01);
        LocalDate endDate = new LocalDate(2016, 7, 30);
        
        lease.setStartDate(startDate);
        lease.setEndDate(endDate);
        lease.setTenancyStartDate(startDate);
        lease.setTenancyEndDate(endDate);
        
        assertThat(lease.getTenancyDuration(), is("3y11m30d"));
    }

    @Test
    public void testNoLeaseEndDateSet() {
        LocalDate startDate = new LocalDate(2012, 8, 01);
        lease.setStartDate(startDate);
        lease.setTenancyStartDate(startDate);
        
        assertNull(lease.getTenancyDuration());
    }
    
    @Test
    public void testNoLeaseStartDateSet() {
        LocalDate endDate = new LocalDate(2016, 7, 31);
        lease.setEndDate(endDate);
        lease.setTenancyEndDate(endDate);
        
        assertNull(lease.getTenancyDuration());
    }
    
    @Test
    public void testNoTenancyDatesSet() {
        LocalDate startDate = new LocalDate(2012, 8, 01);
        LocalDate endDate = new LocalDate(2016, 7, 31);
        
        lease.setStartDate(startDate);
        lease.setEndDate(endDate);
        
        assertNull(lease.getTenancyDuration());
    }
    
    @Test
    public void testEndDateBeforeStartDate() {
        LocalDate startDate = new LocalDate(2012, 8, 01);
        LocalDate endDate = new LocalDate(2016, 7, 31);
        
        lease.setStartDate(startDate);
        lease.setEndDate(endDate);
        
        lease.setTenancyStartDate(endDate);
        lease.setTenancyEndDate(startDate);
        
        assertNull(lease.getTenancyDuration());
    }
}
