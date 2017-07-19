package org.estatio.capex.dom.order.viewmodel;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderItemRepository;

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
        incomingDocAsOrderViewModel.clockService = mockClockService;
        Order.addItem mixin = new Order.addItem(order);
        incomingDocAsOrderViewModel.factoryService = new FactoryService() {
            @Override public <T> T instantiate(final Class<T> aClass) {
                return null;
            }

            @Override public <T> T mixin(final Class<T> aClass, final Object o) {
                return (T) mixin;
            }
        };
        mixin.orderItemRepository = mockOrderItemRepository;

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

}