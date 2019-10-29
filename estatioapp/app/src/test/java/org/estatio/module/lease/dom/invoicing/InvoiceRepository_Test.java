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
package org.estatio.module.lease.dom.invoicing;

import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Test;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.currency.dom.Currency;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.party.dom.Party;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceRepository_Test {

    InvoiceForLeaseRepository invoiceForLeaseRepository;

        @Test
        public void whenMany_returnsFirst() {

            final InvoiceForLease invoice1 = new InvoiceForLease();
            final InvoiceForLease invoice2 = new InvoiceForLease();
            final InvoiceForLease invoice3 = new InvoiceForLease();

            invoiceForLeaseRepository = new InvoiceForLeaseRepository() {
                @Override
                @Action(hidden = Where.EVERYWHERE, semantics = SemanticsOf.SAFE)
                public List<InvoiceForLease> findMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, Lease lease, InvoiceStatus invoiceStatus, LocalDate dueDate) {
                    return Arrays.asList(invoice1, invoice2, invoice3);
                }
            };

            assertThat(invoiceForLeaseRepository.findMatchingInvoice(null, null, null, null, null, null)).isEqualTo(invoice1);
        }

        @Test
        public void whenEmpty_returnsNull() {

            invoiceForLeaseRepository = new InvoiceForLeaseRepository() {
                @Override
                @Action(hidden = Where.EVERYWHERE, semantics = SemanticsOf.SAFE)
                public List<InvoiceForLease> findMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, Lease lease, InvoiceStatus invoiceStatus, LocalDate dueDate) {
                    return Arrays.<InvoiceForLease>asList();
                }

                @Override
                public InvoiceForLease newInvoice(final ApplicationTenancy applicationTenancy, Party seller, Party buyer, PaymentMethod paymentMethod, Currency currency, LocalDate dueDate, Lease lease, String interactionId) {
                    return null;
                }
            };
            assertThat(invoiceForLeaseRepository.findMatchingInvoice(null, null, null, null, null, null)).isNull();
        }


}
