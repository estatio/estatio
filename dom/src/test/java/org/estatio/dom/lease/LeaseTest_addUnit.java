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

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class LeaseTest_addUnit {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DomainObjectContainer mockContainer;

    private Lease lease;
    private Occupancies leaseUnits;

    private UnitForLease unit;

    @Before
    public void setUp() throws Exception {
    
        unit = new UnitForLease();
        
        // this is actually a mini-integration test...
        leaseUnits = new Occupancies();
        leaseUnits.setContainer(mockContainer);
        
        lease = new Lease();
        lease.injectOccupancies(leaseUnits);
    }
    
    @Test
    public void test() {
        final Occupancy leaseUnit = new Occupancy();
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).newTransientInstance(Occupancy.class);
                will(returnValue(leaseUnit));
                oneOf(mockContainer).persistIfNotAlready(leaseUnit);
            }
        });
        
        final Occupancy addedUnit = lease.occupy(unit);
        assertThat(addedUnit, is(leaseUnit));
        assertThat(leaseUnit.getLease(), is(lease));
        assertThat(leaseUnit.getUnit(), is(unit));
    }

}
