package org.estatio.module.lease.dom.amendments;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.estatio.module.lease.dom.LeaseItemType;

import static org.assertj.core.api.Assertions.assertThat;

public class AmendmentItem_Test {

    @Test
    public void applicableToToString() {
        // given
        final List<LeaseItemType> types = Arrays.asList(LeaseItemType.RENT, LeaseItemType.SERVICE_CHARGE);
        // when, then
        assertThat(AmendmentItem.applicableToToString(types)).isEqualTo("RENT,SERVICE_CHARGE");
    }

    @Test
    public void applicableToFromString() {
        // given
        final String string = "RENT,SERVICE_CHARGE";
        // when
        final List<LeaseItemType> leaseItemTypes = AmendmentItem
                .applicableToFromString(string);
        // then
        assertThat(leaseItemTypes).hasSize(2);
        assertThat(leaseItemTypes).contains(LeaseItemType.RENT);
        assertThat(leaseItemTypes).contains(LeaseItemType.SERVICE_CHARGE);
    }
}