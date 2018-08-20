package org.estatio.module.fastnet.dom;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChargingLineLogViewModel_Test {

    @Test
    public void compareTo() {

        // given
        ChargingLineLogViewModel vm = new ChargingLineLogViewModel();
        ChargingLineLogViewModel vmOther = new ChargingLineLogViewModel();
        // when //then (should be NOOP)
        Assertions.assertThat(vm.compareTo(vmOther)).isEqualTo(0);

        // when
        vm.setApplied(new LocalDate(2018,1,2));
        vmOther.setApplied(new LocalDate(2018,1,1));
        // then
        Assertions.assertThat(vm.compareTo(vmOther)).isEqualTo(1);

        // when
        vm.setKeyToLeaseExternalReference("123");
        vmOther.setKeyToLeaseExternalReference("234");
        // then
        Assertions.assertThat(vm.compareTo(vmOther)).isEqualTo(-1);

        // when
        vm.setLeaseReference("REF123");
        vmOther.setLeaseReference("REF023");
        // then
        Assertions.assertThat(vm.compareTo(vmOther)).isEqualTo(1);

        // when
        vm.setLeaseReference(null);
        vmOther.setLeaseReference("REF023");
        // then
        Assertions.assertThat(vm.compareTo(vmOther)).isEqualTo(-1);

        // when
        vm.setLeaseReference("REF123");
        vmOther.setLeaseReference(null);
        // then
        Assertions.assertThat(vm.compareTo(vmOther)).isEqualTo(1);

    }
}