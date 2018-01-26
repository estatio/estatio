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
package org.estatio.module.lease.fixtures.invoice.builders;

import java.math.BigInteger;
import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLeaseRepository;
import org.estatio.module.lease.dom.invoicing.NumeratorForCollectionRepository;
import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.party.dom.Party;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"lease"}, callSuper = false)
@ToString(of={"lease"})
@Accessors(chain = true)
public class InvoiceForLeaseBuilder extends BuilderScriptAbstract<InvoiceForLease, InvoiceForLeaseBuilder> {

    @Getter @Setter
    ApplicationTenancy applicationTenancy;
    @Getter @Setter
    Lease lease;
    @Getter @Setter
    Party seller;
    @Getter @Setter
    Party buyer;
    @Getter @Setter
    PaymentMethod paymentMethod;
    @Getter @Setter
    Currency currency;
    @Getter @Setter
    LocalDate dueDate;
    @Getter @Setter // optional
    LocalDate invoiceDate;

    @Getter @Setter
    List<ItemsSpec> itemSpecs = Lists.newArrayList();

    @AllArgsConstructor
    @Data
    public static class ItemsSpec {
        private final LeaseItemType leaseItemType;
        final LocalDate startDate;
        final LocalDateInterval interval;
    }

    @Getter
    private InvoiceForLease object;
    @Getter
    private Numerator numerator;
    @Getter
    private List<InvoiceItemForLease> items = Lists.newArrayList();

    @Override
    protected void execute(final ExecutionContext ec) {

        checkParam("lease", ec, Lease.class);
        checkParam("seller", ec, Party.class);
        checkParam("buyer", ec, Party.class);
        checkParam("paymentMethod", ec, PaymentMethod.class);
        checkParam("currency", ec, Currency.class);
        checkParam("dueDate", ec, LocalDate.class);

        defaultParam("invoiceDate", ec, getDueDate());

        final String interactionId = null;

        final InvoiceForLease invoice =
                invoiceForLeaseRepository.newInvoice(
                        applicationTenancy, seller, buyer, paymentMethod, currency, dueDate, lease, interactionId);
        invoice.setInvoiceDate(this.invoiceDate);

        ec.addResult(this, invoice);


        final Property property = lease.getProperty();
        final String format = property.getReference() + "-%06d";
        final BigInteger lastIncrement = BigInteger.ZERO;

        this.numerator = numeratorForCollectionRepository
                .createInvoiceNumberNumerator(property, format, lastIncrement, applicationTenancy);

        ec.addResult(this, numerator);

        for (ItemsSpec itemSpec : itemSpecs) {
            final List<InvoiceItemForLease> items = createInvoiceItemsForTermsOfFirstLeaseItemOfType(
                    invoice, itemSpec.leaseItemType, itemSpec.startDate, itemSpec.interval, ec);
            this.items.addAll(items);
        }

        object = invoice;
    }

    private List<InvoiceItemForLease> createInvoiceItemsForTermsOfFirstLeaseItemOfType(
            final InvoiceForLease invoice,
            final LeaseItemType leaseItemType,
            final LocalDate startDate,
            final LocalDateInterval interval,
            final ExecutionContext ec) {

        final Lease lease = invoice.getLease();
        final LeaseItem firstLeaseItem = lease.findFirstItemOfType(leaseItemType);
        final SortedSet<LeaseTerm> terms = firstLeaseItem.getTerms();

        List<InvoiceItemForLease> items = Lists.newArrayList();
        for (final LeaseTerm term : terms) {
            InvoiceItemForLease item = invoiceItemForLeaseRepository.newInvoiceItem(term, interval, interval, interval, startDate, null);
            item.setInvoice(invoice);
            item.setSequence(invoice.nextItemSequence());

            ec.addResult(this, item);
            items.add(item);
        }

        return items;
    }


    @Inject
    InvoiceForLeaseRepository invoiceForLeaseRepository;

    @Inject
    InvoiceItemForLeaseRepository invoiceItemForLeaseRepository;

    @Inject
    NumeratorForCollectionRepository numeratorForCollectionRepository;

}
