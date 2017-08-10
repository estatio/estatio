package org.estatio.capex.dom.order.itemmixins;

import java.math.BigDecimal;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderItemRepository;
import org.estatio.capex.dom.project.ProjectItem;

public class ProjectItem_InvoicedAmount_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    OrderItemRepository mockOrderItemRepository;

    @Test
    public void orderedAmount_works() throws Exception {

        // given
        BigDecimal expectedTotalInvoicedNetAmountOnItems = new BigDecimal("100.00");
        BigDecimal invoicedNetAmountOnItem1 = new BigDecimal("55.00");
        BigDecimal invoicedNetAmountOnItem2 = new BigDecimal("45.00");

        ProjectItem projectItem = new ProjectItem();
        ProjectItem_InvoicedAmount mixin = new ProjectItem_InvoicedAmount(projectItem);
        mixin.orderItemRepository = mockOrderItemRepository;

        OrderItem orderItem1 = new OrderItem(){
            @Override
            public BigDecimal getNetAmountInvoiced(){
                return invoicedNetAmountOnItem1;
            }
        };
        OrderItem orderItem2 = new OrderItem(){
            @Override
            public BigDecimal getNetAmountInvoiced(){
                return invoicedNetAmountOnItem2;
            }
        };

        // expect
        context.checking(new Expectations(){{
            oneOf(mockOrderItemRepository).findByProjectAndCharge(null, null);
            will(returnValue(Arrays.asList(orderItem1, orderItem2)));
        }});

        // when
        BigDecimal orderedAmount = mixin.invoicedAmount();

        // then
        Assertions.assertThat(orderedAmount).isEqualTo(expectedTotalInvoicedNetAmountOnItems);

    }

}