package org.estatio.module.capex.app.order;

import java.math.BigDecimal;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.party.dom.Organisation;

public class IncomingDocAsOrderViewModel_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    OrderItemRepository mockOrderItemRepository;

    @Mock
    ClockService mockClockService;

    @Test
    public void mixin_save_works() throws Exception {

        // given
        IncomingDocAsOrderViewModel incomingDocAsOrderViewModel = new IncomingDocAsOrderViewModel();
        Order order = new Order();
        order.setAtPath("/FRA");
        incomingDocAsOrderViewModel.clockService = mockClockService;
        order.orderItemRepository = mockOrderItemRepository;

        incomingDocAsOrderViewModel.setDomainObject(order);

        BigDecimal netAmount = new BigDecimal("100.00");
        BigDecimal vatAmount = new BigDecimal("20.00");
        BigDecimal grossAmount = new BigDecimal("120.00");
        incomingDocAsOrderViewModel.setNetAmount(netAmount);
        incomingDocAsOrderViewModel.setVatAmount(vatAmount);
        incomingDocAsOrderViewModel.setGrossAmount(grossAmount);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockClockService).now();
            will(returnValue(new LocalDate(2017,01,01)));
            oneOf(mockOrderItemRepository).upsert(
                    order,
                    null,
                    null,
                    netAmount,
                    vatAmount,
                    grossAmount,
                    null, null, null, null, null, null
            );
        }});

        // when
        incomingDocAsOrderViewModel.save();


    }

    @Mock
    OrderRepository mockOrderRepository;

    @Test
    public void double_order_check_works() throws Exception {

        // given
        IncomingDocAsOrderViewModel incomingDocAsOrderViewModel = new IncomingDocAsOrderViewModel();
        incomingDocAsOrderViewModel.orderRepository = mockOrderRepository;
        Order newOrder = new Order();
        incomingDocAsOrderViewModel.setDomainObject(newOrder);

        String sellerOrderReference = "123-456-7";
        Organisation seller = new Organisation();
        LocalDate orderDate = new LocalDate(2017,01,01);

        Order existingOrder = new Order();

        // expect
        context.checking(new Expectations(){{
            oneOf(mockOrderRepository).findBySellerOrderReferenceAndSellerAndOrderDate(sellerOrderReference, seller, orderDate);
            will(returnValue(newOrder));
            oneOf(mockOrderRepository).findBySellerOrderReferenceAndSeller(sellerOrderReference, seller);
            will(returnValue(Arrays.asList(newOrder, existingOrder)));
        }});

        // when
        incomingDocAsOrderViewModel.setSellerOrderReference(sellerOrderReference);
        incomingDocAsOrderViewModel.setSeller(seller);
        incomingDocAsOrderViewModel.setOrderDate(orderDate);
        String message = incomingDocAsOrderViewModel.doubleOrderCheck();

        // then
        Assertions.assertThat(message).contains("WARNING: Orders with the same seller order reference of this seller are found");

        // and expect
        context.checking(new Expectations(){{
            oneOf(mockOrderRepository).findBySellerOrderReferenceAndSellerAndOrderDate(sellerOrderReference, seller, orderDate);
            will(returnValue(existingOrder));
        }});

        // when
        message = incomingDocAsOrderViewModel.doubleOrderCheck();

        // then
        Assertions.assertThat(message).isEqualTo("WARNING: There is already an order with the same seller order reference and order date for this seller. Please check.");

    }

}