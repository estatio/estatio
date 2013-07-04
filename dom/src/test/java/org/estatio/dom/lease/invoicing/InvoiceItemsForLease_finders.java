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
package org.estatio.dom.lease.invoicing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.lease.Lease;

public class InvoiceItemsForLease_finders {

    private FinderInteraction finderInteraction;

    private InvoiceItemsForLease invoiceItems;

    private Lease lease;
    private LocalDate startDate;
    private LocalDate dueDate;

    @Before
    public void setup() {

        lease = new Lease();
        startDate = new LocalDate(2013,4,1);
        dueDate = new LocalDate(2013,5,2);
        
        invoiceItems = new InvoiceItemsForLease() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }
            @Override
            protected List<InvoiceItemForLease> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }
            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };
    }

    @Test
    public void findInvoiceItemsByLease() {
        
        invoiceItems.findInvoiceItemsByLease("*REF?1*", startDate, dueDate);
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
        
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(InvoiceItemForLease.class));
        assertThat(finderInteraction.getQueryName(), is("findByLeaseAndStartDateAndDueDate"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("leaseReference"), is((Object)".*REF.1.*"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("startDate"), is((Object)startDate));
        assertThat(finderInteraction.getArgumentsByParameterName().get("dueDate"), is((Object)dueDate));
        
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(3));
    }

    @Test
    public void allInvoiceItems() {
        
        invoiceItems.allInvoiceItems();
        
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
    }
    
}
