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
package org.estatio.module.capex.integtests.task.invoice;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.util.Enums;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.personas.PropertyAndOwnerAndManagerForOxfGb;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForHelloWorldGb;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForTopModelGb;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskForIncomingInvoiceRepository_IntegTest extends CapexModuleIntegTestAbstract {

    public static class LoadFixtures extends TaskForIncomingInvoiceRepository_IntegTest {

        @Before
        public void setupData() {

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final ExecutionContext executionContext) {

                    executionContext.executeChild(this, new PropertyAndOwnerAndManagerForOxfGb());
                }
            });

        }

        @Inject
        private IncomingInvoiceRepository incomingInvoiceRepository;

        @Inject
        private PartyRepository partyRepository;

        @Inject
        private PropertyRepository propertyRepository;

        @Inject
        private IncomingInvoiceApprovalStateTransition.Repository incomingInvoiceStateTransitionRepository;

        @Before
        public void setUp() throws Exception {

        }

        @Test
        public void happy_case() throws Exception {

            final Party buyer = partyRepository.findPartyByReference(OrganisationForHelloWorldGb.REF);
            final Party seller = partyRepository.findPartyByReference(OrganisationForTopModelGb.REF);
            final Property property = propertyRepository.findPropertyByReference(PropertyAndOwnerAndManagerForOxfGb.REF);

            final IncomingInvoice invoice = incomingInvoiceRepository.create(IncomingInvoiceType.CAPEX,
                    "TEST", property, "/", buyer, seller, new LocalDate(2016, 1, 1), new LocalDate(2016, 2, 1), PaymentMethod.BANK_TRANSFER, InvoiceStatus.NEW, null, null,
                    null);

            // given (the normal setup would create 2 transitions (INSTANTIATE, COMPLETE...)
            incomingInvoiceStateTransitionRepository.deleteFor(invoice);

            // When
            final Person personToAssignToIfAny = null;
            incomingInvoiceStateTransitionRepository
                    .create(invoice,
                            IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR,
                            IncomingInvoiceApprovalState.NEW,
                            PartyRoleTypeEnum.COUNTRY_DIRECTOR,
                            personToAssignToIfAny, Enums.getFriendlyNameOf(IncomingInvoiceApprovalStateTransitionType.APPROVE_AS_COUNTRY_DIRECTOR));

            // Then
            final List<IncomingInvoiceApprovalStateTransition> transitions =  incomingInvoiceStateTransitionRepository.findByDomainObject(invoice);

            assertThat(transitions.size()).isEqualTo(1);
        }

        @Inject
        RepositoryService repositoryService;

    }

}