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
package org.estatio.integtests.capex;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.capex.dom.impmgr.OrderInvoiceImportMenu;
import org.estatio.capex.dom.impmgr.OrderInvoiceLine;
import org.estatio.capex.dom.impmgr.OrderInvoiceSheet;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.dom.project.Project;
import org.estatio.dom.project.ProjectRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderInvoiceImportMenu_IntegTest extends EstatioIntegrationTest {

    public static class LoadFixtures extends OrderInvoiceImportMenu_IntegTest {

        @Before
        public void setupData() {
//            runFixtureScript(new EstatioBaseLineFixture(), new IncomingChargeFixture(), new PropertyForOxfGb());

            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(final FixtureScript.ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new PropertyForOxfGb());
                }
            });

        }

        @Inject
        private OrderRepository orderRepository;

        @Inject
        private IncomingInvoiceRepository incomingInvoiceRepository;

        @Inject
        private ProjectRepository projectRepository;

        @Inject
        OrderInvoiceImportMenu orderInvoiceImportMenu;

        @Before
        public void setUp() throws Exception {

        }

        @Test
        public void happy_case() throws Exception {

            // given
            List<Order> orders = orderRepository.listAll();
            List<IncomingInvoice> incomingInvoices = incomingInvoiceRepository.listAll();
            List<Project> projects = projectRepository.listAll();

            assertThat(orders).isEmpty();
            assertThat(incomingInvoices).isEmpty();
            assertThat(projects).isEmpty();

            // when
            final Blob blob =
                    new Blob("OrderInvoiceImportForDemo.xlsx",
                             ExcelService.XSLX_MIME_TYPE,
                             readBytesFrom("OrderInvoiceImportForDemo.xlsx"));

            OrderInvoiceSheet sheet =
                    orderInvoiceImportMenu.importOrdersAndInvoices2("OXFORD", blob);


            // then creates lines but not yet any orders etc
            List<OrderInvoiceLine> lines = sheet.getLines();
            assertThat(lines).isNotEmpty();

            assertThat(orders).isEmpty();


            // when
            sheet.apply();

            // then
            assertThat(orders).isNotEmpty();
            assertThat(incomingInvoices).isNotEmpty();
            assertThat(projects).isNotEmpty();

        }

        private byte[] readBytesFrom(final String resourceName) throws IOException {
            URL resource = Resources.getResource(getClass(), resourceName);
            return Resources.toByteArray(resource);
        }

    }

}