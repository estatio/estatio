package org.estatio.capex.dom.order.viewmodel;

import java.math.BigDecimal;

import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.party.Organisation;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingDocAsOrderViewModel_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    PaperclipRepository mockPaperclipRepository;

    @Mock
    Document mockDocument;

    @Test
    public void minimalRequiredDataToComplete() throws Exception {

        // given
        IncomingDocAsOrderViewModel vm = new IncomingDocAsOrderViewModel();

        // when
        String result = vm.minimalRequiredDataToComplete();

        // then
        assertThat(result).isEqualTo("order number, buyer, seller, description, net amount, gross amount, charge, period required");

        // and when
        vm.setOrderNumber("123");
        vm.setNetAmount(new BigDecimal("100"));
        result = vm.minimalRequiredDataToComplete();

        // then
        assertThat(result).isEqualTo("buyer, seller, description, gross amount, charge, period required");

        // and when
        vm.setBuyer(new Organisation());
        vm.setSeller(new Organisation());
        vm.setDescription("blah");
        vm.setGrossAmount(BigDecimal.ZERO);
        vm.setCharge(new Charge());
        vm.setPeriod("blah");
        result = vm.minimalRequiredDataToComplete();

        // then
        assertThat(result).isNull();

    }

}