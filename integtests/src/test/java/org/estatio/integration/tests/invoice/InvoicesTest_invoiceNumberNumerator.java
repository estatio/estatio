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
package org.estatio.integration.tests.invoice;

import java.math.BigInteger;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.Constants;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.numerator.Numerator;
import org.estatio.fixture.EstatioOperationalResetFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InvoicesTest_invoiceNumberNumerator extends EstatioIntegrationTest {

    @BeforeClass
    public static void setupDataForClass() {
        scenarioExecution().install(new EstatioOperationalResetFixture());
    }

    private Invoices invoices;
    private Properties properties;
    private BookmarkService bookmarkService;
    
    private Property property1;
    private Property property2;
    
    private Bookmark property1Bookmark;
    private Bookmark property2Bookmark;

    @Before
    public void setUp() throws Exception {
        invoices = service(Invoices.class);
        properties = service(Properties.class);
        bookmarkService = service(BookmarkService.class);
        
        property1 = properties.findPropertyByReference("OXF");
        property2 = properties.findPropertyByReference("KAL");
        assertNotNull(property1);
        assertNotNull(property2);
        
        property1Bookmark = bookmarkService.bookmarkFor(property1);
        property2Bookmark = bookmarkService.bookmarkFor(property2);
    }
    
    @Test
    public void t01_findWhenNone() throws Exception {
        Numerator numerator = invoices.findInvoiceNumberNumerator(property1);
        Assert.assertNull(numerator);
    }

    @Test
    public void t02_createThenFind() throws Exception {
        Numerator numerator = invoices.createInvoiceNumberNumerator(property1, "OXF-%05d", BigInteger.TEN);
        Assert.assertNotNull(numerator);
        
        
        assertThat(numerator.getName(), is(Constants.INVOICE_NUMBER_NUMERATOR_NAME));
        assertThat(numerator.getObjectType(), is(property1Bookmark.getObjectType()));
        assertThat(numerator.getObjectIdentifier(), is(property1Bookmark.getIdentifier()));
        assertThat(numerator.getLastIncrement(), is(BigInteger.TEN));
    }

    @Test
    public void t03_scoped() throws Exception {
        // from previous test
        Numerator numerator1 = invoices.findInvoiceNumberNumerator(property1);
        Assert.assertNotNull(numerator1);
        
        // new in this test
        Numerator numerator2 = invoices.createInvoiceNumberNumerator(property2, "KAL-%05d", BigInteger.ZERO);
        Assert.assertNotNull(numerator2);
        
        assertThat(numerator1, is(not(numerator2)));
        
        assertThat(numerator1.increment(), is("OXF-00011"));
        assertThat(numerator2.increment(), is("KAL-00001"));
        assertThat(numerator2.increment(), is("KAL-00002"));
        assertThat(numerator1.increment(), is("OXF-00012"));
    }

}
