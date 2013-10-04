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
package org.estatio.integration.tests.app;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.estatio.app.PropertyInvoiceSummaries;
import org.estatio.app.PropertyInvoiceSummary;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;

public class PropertyInvoiceSummariesTest_finders extends EstatioIntegrationTest {

    private PropertyInvoiceSummaries propertyInvoiceSummaries;
    private Properties properties;

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioTransactionalObjectsFixture());
    }

    @Before
    public void setUp() throws Exception {
        propertyInvoiceSummaries = service(PropertyInvoiceSummaries.class);
        properties = service(Properties.class);
    }
    
    @Test
    public void allPropertyInvoiceSummaries() throws Exception {
        List<PropertyInvoiceSummary> summaries = propertyInvoiceSummaries.propertyInvoicesSql();
        List<Property> propertyLst = properties.allProperties();
        assertThat(summaries.size(), is(propertyLst.size()));
        PropertyInvoiceSummary summary = summaries.get(0);
        Property property = summary.getProperty();
        assertThat(property, is(not(nullValue())));
    }

}
