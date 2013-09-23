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
package org.estatio.dom.event;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.contracttests.AbstractWithIntervalMutableContractTest_changeDates;

public class EventTest_changeDates extends AbstractWithIntervalMutableContractTest_changeDates<Event> {


    private boolean locked;
    private Event event;

    @Before
    public void setUp() throws Exception {
        event = withIntervalMutable;
    }
    
    protected Event doCreateWithIntervalMutable(final WithIntervalMutable.Helper<Event> mockChangeDates) {
        return new Event() {
            @Override
            org.estatio.dom.WithIntervalMutable.Helper<Event> getChangeDates() {
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
        assertThat(event.disableChangeDates(null,null), is("Cannot modify when locked"));
    }
    
    @Test
    public void disableChangeDates_whenNotLocked() throws Exception {
        locked = false;
        assertThat(event.disableChangeDates(null,null), is(nullValue()));
    }


    // //////////////////////////////////////

    @Test
    public void changeDatesDelegate() {
        event = new Event();
        assertThat(event.getChangeDates(), is(not(nullValue())));
    }

}
