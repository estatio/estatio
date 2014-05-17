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

import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.numerator.Numerator;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertiesAndUnitsFixture;
import org.estatio.fixture.lease.LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture;
import org.estatio.fixture.party.PersonsAndOrganisationsAndCommunicationChannelsFixture;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;

import static org.junit.Assert.assertNotNull;

public class InvoicesTest_findInvoiceNumberNumerator extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new CompositeFixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);
                execute("parties", new PersonsAndOrganisationsAndCommunicationChannelsFixture(), executionContext);
                execute("properties", new PropertiesAndUnitsFixture(), executionContext);
                execute("leases", new LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture(), executionContext);
            }
        });
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
    public void whenNone() throws Exception {
        // when
        Numerator numerator = invoices.findInvoiceNumberNumerator(property1);
        // then
        Assert.assertNull(numerator);
    }


}
