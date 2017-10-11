package org.estatio.integtests.capex.order;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.togglz.junit.TogglzRule;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.sudo.SudoService;
import org.apache.isis.applib.services.wrapper.DisabledException;

import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.capex.dom.order.approval.OrderApprovalState;
import org.estatio.capex.dom.order.approval.triggers.Order_discard;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.dom.party.PersonRepository;
import org.estatio.dom.togglz.EstatioTogglzFeature;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.invoice.IncomingInvoiceFixture;
import org.estatio.fixture.order.OrderFixture;
import org.estatio.fixture.party.PersonForJonathanPropertyManagerGb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class Order_withLinks_IntegTest extends EstatioIntegrationTest {

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(EstatioTogglzFeature.class);

    @Before
    public void setupData() {

        final OrderFixture orderFixture = new OrderFixture();
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, orderFixture);
                executionContext.executeChild(this, new IncomingInvoiceFixture());
                executionContext.executeChild(this, new PersonForJonathanPropertyManagerGb());
            }
        });
        order = orderFixture.getOrder();
        orderItem = orderFixture.getFirstItem();
    }

    Order order;
    OrderItem orderItem;



    @Test
    public void cannot_discard_when_has_linked_items() throws Exception {

        // given
        assertNotNull(order);
        assertNotNull(orderItem);
        assertThat(linkRepository.findByOrderItem(orderItem)).isNotEmpty();

        assertThat(order.getApprovalState()).isEqualTo(OrderApprovalState.NEW);

        // expecting
        expectedExceptions.expect(DisabledException.class);

        // when
        final Order_discard mixin = mixin(Order_discard.class, order);
        sudoService.sudo(PersonForJonathanPropertyManagerGb.SECURITY_USERNAME, () -> {
            wrap(mixin).act("Discarding junk");
        });

    }


    @Inject SudoService sudoService;

    @Inject ClockService clockService;

    @Inject OrderItemInvoiceItemLinkRepository linkRepository;

    @Inject OrderRepository orderRepository;

    @Inject
    PersonRepository personRepository;

    @Inject IncomingDocumentRepository incomingDocumentRepository;

}
