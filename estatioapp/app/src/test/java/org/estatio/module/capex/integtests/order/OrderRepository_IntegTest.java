package org.estatio.module.capex.integtests.order;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.asset.fixtures.property.enums.Property_enum;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.capex.dom.order.approval.OrderApprovalState;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderRepository_IntegTest extends CapexModuleIntegTestAbstract {

    @Inject OrderRepository orderRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {

                executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.builder());
                executionContext.executeChild(this, OrganisationAndComms_enum.TopModelGb.builder());
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
            Order orderMade1 = orderRepository.create(property, "123", sellerOrderReference, orderDate.plusDays(4),
                    orderDate, seller, null, null, "/GBR", null);
            Order orderMade2 = orderRepository.create(property, "456", sellerOrderReference, orderDate.plusDays(5),
                    orderDate.plusDays(1), seller, null, null, "/GBR", null);

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

    public static class FindBySellerAndApprovalStates extends OrderRepository_IntegTest {

        @Test
        public void findBySellerAndApprovalStates_works() throws Exception {
            // given
            String sellerOrderReference = "123-456-7";
            LocalDate orderDate = new LocalDate(2017, 1, 1);

            Organisation seller = OrganisationAndComms_enum.TopModelGb.findUsing(serviceRegistry);
            Property property = Property_enum.OxfGb.findUsing(serviceRegistry);

            Order orderMade1 = orderRepository.create(property, "123", sellerOrderReference, orderDate.plusDays(4),
                    orderDate, seller, null, null, "/GBR", OrderApprovalState.NEW);
            Order orderMade2 = orderRepository.create(property, "456", sellerOrderReference, orderDate.plusDays(5),
                    orderDate.plusDays(1), seller, null, null, "/GBR", OrderApprovalState.APPROVED);
            Order orderMade3 = orderRepository.create(property, "789", sellerOrderReference, orderDate.plusDays(6),
                    orderDate, seller, null, null, "/GBR", OrderApprovalState.DISCARDED);
            Order orderMade4 = orderRepository.create(property, "012", sellerOrderReference, orderDate.plusDays(7),
                    orderDate.plusDays(1), seller, null, null, "/GBR", null);

            assertThat(orderRepository.findBySeller(seller)).contains(orderMade1, orderMade2, orderMade3, orderMade4);

            // when
            List<Order> ordersByApprovalState = orderRepository.findBySellerPartyAndApprovalStates(seller, Arrays.asList(OrderApprovalState.NEW, OrderApprovalState.APPROVED, null));

            // then
            assertThat(ordersByApprovalState).contains(orderMade1, orderMade2, orderMade4);
        }
    }

    @Inject PartyRepository partyRepository;

    @Inject PropertyRepository propertyRepository;
}
