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
package org.estatio.dom.lease.invoicing;

import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.query.Query;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;
import org.incode.module.unittestsupport.dom.repo.FinderInteraction.FinderMethod;

import org.estatio.dom.asset.Property;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceRepository;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyForTesting;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceRepository_Test {

    FinderInteraction finderInteraction;

    InvoiceRepository invoiceRepository;
    InvoiceForLeaseRepository invoiceForLeaseRepository;

    NumeratorForCollectionRepository estatioNumeratorRepository;

    Party seller;
    Party buyer;
    PaymentMethod paymentMethod;
    Lease lease;
    InvoiceStatus invoiceStatus;
    LocalDate dueDate;

    @Before
    public void setup() {

        seller = new PartyForTesting();
        buyer = new PartyForTesting();
        paymentMethod = PaymentMethod.BANK_TRANSFER;
        lease = new Lease() {
            @Override
            public Property getProperty() {
                return null;
            }
        };
        invoiceStatus = InvoiceStatus.APPROVED;
        dueDate = new LocalDate(2013, 4, 1);

        invoiceRepository = new InvoiceRepository() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<Invoice> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };
        invoiceForLeaseRepository = new InvoiceForLeaseRepository() {
            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<InvoiceForLease> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };

        estatioNumeratorRepository = new NumeratorForCollectionRepository();
    }

    public static class FindMatchingInvoices extends InvoiceRepository_Test {

        @Test
        public void happyCase() {

            invoiceForLeaseRepository.findMatchingInvoices(seller, buyer, paymentMethod, lease, invoiceStatus, dueDate);

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_MATCHES);
            assertThat(finderInteraction.getResultType()).isEqualTo(InvoiceForLease.class);
            assertThat(finderInteraction.getQueryName()).isEqualTo("findMatchingInvoices");
            assertThat(finderInteraction.getArgumentsByParameterName().get("buyer")).isEqualTo((Object) buyer);
            assertThat(finderInteraction.getArgumentsByParameterName().get("seller")).isEqualTo((Object) seller);
            assertThat(finderInteraction.getArgumentsByParameterName().get("paymentMethod")).isEqualTo((Object) paymentMethod);
            assertThat(finderInteraction.getArgumentsByParameterName().get("lease")).isEqualTo((Object) lease);
            assertThat(finderInteraction.getArgumentsByParameterName().get("status")).isEqualTo((Object) invoiceStatus);
            assertThat(finderInteraction.getArgumentsByParameterName().get("dueDate")).isEqualTo((Object) dueDate);

            assertThat(finderInteraction.getArgumentsByParameterName()).hasSize(6);

        }

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

    public static class AllInvoices extends InvoiceRepository_Test {

        @Test
        public void happyCase() {

            invoiceRepository.allInvoices();

            assertThat(finderInteraction.getFinderMethod()).isEqualTo(FinderMethod.ALL_INSTANCES);
        }
    }


}
