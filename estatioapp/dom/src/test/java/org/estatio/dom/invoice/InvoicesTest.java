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
package org.estatio.dom.invoice;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.asset.Property;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.numerator.Numerators;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class InvoicesTest {

    FinderInteraction finderInteraction;

    InvoiceRepository invoiceRepository;
    InvoiceMenu invoiceMenu;
    EstatioNumeratorRepository estatioNumeratorRepository;

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
        lease = new Lease(){
            @Override
            public Property getProperty() {
                return null;
            }
        };
        invoiceStatus = InvoiceStatus.APPROVED;
        dueDate = new LocalDate(2013,4,1);

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

        invoiceMenu = new InvoiceMenu() {

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

        estatioNumeratorRepository = new EstatioNumeratorRepository() {
        };
    }

    public static class FindMatchingInvoices extends InvoicesTest {

        @Test
        public void happyCase() {

            invoiceRepository.findMatchingInvoices(seller, buyer, paymentMethod, lease, invoiceStatus, dueDate);

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
            assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Invoice.class));
            assertThat(finderInteraction.getQueryName(), is("findMatchingInvoices"));
            assertThat(finderInteraction.getArgumentsByParameterName().get("buyer"), is((Object)buyer));
            assertThat(finderInteraction.getArgumentsByParameterName().get("seller"), is((Object)seller));
            assertThat(finderInteraction.getArgumentsByParameterName().get("paymentMethod"), is((Object)paymentMethod));
            assertThat(finderInteraction.getArgumentsByParameterName().get("lease"), is((Object)lease));
            assertThat(finderInteraction.getArgumentsByParameterName().get("status"), is((Object)invoiceStatus));
            assertThat(finderInteraction.getArgumentsByParameterName().get("dueDate"), is((Object)dueDate));

            assertThat(finderInteraction.getArgumentsByParameterName().size(), is(6));
        }


        @Test
        public void whenMany_returnsFirst() {

            final Invoice invoice1 = new Invoice();
            final Invoice invoice2 = new Invoice();
            final Invoice invoice3 = new Invoice();

            invoiceRepository = new InvoiceRepository() {
                @Override
                @Action(hidden = Where.EVERYWHERE, semantics = SemanticsOf.SAFE)
                public List<Invoice> findMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, Lease lease, InvoiceStatus invoiceStatus, LocalDate dueDate) {
                    return Arrays.asList(invoice1, invoice2, invoice3);
                }
            };

            assertThat(invoiceRepository.findMatchingInvoice(null, null, null, null, null, null), is(invoice1));
        }

        @Test
        public void whenEmpty_returnsNull() {

            invoiceRepository = new InvoiceRepository() {
                @Override
                @Action(hidden = Where.EVERYWHERE, semantics = SemanticsOf.SAFE)
                public List<Invoice> findMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, Lease lease, InvoiceStatus invoiceStatus, LocalDate dueDate) {
                    return Arrays.<Invoice>asList();
                }
                @Override
                public Invoice newInvoice(final ApplicationTenancy applicationTenancy, Party seller, Party buyer, PaymentMethod paymentMethod, Currency currency, LocalDate dueDate, Lease lease, String interactionId) {
                    return null;
                }
            };
            assertThat(invoiceRepository.findMatchingInvoice(null, null, null, null, null, null), is(nullValue()));
        }

    }

    public static class AllInvoices extends InvoicesTest {

        @Test
        public void happyCase() {

            invoiceMenu.allInvoices();

            assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_INSTANCES));
        }
    }

    public static class FindXxxNumerator extends InvoicesTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private Numerators mockNumerators;


        @JUnitRuleMockery2.Ignoring
        @Mock
        Property mockProperty;

        private String format;
        private BigInteger lastIncrement;
        private ApplicationTenancy applicationTenancy;


        @Before
        public void setUp() throws Exception {
            format = "0%6d";
            lastIncrement = BigInteger.TEN;

            invoiceRepository = new InvoiceRepository();
            estatioNumeratorRepository = new EstatioNumeratorRepository();
            estatioNumeratorRepository.numerators = mockNumerators;

            applicationTenancy = new ApplicationTenancy();
            applicationTenancy.setPath("/");
        }


        @Test
        public void findCollectionNumberNumerator() {
            context.checking(new Expectations() {
                {
                    oneOf(mockNumerators).findGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, null);
                }
            });
            estatioNumeratorRepository.findCollectionNumberNumerator();
        }

        @Test
        public void createCollectionNumberNumerator() {
            context.checking(new Expectations() {
                {
                    oneOf(mockNumerators).createGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, format, lastIncrement, applicationTenancy);
                }
            });
            estatioNumeratorRepository.createCollectionNumberNumerator(format, lastIncrement, applicationTenancy);
        }

        @Action(hidden = Where.EVERYWHERE)
        public void findInvoiceNumberNumerator() {
            context.checking(new Expectations() {
                {
                    oneOf(mockNumerators).createGlobalNumerator(Constants.COLLECTION_NUMBER_NUMERATOR_NAME, format, lastIncrement, applicationTenancy);
                }
            });
            estatioNumeratorRepository.findInvoiceNumberNumerator(mockProperty, applicationTenancy);
        }

        @Action(hidden = Where.EVERYWHERE)
        public void createInvoiceNumberNumerator(
                final Property property,
                final String format,
                final BigInteger lastIncrement) {

            context.checking(new Expectations() {
                {
                    oneOf(mockNumerators).createScopedNumerator(Constants.INVOICE_NUMBER_NUMERATOR_NAME, mockProperty, format, lastIncrement, applicationTenancy);
                }
            });
            estatioNumeratorRepository.createInvoiceNumberNumerator(mockProperty, format, lastIncrement, applicationTenancy);
        }


    }


}
