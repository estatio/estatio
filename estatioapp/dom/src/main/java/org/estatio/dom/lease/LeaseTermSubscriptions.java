/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.dom.lease;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;

@DomainService(nature = NatureOfService.DOMAIN)
public class LeaseTermSubscriptions extends UdoDomainService<LeaseTermSubscriptions> {

    public final String DEFAULT_INVALIDATION_MESSAGE = "You cannot change the invoicing frequency of a lease item with invoice items on its terms. This lease item has invoice items on the following term(s):\n";

    public LeaseTermSubscriptions() {
        super(LeaseTermSubscriptions.class);
    }

    @Subscribe
    @Programmatic
    public void on(LeaseItem.ChangeInvoicingFrequencyEvent ev) {
        LeaseItem sourceLeaseItem = ev.getSource();

        List<LeaseTerm> terms;
        switch(ev.getEventPhase()) {
        case VALIDATE:
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(DEFAULT_INVALIDATION_MESSAGE);

            terms = leaseTerms.findByLeaseItem(sourceLeaseItem);
            terms.forEach(term -> {
                SortedSet<InvoiceItemForLease> invoiceItemsForTerm = term.getInvoiceItems();
                if (!invoiceItemsForTerm.isEmpty()) {
                    stringBuilder.append(term.title() + "\n");
                }
            });

            if (!stringBuilder.toString().equals(DEFAULT_INVALIDATION_MESSAGE)) {
                ev.invalidate(stringBuilder.toString());
            }
            break;
        default:
            break;
        }
    }

    @Inject
    LeaseTerms leaseTerms;
}
