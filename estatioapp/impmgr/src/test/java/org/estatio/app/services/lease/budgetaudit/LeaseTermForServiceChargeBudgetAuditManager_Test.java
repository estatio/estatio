package org.estatio.app.services.lease.budgetaudit;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.estatio.dom.lease.LeaseItemType;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseTermForServiceChargeBudgetAuditManager_Test {

    @Test
    public void TypesToStringTest() {

        // given
        LeaseTermForServiceChargeBudgetAuditManager manager = new LeaseTermForServiceChargeBudgetAuditManager();

        // when
        List<LeaseItemType> listToConvert = Arrays.asList(LeaseItemType.SERVICE_CHARGE, LeaseItemType.MARKETING, LeaseItemType.PROPERTY_TAX);
        String result = manager.typesToString(listToConvert);

        // then
        assertThat(result).isEqualTo("SERVICE_CHARGE, MARKETING, PROPERTY_TAX");

        // and when
        listToConvert = Arrays.asList(LeaseItemType.SERVICE_CHARGE);
        result = manager.typesToString(listToConvert);

        // then
        assertThat(result).isEqualTo("SERVICE_CHARGE");

    }

    @Test
    public void TypesFromStringTest(){

        // given
        LeaseTermForServiceChargeBudgetAuditManager manager = new LeaseTermForServiceChargeBudgetAuditManager();

        // when
        String stringToConvert = "SERVICE_CHARGE, MARKETING, PROPERTY_TAX";
        List<LeaseItemType> result = manager.typesFromString(stringToConvert);

        // then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0)).isEqualTo(LeaseItemType.SERVICE_CHARGE);

        // and when
        stringToConvert = "MARKETING";
        result = manager.typesFromString(stringToConvert);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(LeaseItemType.MARKETING);

    }

}
