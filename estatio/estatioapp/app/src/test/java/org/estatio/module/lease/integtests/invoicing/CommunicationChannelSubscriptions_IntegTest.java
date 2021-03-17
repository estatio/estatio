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
package org.estatio.module.lease.integtests.invoicing;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.invoice.subscriptions.CommunicationChannelSubscriptions;
import org.estatio.module.lease.app.NumeratorForOutgoingInvoicesMenu;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.lease.fixtures.invoice.enums.InvoiceForLease_enum;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.numerator.dom.NumeratorRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

public class CommunicationChannelSubscriptions_IntegTest extends LeaseModuleIntegTestAbstract {

    @Inject
    InvoiceRepository invoiceRepository;

    @Inject
    InvoiceForLeaseRepository invoiceForLeaseRepository;

    @Inject
    NumeratorForOutgoingInvoicesMenu estatioNumeratorRepository;

    @Inject
    NumeratorRepository numeratorRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;

    @Inject
    BookmarkService bookmarkService;


    public static class FindInvoiceRepositoryTest extends CommunicationChannelSubscriptions_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {

                    executionContext.executeChild(this, InvoiceForLease_enum.OxfPoison003Gb.builder());
                    executionContext.executeChild(this, InvoiceForLease_enum.KalPoison001Nl.builder());
                }
            });
        }

        private static String runId = "2014-02-16T02:30:03.156 - OXF - [OXF-TOPMODEL-001] - [RENT, SERVICE_CHARGE, TURNOVER_RENT, TAX] - 2012-01-01 - 2012-01-01/2012-01-02";

        Property propertyKal;

        Lease lease;

        Party buyer;

        Party seller;

        ApplicationTenancy applicationTenancy;

        @Before
        public void setUp() throws Exception {
            applicationTenancy = applicationTenancyRepository.findByPath(ApplicationTenancy_enum.Nl.getPath());
            seller = InvoiceForLease_enum.KalPoison001Nl.getSeller_d().findUsing(serviceRegistry);
            buyer = InvoiceForLease_enum.KalPoison001Nl.getBuyer_d().findUsing(serviceRegistry);
            lease = InvoiceForLease_enum.KalPoison001Nl.getLease_d().findUsing(serviceRegistry);

            propertyKal = Property_enum.KalNl.findUsing(serviceRegistry);

            InvoiceForLease invoice = invoiceForLeaseRepository.findOrCreateMatchingInvoice(
                    applicationTenancy,
                    seller,
                    buyer,
                    PaymentMethod.DIRECT_DEBIT,
                    lease,
                    InvoiceStatus.NEW,
                    InvoiceForLease_enum.KalPoison001Nl.getDueDate(),
                    null);
            invoice.setRunId(runId);
            Assert.assertNotNull(invoice);
        }

        public static class OnRemoveTest extends FindInvoiceRepositoryTest {

            @Inject
            RepositoryService repositoryService;
            CommunicationChannel originalChannel;
            CommunicationChannel replacementChannel;

            @Before
            public void lookupCommChannel() throws Exception {
                final List<CommunicationChannel> communicationChannels = repositoryService
                        .allInstances(CommunicationChannel.class);
                assertThat(communicationChannels.size()).isGreaterThanOrEqualTo(2);
                originalChannel = communicationChannels.get(0);
                replacementChannel = communicationChannels.get(1);
            }

            @Inject
            CommunicationChannelSubscriptions communicationChannelSubscriptions;

            @Test
            public void when_none_and_no_replacement() {

                // when
                wrap(originalChannel).remove(null);

                // then ok..

            }

            @Test
            public void when_some_and_replacement() {

                // given
                final List<Invoice> invoiceList = repositoryService.allInstances(Invoice.class);
                assertThat(invoiceList.size()).isGreaterThanOrEqualTo(2);
                final Invoice invoice0 = invoiceList.get(0);
                final Invoice invoice1 = invoiceList.get(1);

                invoice0.setSendTo(originalChannel);
                invoice1.setSendTo(replacementChannel);

                // when
                wrap(originalChannel).remove(replacementChannel);

                // then
                assertThat(invoice0.getSendTo()).isSameAs(replacementChannel);
            }

            @Test
            public void when_some_and_not_yet_invoiced_and_no_replacement() {

                // given
                final List<Invoice> invoiceList = repositoryService.allInstances(Invoice.class);
                assertThat(invoiceList.size()).isGreaterThanOrEqualTo(2);
                final Invoice invoice0 = invoiceList.get(0);
                final Invoice invoice1 = invoiceList.get(1);

                invoice0.setSendTo(originalChannel);
                invoice1.setSendTo(replacementChannel);

                // expect
                expectedExceptions.expect(InvalidException.class);
                expectedExceptions.expectMessage(containsString("Communication channel is being used (as the 'sendTo' channel for 1 invoice(s); provide a replacement"));

                // when
                wrap(originalChannel).remove(null);
            }


            @Test
            public void when_some_but_already_invoiced() {

                // given
                final List<Invoice> invoiceList = repositoryService.allInstances(Invoice.class);
                assertThat(invoiceList.size()).isGreaterThanOrEqualTo(2);
                final Invoice invoice0 = invoiceList.get(0);
                final Invoice invoice1 = invoiceList.get(1);

                invoice0.setSendTo(originalChannel);
                invoice1.setSendTo(replacementChannel);

                invoice0.setInvoiceNumber("XXX-000123");

                // when
                wrap(originalChannel).remove(null);

                // then
                assertThat(invoice0.getSendTo()).isNull();
            }
        }
    }

    public static class FindInvoiceRepositoryByRunId extends CommunicationChannelSubscriptions_IntegTest {

        private Property kalProperty;

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {

                    executionContext.executeChildren(this,
                            InvoiceForLease_enum.OxfPoison003Gb,
                            InvoiceForLease_enum.KalPoison001Nl);
                }
            });
        }

        private static String runId = "2014-02-16T02:30:03.156 - OXF - [OXF-TOPMODEL-001] - [RENT, SERVICE_CHARGE, TURNOVER_RENT, TAX] - 2012-01-01 - 2012-01-01/2012-01-02";

        @Before
        public void setUp() throws Exception {
            final ApplicationTenancy applicationTenancy = applicationTenancyRepository.findByPath(ApplicationTenancy_enum.Gb.getPath());
            final Party seller = InvoiceForLease_enum.OxfPoison003Gb.getSeller_d().findUsing(serviceRegistry);
            final Party buyer = InvoiceForLease_enum.OxfPoison003Gb.getBuyer_d().findUsing(serviceRegistry);
            final Lease lease = InvoiceForLease_enum.OxfPoison003Gb.getLease_d().findUsing(serviceRegistry);
            final LocalDate startDate = InvoiceForLease_enum.OxfPoison003Gb.getLease_d().getStartDate().plusYears(1);

            InvoiceForLease invoiceForLease = invoiceForLeaseRepository.findOrCreateMatchingInvoice(
                    applicationTenancy,
                    seller, buyer, PaymentMethod.DIRECT_DEBIT, lease,
                    InvoiceStatus.NEW, startDate, null);
            invoiceForLease.setRunId(runId);
            Assert.assertNotNull(invoiceForLease);
        }

        @Test
        public void byRunId() {
            // when
            List<InvoiceForLease> result = invoiceForLeaseRepository.findInvoicesByRunId(runId);

            // then
            assertThat(result.size(), is(1));
        }

    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class FindOrCreateMatchingInvoice extends CommunicationChannelSubscriptions_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {

                    executionContext.executeChild(this, LeaseItemForRent_enum.OxfPoison003Gb.builder());
                    executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfPoison003Gb.builder());
                    executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfPoison003Gb.builder());
                }
            });
        }

        private ApplicationTenancy applicationTenancy;
        private Party seller;
        private Party buyer;
        private Lease lease;
        private LocalDate invoiceStartDate;

        @Before
        public void setUp() throws Exception {
            applicationTenancy = applicationTenancyRepository.findByPath(ApplicationTenancy_enum.Gb.getPath());
            seller = OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry);
            buyer = OrganisationAndComms_enum.PoisonGb.findUsing(serviceRegistry);
            lease = Lease_enum.OxfPoison003Gb.findUsing(serviceRegistry);
            invoiceStartDate = InvoiceForLease_enum.OxfPoison003Gb.getLease_d().getStartDate().plusYears(1);
        }

        @Test
        public void whenDoesNotExist() {
            // given
            Assert.assertThat(invoiceRepository.allInvoices().isEmpty(), is(true));
            // when
            InvoiceForLease invoice = invoiceForLeaseRepository.findOrCreateMatchingInvoice(
                    applicationTenancy,
                    seller, buyer, PaymentMethod.DIRECT_DEBIT, lease,
                    InvoiceStatus.NEW, invoiceStartDate, null);
            // then
            Assert.assertNotNull(invoice);
            Assert.assertThat(invoiceRepository.allInvoices().isEmpty(), is(false));
        }

        @Test
        public void whenExist() {
            // given
            InvoiceForLease invoice = invoiceForLeaseRepository.findOrCreateMatchingInvoice(
                    applicationTenancy,
                    seller, buyer, PaymentMethod.DIRECT_DEBIT, lease,
                    InvoiceStatus.NEW, invoiceStartDate, null);
            // when
            InvoiceForLease invoice2 = invoiceForLeaseRepository.findOrCreateMatchingInvoice(
                    applicationTenancy,
                    seller, buyer, PaymentMethod.DIRECT_DEBIT, lease,
                    InvoiceStatus.NEW, invoiceStartDate, null);
            // then
            Assert.assertThat(invoice2, is(sameInstance(invoice)));
        }

    }

}