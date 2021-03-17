package org.estatio.module.lease.dom.occupancy.salesarea;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import org.estatio.module.lease.dom.occupancy.Occupancy;

public class SalesAreaLicense_Test {

    @Test
    public void validate() {

        final LocalDate effectiveStartDate = new LocalDate(2020,1,16);
        final LocalDate effectiveEndDate = new LocalDate(2021,1,15);

        // given
        Occupancy o = new Occupancy(){
            @Override public LocalDate getEffectiveStartDate() {
                return effectiveStartDate;
            }
            @Override public LocalDate getEffectiveEndDate() {
                return effectiveEndDate;
            }
        };

        // when, then
        Assertions.assertThat(SalesAreaLicense.validate(o, null, new LocalDate(2020,1,15),null,null,null)).isEqualTo("Please fill in at least 1 area");
        Assertions.assertThat(SalesAreaLicense.validate(o, null, new LocalDate(2020,1,15), BigDecimal.ZERO,null,null)).isEqualTo("The license start date cannot be before the occupancy effective start date");
        Assertions.assertThat(SalesAreaLicense.validate(o, null, new LocalDate(2020,1,16), BigDecimal.ZERO,null,null)).isNull();

        // when
        o.setStartDate(new LocalDate(2020,1,17));
        // then
        Assertions.assertThat(SalesAreaLicense.validate(o, null, new LocalDate(2020,1,16), BigDecimal.ZERO,null,null)).isEqualTo("The license start date cannot be before the occupancy start date");
        Assertions.assertThat(SalesAreaLicense.validate(o, null, new LocalDate(2020,1,17), BigDecimal.ZERO,null,null)).isNull();
        Assertions.assertThat(SalesAreaLicense.validate(o, null, new LocalDate(2021,1,16), BigDecimal.ZERO,null,null)).isEqualTo("The license start date cannot be after the occupancy effective end date");
        Assertions.assertThat(SalesAreaLicense.validate(o, null, new LocalDate(2021,1,15), BigDecimal.ZERO,null,null)).isNull();

        // when
        o.setEndDate((new LocalDate(2021,1,14)));
        Assertions.assertThat(SalesAreaLicense.validate(o, null, new LocalDate(2021,1,15), BigDecimal.ZERO,null,null)).isEqualTo("The license start date cannot be after the occupancy end date");
        Assertions.assertThat(SalesAreaLicense.validate(o, null, new LocalDate(2021,1,14), BigDecimal.ZERO,null,null)).isNull();

        // and given
        SalesAreaLicense currentLicense = new SalesAreaLicense();
        currentLicense.setStartDate(new LocalDate(2021, 1, 14));
        // then
        Assertions.assertThat(SalesAreaLicense.validate(o, currentLicense, new LocalDate(2021,1,13), BigDecimal.ZERO,null,null)).isEqualTo("The start date cannot be before the current start date");
        Assertions.assertThat(SalesAreaLicense.validate(o, currentLicense, new LocalDate(2021,1,14), BigDecimal.ZERO,null,null)).isNull();
    }
}