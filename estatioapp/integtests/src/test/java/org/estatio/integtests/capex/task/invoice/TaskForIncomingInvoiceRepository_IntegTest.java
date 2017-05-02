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

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.rule.IncomingInvoiceTransition;
import org.estatio.capex.dom.invoice.task.TaskForIncomingInvoice;
import org.estatio.capex.dom.invoice.task.TaskForIncomingInvoiceRepository;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.roles.EstatioRole;
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
        private TaskForIncomingInvoiceRepository taskForIncomingInvoiceRepository;

        @Before
        public void setUp() throws Exception {

        }

        @Test
        public void happy_case() throws Exception {

            final Party buyer = partyRepository.findPartyByReference(OrganisationForHelloWorldGb.REF);
            final Party seller = partyRepository.findPartyByReference(OrganisationForTopModelGb.REF);

            final IncomingInvoice invoice = incomingInvoiceRepository.create("TEST", "/", buyer, seller, new LocalDate(2016, 1, 1), new LocalDate(2016, 2, 1), PaymentMethod.BANK_TRANSFER, InvoiceStatus.NEW);

            // When
            taskForIncomingInvoiceRepository.create(invoice, IncomingInvoiceTransition.APPROVE_AS_COUNTRY_DIRECTOR, EstatioRole.COUNTRY_DIRECTOR, "Some description");

            //Then

            final List<TaskForIncomingInvoice> tasks =  taskForIncomingInvoiceRepository.findByInvoice(invoice);

            assertThat(tasks.size()).isEqualTo(1);
        }

    }

}