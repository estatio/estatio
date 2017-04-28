package org.estatio.capex.dom.documents.order;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.party.Organisation;

public class IncomingOrderViewModelTest {

    @Test
    public void minimalRequiredDataToComplete() throws Exception {

        // given
        IncomingOrderViewModel vm = new IncomingOrderViewModel();

        // when
        String result = vm.minimalRequiredDataToComplete();

        // then
        Assertions.assertThat(result).isEqualTo("order number, buyer, seller, description, net amount, gross amount, charge, period required");

        // and when
        vm.setOrderNumber("123");
        vm.setNetAmount(new BigDecimal("100"));
        result = vm.minimalRequiredDataToComplete();

        // then
        Assertions.assertThat(result).isEqualTo("buyer, seller, description, gross amount, charge, period required");

        // and when
        vm.setBuyer(new Organisation());
        vm.setSeller(new Organisation());
        vm.setDescription("blah");
        vm.setGrossAmount(BigDecimal.ZERO);
        vm.setCharge(new Charge());
        vm.setPeriod("blah");
        result = vm.minimalRequiredDataToComplete();

        // then
        Assertions.assertThat(result).isNull();

    }

}