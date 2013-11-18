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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.net.URL;

import com.google.common.io.BaseEncoding;
import com.google.common.io.Resources;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.services.xmlsnapshot.XmlSnapshotServiceAbstract;

public class InvoiceSummaryForPropertyDueDateTest_viewModelInit {

    private String base64UrlEncodedXml;


    @Before
    public void setUp() throws Exception {
        URL resource = Resources.getResource(InvoiceSummaryForPropertyDueDateTest_viewModelInit.class, "InvoiceSummaryForPropertyDueDateTest_viewModelInit.xml");
        base64UrlEncodedXml = BaseEncoding.base64Url().encode(Resources.toByteArray(resource));
    }

    @Test
    public void test() {
        InvoiceSummaryForPropertyDueDate viewModel = new InvoiceSummaryForPropertyDueDate();
        viewModel.viewModelInit(base64UrlEncodedXml);
        
        assertThat(viewModel.getReference(), is("OXF"));
        assertThat(viewModel.getDueDate(), is(new LocalDate(2013,4,1)));
        assertThat(viewModel.getNetAmount(), is(new BigDecimal("10.00")));
        assertThat(viewModel.getVatAmount(), is(new BigDecimal("1.75")));
        assertThat(viewModel.getGrossAmount(), is(new BigDecimal("11.75")));
        assertThat(viewModel.getTotal(), is(123));
    }


}
