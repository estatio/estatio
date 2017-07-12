package org.estatio.capex.dom.order;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Test;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.party.Organisation;

import static org.assertj.core.api.Assertions.assertThat;

public class Order_Test {

    @Test
    public void minimalRequiredDataToComplete() throws Exception {

        // given
        Order order = new Order();
        OrderItem item1 = new OrderItem();
        order.getItems().add(item1);


        // when
        String result = order.reasonIncomplete();

        // then
        assertThat(result).isEqualTo("order number, buyer, seller, (on item) description, charge, start date, end date, net amount, gross amount required");

        // and when
        order.setOrderNumber("123");
        item1.setNetAmount(new BigDecimal("100"));
        result = order.reasonIncomplete();

        // then
        assertThat(result).isEqualTo("buyer, seller, (on item) description, charge, start date, end date, gross amount required");

        // and when
        order.setBuyer(new Organisation());
        order.setSeller(new Organisation());
        item1.setDescription("blah");
        item1.setGrossAmount(BigDecimal.ZERO);
        item1.setCharge(new Charge());
        item1.setStartDate(new LocalDate());
        item1.setEndDate(new LocalDate());
        result = order.reasonIncomplete();

        // then
        assertThat(result).isNull();

    }

}