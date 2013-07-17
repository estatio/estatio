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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.financial.BankMandate;

public class LeaseTest_newItem {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DomainObjectContainer mockContainer;

    private Lease lease;
    private LeaseItems leaseItems;


    @Before
    public void setUp() throws Exception {
    
        // this is actually a mini-integration test...
        leaseItems = new LeaseItems();
        leaseItems.setContainer(mockContainer);
        
        lease = new Lease();
        lease.injectLeaseItems(leaseItems);
    }
    
    @Test
    public void test() {
        assertThat(lease.getItems(), Matchers.empty());
        
        final LeaseItem leaseItem = new LeaseItem();
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).newTransientInstance(LeaseItem.class);
                will(returnValue(leaseItem));
                oneOf(mockContainer).persistIfNotAlready(leaseItem);
            }
        });
        
        final LeaseItem newItem = lease.newItem(LeaseItemType.RENT);
        assertThat(newItem, is(leaseItem));
        assertThat(leaseItem.getLease(), is(lease));
        
        // this assertion not true for unit tests, because we rely on JDO
        // to manage the bidir relationship for us.
        //assertThat(lease.getItems(), Matchers.contains(newItem));
    }

    
    
    
}
