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

package org.estatio.module.lease.dom;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.title.TitleService;

import org.estatio.dom.UdoDomainService;

// TODO: REVIEW, why use a subscriber for intra-module interactions ??
@DomainService(nature = NatureOfService.DOMAIN)
public class LeaseTermSubscriptions extends UdoDomainService<LeaseTermSubscriptions> {

    private final String DEFAULT_INVALIDATION_MESSAGE = "You cannot change the invoicing frequency of a lease item with invoice items on its terms. This lease item has invoice items on the following term(s): \n";

    public LeaseTermSubscriptions() {
        super(LeaseTermSubscriptions.class);
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final LeaseItem.ChangeInvoicingFrequencyEvent ev) {
        final LeaseItem sourceLeaseItem = ev.getSource();

        switch(ev.getEventPhase()) {
        case VALIDATE:
            final StringBuilder buf = new StringBuilder();

            final List<LeaseTerm> terms = leaseTermRepository.findByLeaseItem(sourceLeaseItem);
            terms.stream()
                    .map(LeaseTerm::getInvoiceItems)
                    .filter(invoiceTerms -> !invoiceTerms.isEmpty())
                    .forEach(invoiceItemForLeases -> {
                        final LeaseTerm term = invoiceItemForLeases.first().getLeaseTerm();
                        buf.append(titleService.titleOf(term)).append("\n");
                    });

            if (buf.length() > 0) {
                ev.invalidate(DEFAULT_INVALIDATION_MESSAGE + buf.toString());
            }
            break;
        default:
            break;
        }
    }

    @Inject
    LeaseTermRepository leaseTermRepository;
    @Inject
    TitleService titleService;
}
