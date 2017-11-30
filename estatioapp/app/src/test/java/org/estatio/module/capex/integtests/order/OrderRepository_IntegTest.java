package org.estatio.module.capex.integtests.order;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.asset.fixtures.property.personas.PropertyAndUnitsAndOwnerAndManagerForOxfGb;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderRepository_IntegTest extends CapexModuleIntegTestAbstract {

    @Inject OrderRepository orderRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {

                executionContext.executeChild(this, new PropertyAndUnitsAndOwnerAndManagerForOxfGb());
                executionContext.executeChild(this, OrganisationAndComms_enum.TopModelGb.toFixtureScript());
            }
        });
    }

    public static class FindBySellerOrderReferenceAndSellerAndOrderDate extends OrderRepository_IntegTest {

        @Test
        public void find_by_sellerOrderReference_and_seller_and_optional_orderDate_works() {
            // given
            String sellerOrderReference = "123-456-7";
            LocalDate orderDate = new LocalDate(2017, 1, 1);


            Party seller = OrganisationAndComms_enum.TopModelGb.findUsing(serviceRegistry);
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);
            Order orderMade1 = orderRepository.create(property,"123", sellerOrderReference, orderDate.plusDays(4),orderDate, seller, null, "/GBR", null);
            Order orderMade2 = orderRepository.create(property,"456", sellerOrderReference, orderDate.plusDays(5),orderDate.plusDays(1), seller, null, "/GBR", null);

            // when
            Order orderFound = orderRepository.findBySellerOrderReferenceAndSellerAndOrderDate(sellerOrderReference, seller, orderDate);
            List<Order> ordersFound = orderRepository.findBySellerOrderReferenceAndSeller(sellerOrderReference, seller);

            // then
            assertThat(orderFound).isEqualTo(orderMade1);

            assertThat(ordersFound.size()).isEqualTo(2);
            assertThat(ordersFound).contains(orderMade1);
            assertThat(ordersFound).contains(orderMade2);

        }

    }

    @Inject PartyRepository partyRepository;

    @Inject PropertyRepository propertyRepository;
}
