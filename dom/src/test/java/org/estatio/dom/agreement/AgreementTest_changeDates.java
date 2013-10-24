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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.contracttests.AbstractWithIntervalMutableContractTest_changeDates;

public class AgreementTest_changeDates extends AbstractWithIntervalMutableContractTest_changeDates<Agreement> {


    private Agreement agreement;

    @Before
    public void setUp() throws Exception {
        agreement = withIntervalMutable;
    }
    
    protected Agreement doCreateWithIntervalMutable(final WithIntervalMutable.Helper<Agreement> mockChangeDates) {
        return new AgreementForTesting() {
            @Override
            org.estatio.dom.WithIntervalMutable.Helper<Agreement> getChangeDates() {
                return mockChangeDates;
            }
        };
    }
    

    @Test
    public void changeDatesDelegate() {
        agreement = new AgreementForTesting();
        assertThat(agreement.getChangeDates(), is(not(nullValue())));
    }

}
