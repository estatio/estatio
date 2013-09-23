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
package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.Status;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.contracttests.AbstractWithIntervalMutableContractTest_changeDates;

public class AgreementTest_changeDates extends AbstractWithIntervalMutableContractTest_changeDates<Agreement<Status>> {


    private boolean locked;
    private Agreement<Status> agreement;

    @Before
    public void setUp() throws Exception {
        agreement = withIntervalMutable;
    }
    
    protected Agreement<Status> doCreateWithIntervalMutable(final WithIntervalMutable.Helper<Agreement<Status>> mockChangeDates) {
        return new AgreementForTesting() {
            @Override
            org.estatio.dom.WithIntervalMutable.Helper<Agreement<Status>> getChangeDates() {
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
        assertThat(agreement.disableChangeDates(null,null), is("Cannot modify when locked"));
    }
    
    @Test
    public void disableChangeDates_whenNotLocked() throws Exception {
        locked = false;
        assertThat(agreement.disableChangeDates(null,null), is(nullValue()));
    }


    // //////////////////////////////////////

    @Test
    public void changeDatesDelegate() {
        agreement = new AgreementForTesting();
        assertThat(agreement.getChangeDates(), is(not(nullValue())));
    }

}
