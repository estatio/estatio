package org.estatio.module.party.dom;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class Supplier_Test {

    @Test
    public void title() throws Exception {

        // given
        Organisation organisation = new Organisation();
        organisation.setName("Great Organisation");
        organisation.setChamberOfCommerceCode("123456ABCDEF");
        organisation.setReference("FRCL1234");

        // when
        Supplier vm = new Supplier(organisation);
        // then
        Assertions.assertThat(vm.title()).isEqualTo("Great Organisation 123456ABCDEF [FRCL1234]");

        // and when
        organisation.setChamberOfCommerceCode(null);
        vm = new Supplier(organisation);
        // then
        Assertions.assertThat(vm.title()).isEqualTo("Great Organisation [FRCL1234]");

    }

}