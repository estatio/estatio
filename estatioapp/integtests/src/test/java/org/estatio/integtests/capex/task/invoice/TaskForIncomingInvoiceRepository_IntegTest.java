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
package org.estatio.integtests.capex.task.invoice;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.util.Enums;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.party.PartyRoleTypeEnum;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.party.OrganisationForHelloWorldGb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskForIncomingInvoiceRepository_IntegTest extends EstatioIntegrationTest {

    public static class LoadFixtures extends TaskForIncomingInvoiceRepository_IntegTest {

        @Before
        public void setupData() {

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new PropertyForOxfGb());
                }
            });

        }

        @Inject
        private IncomingInvoiceRepository incomingInvoiceRepository;

        @Inject
        private PartyRepository partyRepository;

        @Inject
        private IncomingInvoiceApprovalStateTransition.Repository incomingInvoiceStateTransitionRepository;

        @Before
        public void setUp() throws Exception {

        }

        @Test
        public void happy_case() throws Exception {

            final Party buyer = partyRepository.findPartyByReference(OrganisationForHelloWorldGb.REF);
            final Party seller = partyRepository.findPartyByReference(OrganisationForTopModelGb.REF);

            final IncomingInvoice invoice = incomingInvoiceRepository.create(IncomingInvoiceType.CAPEX,
                    "TEST", "/", buyer, seller, new LocalDate(2016, 1, 1), new LocalDate(2016, 2, 1), PaymentMethod.BANK_TRANSFER, InvoiceStatus.NEW, null, null);

            // When
            incomingInvoiceStateTransitionRepository
                    .create(invoice,
                            IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR,
                            IncomingInvoiceApprovalState.NEW,
                            PartyRoleTypeEnum.COUNTRY_DIRECTOR,
                            Enums.getFriendlyNameOf(IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR));

            // Then
            final List<IncomingInvoiceApprovalStateTransition> tasks =  incomingInvoiceStateTransitionRepository.findByDomainObject(invoice);

            assertThat(tasks.size()).isEqualTo(1);
        }

    }

}