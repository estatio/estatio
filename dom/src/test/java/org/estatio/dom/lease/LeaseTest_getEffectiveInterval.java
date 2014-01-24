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

import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.lease.Lease;

public class LeaseTest_getEffectiveInterval {

    private Lease agreement;

    @Before
    public void setup() {
        agreement = new Lease();
        agreement.setStartDate(new LocalDate(2012, 1, 1));

    }

    @Test
    public void getEffectiveInterval() {
        Assert.assertNull(agreement.getEffectiveInterval().endDateExcluding());
        agreement.setTenancyEndDate(new LocalDate(2012, 6, 30));
        Assert.assertThat(agreement.getEffectiveInterval().endDateExcluding(), Is.is(new LocalDate(2012, 7, 1)));
    }

}
