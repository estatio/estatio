/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.estatio.dom.invoice.viewmodel;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

public class InvoiceSummaryForPropertyDueDateTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_ONLY);

    InvoiceSummaryForPropertyDueDate viewModel;

    @Before
    public void setUp() throws Exception {
        viewModel = new InvoiceSummaryForPropertyDueDate();
        viewModel.setReference("OXF");
        viewModel.setDueDate(new LocalDate(2013, 4, 1));
        viewModel.setNetAmount(new BigDecimal("10.00"));
        viewModel.setVatAmount(new BigDecimal("1.75"));
        viewModel.setGrossAmount(new BigDecimal("11.75"));
        viewModel.setTotal(123);
    }

}