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
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.applib.services.memento.MementoService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InvoiceSummaryForPropertyDueDateTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_ONLY);

    @Mock
    MementoService mockMementoService;

    @Mock
    MementoService.Memento mockMemento;

    InvoiceSummaryForPropertyDueDate viewModel;

    @Before
    public void setUp() throws Exception {
        viewModel = new InvoiceSummaryForPropertyDueDate();
        viewModel.injectMementoService(mockMementoService);

        viewModel.setReference("OXF");
        viewModel.setDueDate(new LocalDate(2013, 4, 1));
        viewModel.setNetAmount(new BigDecimal("10.00"));
        viewModel.setVatAmount(new BigDecimal("1.75"));
        viewModel.setGrossAmount(new BigDecimal("11.75"));
        viewModel.setTotal(123);
    }

    public static class ViewModelMemento extends InvoiceSummaryForPropertyDueDateTest {

        @Test
        public void happyCase() {

            context.checking(new Expectations() {
                {
                    oneOf(mockMementoService).create();
                    will(returnValue(mockMemento));

                    oneOf(mockMemento).set("reference", "OXF");
                    will(returnValue(mockMemento));
                    oneOf(mockMemento).set("dueDate", new LocalDate(2013, 4, 1));
                    will(returnValue(mockMemento));
                    oneOf(mockMemento).set("netAmount", new BigDecimal("10.00"));
                    will(returnValue(mockMemento));
                    oneOf(mockMemento).set("vatAmount", new BigDecimal("1.75"));
                    will(returnValue(mockMemento));
                    oneOf(mockMemento).set("grossAmount", new BigDecimal("11.75"));
                    will(returnValue(mockMemento));
                    oneOf(mockMemento).set("total", 123);
                    will(returnValue(mockMemento));

                    oneOf(mockMemento).asString();
                    will(returnValue("encodedXml"));
                }
            });

            assertThat(viewModel.viewModelMemento(), is("encodedXml"));
        }

    }

    public static class ViewModelInit extends InvoiceSummaryForPropertyDueDateTest {

        @Test
        public void happyCase() {

            context.checking(new Expectations() {
                {
                    oneOf(mockMementoService).parse("encodedXml");
                    will(returnValue(mockMemento));

                    oneOf(mockMemento).get("reference", String.class);
                    will(returnValue("OXF"));
                    oneOf(mockMemento).get("dueDate", LocalDate.class);
                    will(returnValue(new LocalDate(2013, 4, 1)));
                    oneOf(mockMemento).get("netAmount", BigDecimal.class);
                    will(returnValue(new BigDecimal("10.00")));
                    oneOf(mockMemento).get("vatAmount", BigDecimal.class);
                    will(returnValue(new BigDecimal("1.75")));
                    oneOf(mockMemento).get("grossAmount", BigDecimal.class);
                    will(returnValue(new BigDecimal("11.75")));
                    oneOf(mockMemento).get("total", Integer.class);
                    will(returnValue(123));
                }
            });

            viewModel.viewModelInit("encodedXml");

            assertThat(viewModel.getReference(), is("OXF"));
            assertThat(viewModel.getDueDate(), is(new LocalDate(2013, 4, 1)));
            assertThat(viewModel.getNetAmount(), is(new BigDecimal("10.00")));
            assertThat(viewModel.getVatAmount(), is(new BigDecimal("1.75")));
            assertThat(viewModel.getGrossAmount(), is(new BigDecimal("11.75")));
            assertThat(viewModel.getTotal(), is(123));

        }
    }
}