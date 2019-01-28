package org.estatio.module.capex.dom.order;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.charge.dom.Charge;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class OrderItem_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    EventBusService mockEventBusService;

    private IncomingInvoiceItem newIncomingInvoiceItem() {
        final IncomingInvoiceItem incomingInvoiceItem = new IncomingInvoiceItem();
        final IncomingInvoice incomingInvoice = new IncomingInvoice() {
            @Override protected EventBusService getEventBusService() {
                return mockEventBusService;
            }
        };
        context.ignoring(mockEventBusService);

        incomingInvoiceItem.setInvoice(incomingInvoice);
        return incomingInvoiceItem;
    }


    @Test
    public void isInvoiced_works() throws Exception {

        // given
        OrderItem orderItem = new OrderItem();
        orderItem.setNetAmount(new BigDecimal("100.00"));
        IncomingInvoiceItem item1 = newIncomingInvoiceItem();
        IncomingInvoiceItem item2 = newIncomingInvoiceItem();

        // when
        item1.setNetAmount(new BigDecimal("50.00"));
        item2.setNetAmount(new BigDecimal("49.99"));
        orderItem.orderItemInvoiceItemLinkRepository = setupOrderItemInvoiceItemLinkRepository(item1, item2);

        // then
        assertThat(orderItem.isInvoiced()).isFalse();

        // and when
        item1.setNetAmount(new BigDecimal("50.00"));
        item2.setNetAmount(new BigDecimal("50.00"));
        orderItem.orderItemInvoiceItemLinkRepository = setupOrderItemInvoiceItemLinkRepository(item1, item2);

        // then
        assertThat(orderItem.isInvoiced()).isTrue();

        // and when
        item1.setNetAmount(new BigDecimal("50.00"));
        item2.setNetAmount(new BigDecimal("50.01"));
        orderItem.orderItemInvoiceItemLinkRepository = setupOrderItemInvoiceItemLinkRepository(item1, item2);

        // then
        assertThat(orderItem.isInvoiced()).isTrue();

    }

    @Test
    public void isInvoiced_works_with_negative_amounts() throws Exception {

        // given
        OrderItem orderItem = new OrderItem();
        orderItem.setNetAmount(new BigDecimal("-100.00"));
        IncomingInvoiceItem item1 = newIncomingInvoiceItem();
        IncomingInvoiceItem item2 = newIncomingInvoiceItem();

        // when
        item1.setNetAmount(new BigDecimal("-50.00"));
        item2.setNetAmount(new BigDecimal("-49.99"));
        orderItem.orderItemInvoiceItemLinkRepository = setupOrderItemInvoiceItemLinkRepository(item1, item2);

        // then
        assertThat(orderItem.isInvoiced()).isFalse();

        // and when
        item1.setNetAmount(new BigDecimal("-50.00"));
        item2.setNetAmount(new BigDecimal("-50.00"));
        orderItem.orderItemInvoiceItemLinkRepository = setupOrderItemInvoiceItemLinkRepository(item1, item2);

        // then
        assertThat(orderItem.isInvoiced()).isTrue();

        // and when
        item1.setNetAmount(new BigDecimal("-50.00"));
        item2.setNetAmount(new BigDecimal("-50.01"));
        orderItem.orderItemInvoiceItemLinkRepository = setupOrderItemInvoiceItemLinkRepository(item1, item2);

        // then
        assertThat(orderItem.isInvoiced()).isTrue();

    }

    @Test
    public void isInvoiced_works_when_no_netamount_on_orderItem() throws Exception {
        // given
        OrderItem orderItem = new OrderItem();
        orderItem.setNetAmount(null); // explicit for this test
        IncomingInvoiceItem item1 = newIncomingInvoiceItem();
        IncomingInvoiceItem item2 = newIncomingInvoiceItem();

        // when
        orderItem.orderItemInvoiceItemLinkRepository = setupOrderItemInvoiceItemLinkRepository(item1, item2);

        // then
        assertThat(orderItem.isInvoiced()).isFalse();
    }

    @Test
    public void isInvoiced_works_when_no_netamount_on_invoiceItem() throws Exception {
        // given
        OrderItem orderItem = new OrderItem();
        orderItem.setNetAmount(new BigDecimal("100.00"));
        IncomingInvoiceItem item1 = newIncomingInvoiceItem();
        IncomingInvoiceItem item2 = newIncomingInvoiceItem();

        // when
        item1.setNetAmount(null); // explicit for this test
        item2.setNetAmount(new BigDecimal("50.00"));
        orderItem.orderItemInvoiceItemLinkRepository = setupOrderItemInvoiceItemLinkRepository(item1, item2);

        // then
        assertThat(orderItem.isInvoiced()).isFalse();
    }

    @Test
    public void isInvoiced_works_with_discarded_invoice() throws Exception {
        // given
        OrderItem orderItem = new OrderItem();
        orderItem.setNetAmount(new BigDecimal("100.00"));
        IncomingInvoice discardedInvoice = new IncomingInvoice();
        discardedInvoice.setApprovalState(IncomingInvoiceApprovalState.DISCARDED);
        IncomingInvoiceItem item1 = newIncomingInvoiceItem();
        item1.setInvoice(discardedInvoice);
        IncomingInvoice invoice = new IncomingInvoice();
        IncomingInvoiceItem item2 = newIncomingInvoiceItem();
        item2.setInvoice(invoice);

        // when
        item1.setNetAmount(new BigDecimal("45.00")); // should be discarded
        item2.setNetAmount(new BigDecimal("55.00"));
        orderItem.orderItemInvoiceItemLinkRepository = setupOrderItemInvoiceItemLinkRepository(item1, item2);

        // then
        assertThat(orderItem.isInvoiced()).isFalse();
    }

    private OrderItemInvoiceItemLinkRepository setupOrderItemInvoiceItemLinkRepository(final IncomingInvoiceItem item1, final IncomingInvoiceItem item2){
        OrderItemInvoiceItemLink link1 = new OrderItemInvoiceItemLink();
        OrderItemInvoiceItemLink link2 = new OrderItemInvoiceItemLink();

        link1.setInvoiceItem(item1);
        link1.setNetAmount(item1.getNetAmount());
        link2.setInvoiceItem(item2);
        link2.setNetAmount(item2.getNetAmount());

        OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository = new OrderItemInvoiceItemLinkRepository(){
            @Override
            public List<OrderItemInvoiceItemLink> findByOrderItem(
                    final OrderItem orderItem) {
                return Arrays.asList(link1, link2);
            }
        };

        return orderItemInvoiceItemLinkRepository;
    }

    @Test
    public void netAmountOutstanding_works_when_netAmount_is_null() throws Exception {

        // given
        OrderItem item = new OrderItem();

        // when, then
        assertThat(item.netAmountOutstanding()).isEqualTo(BigDecimal.ZERO);

    }

    @Test
    public void netAmountOutstanding_works_with_netAmount_negative() throws Exception {

        // given
        OrderItem item = new OrderItem() {
            @Override
            BigDecimal netAmountInvoiced() {
                return new BigDecimal("-10.00");
            }
        };
        item.setNetAmount(new BigDecimal("-100.00"));

        // when, then
        assertThat(item.netAmountOutstanding()).isEqualTo(new BigDecimal("-90.00"));

    }

    @Test
    public void chargeNotUsedOnOrder_works() throws Exception {
        // given
        Order order = new Order();
        OrderItem orderItem = new OrderItem();
        orderItem.setOrdr(order);
        order.getItems().add(orderItem);

        Charge chargeOnItem = new Charge();
        orderItem.setCharge(chargeOnItem);

        Charge someOtherCharge = new Charge();

        // when, then
        assertThat(orderItem.chargeNotUsedOnOrder(someOtherCharge)).isTrue();
        assertThat(orderItem.chargeNotUsedOnOrder(chargeOnItem)).isFalse();
    }

    @Test
    public void gross_amount_required_for_non_italian_order_item_only() throws Exception {
        // given
        Order order = new Order();
        order.setAtPath("/FRA");
        OrderItem orderItem = new OrderItem();
        orderItem.setOrdr(order);
        orderItem.setDescription("something");
        orderItem.setCharge(new Charge());
        orderItem.setStartDate(LocalDate.now());
        orderItem.setEndDate(LocalDate.now());
        orderItem.setNetAmount(BigDecimal.TEN);

        // when, then
        assertThat(orderItem.getOrdr().getAtPath()).startsWith("/FRA");
        assertThat(orderItem.reasonIncomplete()).isEqualTo("gross amount required");

        // and when
        order.setAtPath("/ITA");
        // then
        assertThat(orderItem.getOrdr().getAtPath()).startsWith("/ITA");
        assertThat(orderItem.reasonIncomplete()).isNull();
    }

    @Test
    public void property_required_for_non_italian_order_when_having_project() throws Exception {

        // given
        Order order = new Order();
        order.setAtPath("/FRA");
        OrderItem orderItem = new OrderItem();
        orderItem.setOrdr(order);
        Project project = new Project();
        orderItem.setProject(project);
        orderItem.setDescription("something");
        orderItem.setCharge(new Charge());
        orderItem.setStartDate(LocalDate.now());
        orderItem.setEndDate(LocalDate.now());
        orderItem.setNetAmount(BigDecimal.TEN);
        orderItem.setGrossAmount(BigDecimal.TEN);

        // when, then
        assertThat(orderItem.reasonIncomplete()).isEqualTo("when project filled in then property required");
    }

    @Test
    public void property_NOT_required_for_italian_order_when_having_project() throws Exception {

        // given
        Order order = new Order();
        order.setAtPath("/ITA");
        OrderItem orderItem = new OrderItem();
        orderItem.setOrdr(order);
        Project project = new Project();
        orderItem.setProject(project);
        orderItem.setDescription("something");
        orderItem.setCharge(new Charge());
        orderItem.setStartDate(LocalDate.now());
        orderItem.setEndDate(LocalDate.now());
        orderItem.setNetAmount(BigDecimal.TEN);

        // when, then
        assertThat(orderItem.reasonIncomplete()).isNull();
    }

    @Mock ProjectRepository mockProjectRepository;

    @Test
    public void choices_edit_project_when_having_no_project_works() throws Exception {

        // given
        Order order = new Order();
        OrderItem orderItem = new OrderItem();
        orderItem.setOrdr(order);
        orderItem.projectRepository = mockProjectRepository;

        assertThat(orderItem.getFixedAsset()).isNull();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockProjectRepository).findWithoutFixedAsset();
            will(returnValue(Arrays.asList(new Project())));
        }});

        // when, then
        assertThat(orderItem.choices0EditProject()).isNotNull();

    }
    
}