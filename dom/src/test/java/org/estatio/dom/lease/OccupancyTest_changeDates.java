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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.contracttests.AbstractWithIntervalMutableContractTest_changeDates;

public class OccupancyTest_changeDates extends AbstractWithIntervalMutableContractTest_changeDates<Occupancy> {

    private Occupancy occupancy;

    @Before
    public void setUp() throws Exception {
        occupancy = withIntervalMutable;
    }
    
    protected Occupancy doCreateWithIntervalMutable(final WithIntervalMutable.Helper<Occupancy> mockChangeDates) {
        return new Occupancy() {
            @Override
            org.estatio.dom.WithIntervalMutable.Helper<Occupancy> getChangeDates() {
                return mockChangeDates;
            }
        };
    }
    
    // //////////////////////////////////////

    @Test
    public void changeDatesDelegate() {
        occupancy = new Occupancy();
        assertThat(occupancy.getChangeDates(), is(not(nullValue())));
    }

}
