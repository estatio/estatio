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
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class LeaseItemTest_remove {

    private Lease lease;
    private LeaseItem leaseItem;
    private LeaseTermForTesting leaseTerm;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DomainObjectContainer mockContainer;

    private boolean leaseTermSuccessfullyRemoved;

    @Before
    public void setUp() throws Exception {
        lease = new Lease();
        leaseItem = new LeaseItem();

        leaseItem.setLease(lease);
        leaseItem.setContainer(mockContainer);

        leaseTerm = new LeaseTermForTesting() {
            @Override
            public boolean doRemove() {
                return leaseTermSuccessfullyRemoved;
            }
        };

    }

    @Test
    public void whenNotConfirmed() throws Exception {
        Object returned = leaseItem.remove(false);
        assertThat(returned, is((Object) leaseItem));
    }

    @Test
    public void whenConfirmedAndNoChildTerms() throws Exception {
        expectingRemoveAndFlush(leaseItem);

        Object returned = leaseItem.remove(true);
        assertThat(returned, is((Object) lease));
    }

    @Test
    public void whenConfirmedAndChildTermsThatVeto() throws Exception {

        leaseItem.getTerms().add(leaseTerm);
        leaseTermSuccessfullyRemoved = false;

        Object returned = leaseItem.remove(true);
        assertThat(returned, is((Object) leaseItem));
    }

    @Test
    public void whenConfirmedAndChildTermsThatDontVeto() throws Exception {
        leaseItem.getTerms().add(leaseTerm);

        leaseTermSuccessfullyRemoved = true;
        expectingRemoveAndFlush(leaseItem);

        Object returned = leaseItem.remove(true);
        assertThat(returned, is((Object) lease));
    }

    private void expectingRemoveAndFlush(final LeaseItem obj) {
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).remove(obj);
                oneOf(mockContainer).flush();
            }
        });
    }

}
