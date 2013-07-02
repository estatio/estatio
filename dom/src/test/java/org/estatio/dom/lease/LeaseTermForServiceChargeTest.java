/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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

import java.math.BigDecimal;

import org.hamcrest.core.Is;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.index.Index;

public class LeaseTermForServiceChargeTest {

    private Lease lease;
    private LeaseItem item;
    private LeaseTermForServiceCharge term;

    public Index i;

    @Mock
    LeaseTerms mockLeaseTerms;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {
        lease = new Lease();
        lease.setStartDate(new LocalDate(2000,1,1));
        item = new LeaseItem();
        item.modifyLease(lease);
        item.setType(LeaseItemType.SERVICE_CHARGE);
        item.injectLeaseTerms(mockLeaseTerms);
        term = new LeaseTermForServiceCharge();
        term.modifyLeaseItem(item);
        term.initialize();
        term.setStartDate(new LocalDate(2011, 1, 1));
        term.setBudgetedValue(BigDecimal.valueOf(6000).setScale(4));
    }

    @Test
    public void update_ok() {
        term.update();
        assertThat(term.getTrialValue(), Is.is(term.getBudgetedValue()));
        LeaseTermForServiceCharge nextTerm = new LeaseTermForServiceCharge();
        nextTerm.modifyLeaseItem(item);
        nextTerm.modifyPrevious(term);
        nextTerm.initialize();
        nextTerm.update();
        assertThat(nextTerm.getBudgetedValue(), Is.is(term.getBudgetedValue()));
    }

    @Test
    public void valueForDueDate_ok() throws Exception {
        LeaseTermForServiceCharge term = new LeaseTermForServiceCharge();
        term.modifyLeaseItem(item);
        term.setEndDate(new LocalDate(2011, 12, 31));
        term.setBudgetedValue(BigDecimal.valueOf(6000));
        term.setAuditedValue(BigDecimal.valueOf(6600));
        assertThat(term.valueForDueDate(new LocalDate(2011, 1, 1)), is(BigDecimal.valueOf(6000)));
        assertThat(term.valueForDueDate(new LocalDate(2011, 12, 31)), is(BigDecimal.valueOf(6000)));
        assertThat(term.valueForDueDate(new LocalDate(2012, 1, 1)), is(BigDecimal.valueOf(6600)));
        assertThat(term.valueForDueDate(new LocalDate(2012, 7, 31)), is(BigDecimal.valueOf(6600)));
    }

}
