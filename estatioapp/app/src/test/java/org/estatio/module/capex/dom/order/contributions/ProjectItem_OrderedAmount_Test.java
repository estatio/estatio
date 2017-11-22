package org.estatio.module.capex.dom.order.contributions;

import java.math.BigDecimal;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.capex.dom.order.contributions.ProjectItem_OrderedAmount;
import org.estatio.module.capex.dom.project.ProjectItem;

public class ProjectItem_OrderedAmount_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    OrderItemRepository mockOrderItemRepository;

    @Test
    public void orderedAmount_works() throws Exception {

        // given
        BigDecimal expectedTotalNetAmountOnItems = new BigDecimal("100.00");
        BigDecimal netAmountOnItem1 = new BigDecimal("55.00");
        BigDecimal netAmountOnItem2 = new BigDecimal("45.00");
        BigDecimal netAmountOnItem3 = null;

        ProjectItem projectItem = new ProjectItem();
        ProjectItem_OrderedAmount mixin = new ProjectItem_OrderedAmount(projectItem);
        mixin.orderItemRepository = mockOrderItemRepository;

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setNetAmount(netAmountOnItem1);
        OrderItem orderItem2 = new OrderItem();
        orderItem2.setNetAmount(netAmountOnItem2);
        OrderItem orderItem3 = new OrderItem();
        orderItem3.setNetAmount(netAmountOnItem3);

        // expect
        context.checking(new Expectations(){{
            oneOf(mockOrderItemRepository).findByProjectAndCharge(null, null);
            will(returnValue(Arrays.asList(orderItem1, orderItem2, orderItem3)));
        }});

        // when
        BigDecimal orderedAmount = mixin.orderedAmount();

        // then
        Assertions.assertThat(orderedAmount).isEqualTo(expectedTotalNetAmountOnItems);

    }

}