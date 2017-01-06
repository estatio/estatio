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
package org.estatio.fixture.invoice;

import java.math.BigInteger;
import java.util.SortedSet;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.dom.asset.Property;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.currency.CurrencyRepository;
import org.estatio.dom.lease.leaseinvoicing.NumeratorForCollectionRepository;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.leaseinvoicing.InvoiceForLease;
import org.estatio.dom.lease.leaseinvoicing.InvoiceForLeaseRepository;
import org.estatio.dom.lease.leaseinvoicing.InvoiceItemForLease;
import org.estatio.dom.lease.leaseinvoicing.InvoiceItemForLeaseRepository;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;

/**
 * Creates {@link org.estatio.dom.invoice.Invoice} and associated {@link org.estatio.dom.invoice.InvoiceItem}s.
 */
public abstract class InvoiceAbstract extends FixtureScript {

    protected InvoiceAbstract(String friendlyName, String localName) {
        super(friendlyName, localName);
    }

    protected InvoiceForLease createInvoiceAndNumerator(
            final ApplicationTenancy applicationTenancy,
            Lease lease,
            String sellerStr,
            String buyerStr,
            PaymentMethod paymentMethod,
            String currencyStr,
            LocalDate startDate,
            ExecutionContext executionContext) {
        final Party buyer = partyRepository.findPartyByReference(buyerStr);
        final Party seller = partyRepository.findPartyByReference(sellerStr);
        final Currency currency = currencyRepository.findCurrency(currencyStr);

        final String interactionId = null;

        final InvoiceForLease invoice = invoiceForLeaseRepository
                .newInvoice(applicationTenancy, seller, buyer, paymentMethod, currency, startDate, lease, interactionId);
        invoice.setInvoiceDate(startDate);

        final Property property = lease.getProperty();
        final String format = property.getReference() + "-%06d";
        numeratorForCollectionRepository.createInvoiceNumberNumerator(property, format, BigInteger.ZERO, applicationTenancy);

        return executionContext.addResult(this, invoice);
    }

    protected void createInvoiceItemsForTermsOfFirstLeaseItemOfType(
            final InvoiceForLease invoice, final LeaseItemType leaseItemType,
            final LocalDate startDate, final LocalDateInterval interval,
            final ExecutionContext executionContext) {

        final Lease lease = invoice.getLease();
        final LeaseItem firstLeaseItem = lease.findFirstItemOfType(leaseItemType);
        final SortedSet<LeaseTerm> terms = firstLeaseItem.getTerms();
        for (final LeaseTerm term : terms) {
            InvoiceItemForLease item = invoiceItemForLeaseRepository.newInvoiceItem(term, interval, startDate, null);
            item.setInvoice(invoice);
            item.setSequence(invoice.nextItemSequence());

            executionContext.addResult(this, item);
        }
    }

    // //////////////////////////////////////

    @Inject
    private PartyRepository partyRepository;

    @Inject
    private CurrencyRepository currencyRepository;

    @Inject
    private InvoiceForLeaseRepository invoiceForLeaseRepository;

    @Inject
    private InvoiceItemForLeaseRepository invoiceItemForLeaseRepository;

    @Inject
    protected LeaseRepository leaseRepository;

    @Inject
    protected ApplicationTenancies applicationTenancies;

    @Inject
    protected NumeratorForCollectionRepository numeratorForCollectionRepository;

}
