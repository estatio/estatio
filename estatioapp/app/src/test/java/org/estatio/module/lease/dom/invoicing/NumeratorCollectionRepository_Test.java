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

import java.math.BigInteger;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.repo.FinderInteraction;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.invoice.dom.Constants;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.numerator.dom.NumeratorAtPathRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyForTesting;

public class NumeratorCollectionRepository_Test {

    FinderInteraction finderInteraction;

    InvoiceRepository invoiceRepository;
    InvoiceForLeaseRepository invoiceForLeaseRepository;

    NumeratorForOutgoingInvoicesRepository estatioNumeratorRepository;

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
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.FIRST_MATCH);
                return null;
            }

            @Override
            protected List<Invoice> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderInteraction.FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderInteraction.FinderMethod.ALL_MATCHES);
                return null;
            }
        };
        invoiceForLeaseRepository = new InvoiceForLeaseRepository() {

        };

        estatioNumeratorRepository = new NumeratorForOutgoingInvoicesRepository();
    }


    public static class FindXxxNumerator extends NumeratorCollectionRepository_Test {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        NumeratorAtPathRepository mockNumeratorAtPathRepository;

        @JUnitRuleMockery2.Ignoring
        @Mock
        Property mockProperty;
        @JUnitRuleMockery2.Ignoring
        @Mock
        Party mockSeller;

        private String format;
        private BigInteger lastIncrement;
        private ApplicationTenancy applicationTenancy;

        @Before
        public void setUp() throws Exception {
            format = "0%6d";
            lastIncrement = BigInteger.TEN;

            invoiceRepository = new InvoiceRepository();
            estatioNumeratorRepository = new NumeratorForOutgoingInvoicesRepository();

            applicationTenancy = new ApplicationTenancy();
            applicationTenancy.setPath("/");
        }

        @Test
        public void findCollectionNumberNumerator() {
            context.checking(new Expectations() {
                {
                    oneOf(mockNumeratorAtPathRepository).findGlobalNumerator(Constants.NumeratorName.COLLECTION_NUMBER, null);
                }
            });
            estatioNumeratorRepository.findCollectionNumberNumerator();
        }

        @Test
        public void createCollectionNumberNumerator() {
            context.checking(new Expectations() {
                {
                    oneOf(mockNumeratorAtPathRepository).createGlobalNumerator(Constants.NumeratorName.COLLECTION_NUMBER, format, lastIncrement, applicationTenancy);
                }
            });
            estatioNumeratorRepository.createCollectionNumberNumerator(format, lastIncrement);
        }

        @Test
        public void findInvoiceNumberNumerator() {
            context.checking(new Expectations() {
                {
                    oneOf(mockNumeratorAtPathRepository).createGlobalNumerator(Constants.NumeratorName.COLLECTION_NUMBER, format, lastIncrement, applicationTenancy);
                }
            });
            estatioNumeratorRepository.findInvoiceNumberNumerator(mockProperty, mockSeller
            );
        }


    }

}
