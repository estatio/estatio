package org.estatio.capex.dom.order;

import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.capex.dom.project.Project;
import org.estatio.dom.asset.Property;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;
import org.estatio.dom.party.Organisation;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderItemService_Test {


    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private OrderRepository mockOrderRepository;

    @Mock
    private OrderItemRepository mockOrderItemRepository;

    @Mock
    private OrderItemInvoiceItemLinkRepository mockOrderItemInvoiceItemLinkRepository;

    @Mock
    private BankAccountRepository mockBankAccountRepository;

    @Test
    public void autoCompleteOrderItem_works(){

        List<OrderItem> result;

        // given
        OrderItemService service = new OrderItemService();
        service.orderRepository = mockOrderRepository;
        service.orderItemRepository = mockOrderItemRepository;
        Charge someOtherCharge = new Charge();
        Charge charge = new Charge();
        Project project = new Project();
        Organisation seller = new Organisation();

        OrderItem oi1 = new OrderItem();
        oi1.setCharge(charge);
        oi1.setProject(project);
        oi1.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;

        OrderItem oi2 = new OrderItem();
        oi2.setCharge(charge);
        oi2.setProject(project);
        oi2.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;

        OrderItem oi3 = new OrderItem();
        oi3.setCharge(charge);
        oi3.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;

        OrderItem oi4 = new OrderItem();
        oi4.setCharge(someOtherCharge); // charge is mandatory on OrderItem
        oi4.orderItemInvoiceItemLinkRepository = mockOrderItemInvoiceItemLinkRepository;

        Order o1 = new Order();
        o1.getItems().add(oi1);
        oi1.setOrdr(o1);
        o1.setSeller(seller);

        Order o2 = new Order();
        o2.getItems().add(oi2);
        oi2.setOrdr(o2);

        Order o3 = new Order();
        o3.getItems().add(oi3);
        oi3.setOrdr(o3);

        Order o4 = new Order();
        o4.getItems().add(oi4);
        oi4.setOrdr(o4);

        // expect
        context.checking(new Expectations() {
            {
                allowing(mockOrderItemInvoiceItemLinkRepository).findByOrderItem(with(any(OrderItem.class)));
                will(returnValue(Arrays.asList()));
                allowing(mockOrderRepository).matchBySellerReferenceOrName(with(any(String.class)));
                will(returnValue(Arrays.asList(
                        o1, o2
                )));
                allowing(mockOrderRepository).matchByOrderNumber(with(any(String.class)));
                will(returnValue(Arrays.asList(
                        o2, o3
                )));
                allowing(mockOrderItemRepository).matchByDescription(with(any(String.class)));
                will(returnValue(Arrays.asList(
                        oi3, oi4
                )));
            }

        });

        // when
        result = service.searchOrderItem("***", null, null, null, null);

        // then
        assertThat(result.size()).isEqualTo(4);

        // and when
        result = service.searchOrderItem("***", null, charge, null, null);

        // then
        assertThat(result.size()).isEqualTo(3);

        // and when
        result = service.searchOrderItem("***", null, charge, project, null);

        // then
        assertThat(result.size()).isEqualTo(2);

        // and when
        result = service.searchOrderItem("***", seller, charge, project, null);

        // then
        assertThat(result.size()).isEqualTo(1);

        // and when
        Property property = new Property();
        result = service.searchOrderItem("***", seller, charge, project, property);

        // then
        assertThat(result.size()).isEqualTo(0);

    }

}