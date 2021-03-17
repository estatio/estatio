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
package org.estatio.module.capex.integtests.order;

import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.capex.fixtures.order.enums.Order_enum;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.capex.fixtures.charge.builders.IncomingChargesItaXlsxFixture;

public class Order_IntegTest extends CapexModuleIntegTestAbstract {

    @Inject
    OrderRepository orderRepository;

    public static class MatchByOrderNumber extends Order_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new IncomingChargesItaXlsxFixture());
                    executionContext.executeChild(this, Order_enum.italianOrder);
                    executionContext.executeChild(this, Order_enum.italianOrder4112);
                }
            });
        }

        @Test
        public void happyCase() {
            List<Order> ordersFound = orderRepository.matchByOrderNumber("ron*");
            Assertions.assertThat(ordersFound.size()).isEqualTo(2);
        }

    }

}