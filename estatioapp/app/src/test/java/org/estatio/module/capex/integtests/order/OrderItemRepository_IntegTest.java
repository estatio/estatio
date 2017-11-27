package org.estatio.module.capex.integtests.order;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.fixtures.property.personas.PropertyAndUnitsAndOwnerAndManagerForOxfGb;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.capex.fixtures.charge.IncomingChargeFixture;
import org.estatio.module.capex.fixtures.orderinvoice.OrderInvoiceFixture;
import org.estatio.module.capex.integtests.CapexModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.organisation.personas.OrganisationForYoukeaSe;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderItemRepository_IntegTest extends CapexModuleIntegTestAbstract {

    @Inject OrderItemRepository orderItemRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new IncomingChargeFixture());
                executionContext.executeChild(this, new PropertyAndUnitsAndOwnerAndManagerForOxfGb());
                executionContext.executeChild(this, new OrganisationForYoukeaSe());
                executionContext.executeChild(this, new OrderInvoiceFixture());
            }
        });
    }

    public static class FindBySeller extends OrderItemRepository_IntegTest {

        @Test
        public void when_seller_is_null() {
            // given
            Party seller = null;

            // when
            List<OrderItem> orderItems = orderItemRepository.findBySeller(seller);

            // then
            assertThat(orderItems).isEmpty();
        }

        @Test
        public void when_seller_is_not_null() {
            // given
            Party seller = partyRepository.findPartyByReference(OrganisationForYoukeaSe.REF);

            // when
            List<OrderItem> orderItems = orderItemRepository.findBySeller(seller);

            // then
            assertThat(orderItems).isNotEmpty();
            assertThat(orderItems.size()).isEqualTo(1);
        }

    }

    public static class FindBySellerAndPropery extends OrderItemRepository_IntegTest {

        @Test
        public void find_by_seller_and_property_works() {
            // given
            Party seller = partyRepository.findPartyByReference(OrganisationForYoukeaSe.REF);
            Property property = propertyRepository.findPropertyByReference(PropertyAndUnitsAndOwnerAndManagerForOxfGb.REF);

            // when
            List<OrderItem> orderItems = orderItemRepository.findBySellerAndProperty(seller, property);

            // then
            assertThat(orderItems).isNotEmpty();
            assertThat(orderItems.size()).isEqualTo(1);
        }

    }

    @Inject PartyRepository partyRepository;

    @Inject PropertyRepository propertyRepository;
}
