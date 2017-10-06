package org.estatio.integtests.capex.order;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderItemRepository;
import org.estatio.capex.fixture.orderinvoice.OrderInvoiceFixture;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForOxfGb;
import org.estatio.fixture.party.OrganisationForYoukeaSe;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderItemRepository_IntegTest extends EstatioIntegrationTest {

    @Inject OrderItemRepository orderItemRepository;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new PropertyForOxfGb());
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
            Property property = propertyRepository.findPropertyByReference(PropertyForOxfGb.REF);

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
