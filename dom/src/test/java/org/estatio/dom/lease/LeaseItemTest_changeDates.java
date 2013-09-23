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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.contracttests.AbstractWithIntervalMutableContractTest_changeDates;
import org.estatio.dom.event.Event;

public class LeaseItemTest_changeDates extends AbstractWithIntervalMutableContractTest_changeDates<LeaseItem> {

    private boolean locked;
    private LeaseItem leaseItem;

    @Before
    public void setUp() throws Exception {
        leaseItem = withIntervalMutable;
    }
    
    protected LeaseItem doCreateWithIntervalMutable(final WithIntervalMutable.Helper<LeaseItem> mockChangeDates) {
        return new LeaseItem() {
            @Override
            org.estatio.dom.WithIntervalMutable.Helper<LeaseItem> getChangeDates() {
                return mockChangeDates;
            }
            @Override
            public boolean isLocked() {
                return locked;
            }
        };
    }
    
    @Test
    public void disableChangeDates_whenLocked() throws Exception {
        locked = true;
        assertThat(leaseItem.disableChangeDates(null,null), is("Cannot modify when locked"));
    }
    
    @Test
    public void disableChangeDates_whenNotLocked() throws Exception {
        locked = false;
        assertThat(leaseItem.disableChangeDates(null,null), is(nullValue()));
    }


    // //////////////////////////////////////

    @Test
    public void changeDatesDelegate() {
        leaseItem = new LeaseItem();
        assertThat(leaseItem.getChangeDates(), is(not(nullValue())));
    }

}
