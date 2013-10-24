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
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;


public class AgreementTest_newRole_defaults  {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
    
    private Agreement agreement;

    private LocalDate effectiveStartDate;
    private LocalDate effectiveEndDate;

    
    @Before
    public void setUp() throws Exception {
        effectiveStartDate = new LocalDate(2013,4,1);
        effectiveEndDate = new LocalDate(2023,3,30);

        agreement = new AgreementForTesting() {
            @Override
            public LocalDate getStartDate() {
                return effectiveStartDate;
            }
            @Override
            public LocalDate getEndDate() {
                return effectiveEndDate;
            }
        };
    }
    
    @Test
    public void defaultStart() {
        assertThat(agreement.default2NewRole(), is(effectiveStartDate));
    }
    
    @Test
    public void defaultEnd() {
        assertThat(agreement.default3NewRole(), is(effectiveEndDate));
    }

    
}
