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
package org.estatio.module.capex.integtests.migration;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.capex.app.OrderInvoiceImportMenu;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectItem;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.dom.task.TaskRepository;
import org.estatio.module.capex.fixtures.charge.IncomingChargeFixture;
import org.estatio.module.capex.fixtures.orderinvoice.OrderInvoiceFixture;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderInvoiceImport_IntegTest extends CapexModuleIntegTestAbstract {

    public static class LoadFixtures extends OrderInvoiceImport_IntegTest {

        @Before
        public void setupData() {
//            runFixtureScript(new EstatioBaseLineFixture(), new IncomingChargeFixture(), new PropertyAndOwnerAndManagerForOxfGb());

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final FixtureScript.ExecutionContext executionContext) {
                    executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.toFixtureScript());
                }
            });

        }

        @Inject
        private OrderRepository orderRepository;

        @Inject
        private IncomingInvoiceRepository incomingInvoiceRepository;

        @Inject
        private IncomingInvoiceItemRepository incomingInvoiceItemRepository;

        @Inject
        private ProjectRepository projectRepository;

        @Inject
        private OrderInvoiceImportMenu orderInvoiceImportMenu;

        @Inject
        private OrderItemRepository orderItemRepository;

        @Inject
        private OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

        @Inject
        private IncomingInvoiceApprovalStateTransition.Repository incomingInvoiceApprovalStateTransitionRepository;

        @Inject
        private TaskRepository taskRepository;

        @Before
        public void setUp() throws Exception {

        }

        @Test
        public void happy_case() throws Exception {

            // given
            List<Order> orders = orderRepository.listAll();
            List<IncomingInvoice> incomingInvoices = incomingInvoiceRepository.listAll();
            List<Project> projects = projectRepository.listAll();
            List<OrderItemInvoiceItemLink> links = orderItemInvoiceItemLinkRepository.listAll();

            assertThat(orders).isEmpty();
            assertThat(incomingInvoices).isEmpty();
            assertThat(projects).isEmpty();
            assertThat(links).isEmpty();

            // when
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final FixtureScript.ExecutionContext executionContext) {
                    executionContext.executeChild(this, new IncomingChargeFixture());
                    executionContext.executeChild(this, new OrderInvoiceFixture());
                }
            });

            // then orders, invoices and supporting projects created and invoices are linked back to orders.
            orders = orderRepository.listAll();
            incomingInvoices = incomingInvoiceRepository.listAll();
            projects = projectRepository.listAll();
            links = orderItemInvoiceItemLinkRepository.listAll();
            final List<IncomingInvoiceApprovalStateTransition> invoiceTransitions =
                    incomingInvoiceApprovalStateTransitionRepository.listAll();

            assertThat(orders).isNotEmpty();
            assertThat(incomingInvoices).isNotEmpty();
            assertThat(projects).isNotEmpty();
            assertThat(links).isNotEmpty();
            assertThat(links.size()).isEqualTo(3);
            //EST-1335: imported invoices should not have tasks attached
            assertThat(taskRepository.findTasksIncomplete().size()).isEqualTo(0);

            OrderItem orderItem1 = orders.get(0).getItems().first();
            OrderItem orderItem2 = orders.get(1).getItems().first();
            IncomingInvoiceItem invoiceItem1 = (IncomingInvoiceItem) incomingInvoices.get(0).getItems().first();
            IncomingInvoiceItem invoiceItem2 = (IncomingInvoiceItem) incomingInvoices.get(1).getItems().first();
            IncomingInvoiceItem invoiceItem3 = (IncomingInvoiceItem) incomingInvoices.get(2).getItems().first();

            assertThat(links.get(0).getOrderItem()).isEqualTo(orderItem1);
            assertThat(links.get(0).getInvoiceItem()).isEqualTo(invoiceItem1);
            assertThat(links.get(1).getOrderItem()).isEqualTo(orderItem1);
            assertThat(links.get(1).getInvoiceItem()).isEqualTo(invoiceItem2);
            assertThat(links.get(2).getOrderItem()).isEqualTo(orderItem2);
            assertThat(links.get(2).getInvoiceItem()).isEqualTo(invoiceItem3);

            // check amounts on Invoice filled in (EST-1663)
            IncomingInvoice invoice1 = (IncomingInvoice) invoiceItem1.getInvoice();
            assertThat(invoice1.getNetAmount()).isEqualTo(invoiceItem1.getNetAmount());
            assertThat(invoice1.getGrossAmount()).isEqualTo(invoiceItem1.getGrossAmount());
            assertThat(invoice1.getVatAmount()).isEqualTo(invoiceItem1.getVatAmount());

            Project project = projects.get(0);
            assertThat(project.getItems().size()).isEqualTo(2);

            ProjectItem projectItem = project.getItems().first();
            assertThat(orderItemRepository.findByProjectAndCharge(project, projectItem.getCharge()).size()).isEqualTo(1);
            assertThat(incomingInvoiceItemRepository.findByProjectAndCharge(project, projectItem.getCharge()).size()).isEqualTo(2);

        }

    }

}