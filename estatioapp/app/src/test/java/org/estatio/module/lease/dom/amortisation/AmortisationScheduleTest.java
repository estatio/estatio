package org.estatio.module.lease.dom.amortisation;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

public class AmortisationScheduleTest {

    @Test
    public void verifyOutstandingValue() {

        // given
        AmortisationSchedule schedule = new AmortisationSchedule();
        final BigDecimal scheduledValue = new BigDecimal("1234.56");
        schedule.setScheduledValue(scheduledValue);

        AmortisationEntry e1 = new AmortisationEntry();
        e1.setEntryDate(new LocalDate(2020,1,1));
        e1.setEntryAmount(new BigDecimal("234.56"));

        AmortisationEntry e2 = new AmortisationEntry();
        e1.setEntryDate(new LocalDate(2020,2,1));
        e2.setEntryAmount(new BigDecimal("100.00"));

        // when no entries
        schedule.verifyOutstandingValue();
        // then
        Assertions.assertThat(schedule.getOutstandingValue()).isEqualTo(scheduledValue);

        // when no entries reported
        schedule.getEntries().add(e1);
        schedule.getEntries().add(e2);
        schedule.verifyOutstandingValue();
        // then
        Assertions.assertThat(schedule.getOutstandingValue()).isEqualTo(scheduledValue);

        // when entries reported
        e1.setDateReported(new LocalDate(2020,1,2));
        schedule.verifyOutstandingValue();
        // then
        Assertions.assertThat(schedule.getOutstandingValue()).isEqualTo(scheduledValue.subtract(e1.getEntryAmount()));
        Assertions.assertThat(schedule.getOutstandingValue()).isEqualTo(new BigDecimal("1000.00"));
        // and when
        e2.setDateReported(new LocalDate(2020,2,2));
        schedule.verifyOutstandingValue();
        // then
        Assertions.assertThat(schedule.getOutstandingValue()).isEqualTo(scheduledValue.subtract(e1.getEntryAmount()).subtract(e2.getEntryAmount()));
        Assertions.assertThat(schedule.getOutstandingValue()).isEqualTo(new BigDecimal("900.00"));

    }
}