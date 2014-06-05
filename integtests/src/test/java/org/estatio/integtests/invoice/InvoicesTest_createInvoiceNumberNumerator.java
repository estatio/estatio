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
package org.estatio.integtests.invoice;

import java.math.BigInteger;
import javax.inject.Inject;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.Constants;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.numerator.Numerator;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKal;
import org.estatio.fixture.asset.PropertyForOxf;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class InvoicesTest_createInvoiceNumberNumerator extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);

                execute(new PropertyForOxf(), executionContext);
                execute(new PropertyForKal(), executionContext);
            }
        });
    }

    @Inject
    private Invoices invoices;
    @Inject
    private Properties properties;
    @Inject
    private BookmarkService bookmarkService;
    
    private Property propertyOxf;
    private Property propertyKal;
    
    private Bookmark propertyOxfBookmark;

    @Before
    public void setUp() throws Exception {
        propertyOxf = properties.findPropertyByReference(PropertyForOxf.PROPERTY_REFERENCE);
        propertyKal = properties.findPropertyByReference(PropertyForKal.PROPERTY_REFERENCE);

        propertyOxfBookmark = bookmarkService.bookmarkFor(propertyOxf);
    }
    
    @Test
    public void whenNoneForProperty() throws Exception {

        // given
        Numerator numerator = invoices.findInvoiceNumberNumerator(propertyOxf);
        Assert.assertNull(numerator);

        // when
        numerator = invoices.createInvoiceNumberNumerator(propertyOxf, "OXF-%05d", BigInteger.TEN);

        //then
        Assert.assertNotNull(numerator);
        assertThat(numerator.getName(), is(Constants.INVOICE_NUMBER_NUMERATOR_NAME));
        assertThat(numerator.getObjectType(), is(propertyOxfBookmark.getObjectType()));
        assertThat(numerator.getObjectIdentifier(), is(propertyOxfBookmark.getIdentifier()));
        assertThat(numerator.getLastIncrement(), is(BigInteger.TEN));
    }

    @Test
    public void canCreateOnePerProperty() throws Exception {

        // given
        Numerator numerator1 = invoices.createInvoiceNumberNumerator(propertyOxf, "OXF-%05d", BigInteger.TEN);
        Assert.assertNotNull(numerator1);
        
        // when
        Numerator numerator2 = invoices.createInvoiceNumberNumerator(propertyKal, "KAL-%05d", BigInteger.ZERO);

        // then
        Assert.assertNotNull(numerator2);
        assertThat(numerator1, is(not(numerator2)));
        
        assertThat(numerator1.increment(), is("OXF-00011"));
        assertThat(numerator2.increment(), is("KAL-00001"));
        assertThat(numerator2.increment(), is("KAL-00002"));
        assertThat(numerator1.increment(), is("OXF-00012"));
    }

    @Test
    public void canOnlyCreateOnePerProperty_andCannotReset() throws Exception {

        // given
        Numerator numerator1 = invoices.createInvoiceNumberNumerator(propertyOxf, "OXF-%05d", BigInteger.TEN);
        Assert.assertNotNull(numerator1);

        assertThat(numerator1.increment(), is("OXF-00011"));

        // when
        Numerator numerator2 = invoices.createInvoiceNumberNumerator(propertyOxf, "KAL-%05d", BigInteger.ZERO);

        // then
        Assert.assertNotNull(numerator2);
        assertThat(numerator1, is(sameInstance(numerator2)));

        assertThat(numerator1.increment(), is("OXF-00012"));
    }

}
