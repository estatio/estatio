package org.estatio.module.lease.dom.amortisation;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import org.estatio.module.lease.dom.Frequency;

import static org.junit.Assert.*;

public class AmortisationScheduleService_Test {

    AmortisationScheduleService service;

    @Test
    public void nextDate_works() {

        // given
        service = new AmortisationScheduleService();

        // when, then
        assertNextDate(new LocalDate(2020,1,1),  Frequency.QUARTERLY, new LocalDate(2020,4,1));
        assertNextDate(new LocalDate(2020,1,3),  Frequency.QUARTERLY, new LocalDate(2020,4,1));
        assertNextDate(new LocalDate(2020,3,31),  Frequency.QUARTERLY, new LocalDate(2020,4,1));
        assertNextDate(new LocalDate(2020,4,1),  Frequency.QUARTERLY, new LocalDate(2020,7,1));
        assertNextDate(new LocalDate(2020,6,30),  Frequency.QUARTERLY, new LocalDate(2020,7,1));
        assertNextDate(new LocalDate(2020,7,1),  Frequency.QUARTERLY, new LocalDate(2020,10,1));
        assertNextDate(new LocalDate(2020,9,30),  Frequency.QUARTERLY, new LocalDate(2020,10,1));
        assertNextDate(new LocalDate(2020,10,1),  Frequency.QUARTERLY, new LocalDate(2021,1,1));
        assertNextDate(new LocalDate(2020,12,31),  Frequency.QUARTERLY, new LocalDate(2021,1,1));

        assertNextDate(new LocalDate(2020,1,1),  Frequency.MONTHLY, new LocalDate(2020,2,1));
        assertNextDate(new LocalDate(2020,1,31),  Frequency.MONTHLY, new LocalDate(2020,2,1));
        assertNextDate(new LocalDate(2020,2,1),  Frequency.MONTHLY, new LocalDate(2020,3,1));
        // etc ...
        assertNextDate(new LocalDate(2020,12,31),  Frequency.MONTHLY, new LocalDate(2021,1,1));

        // default behaviour: returns null for not supported frequencies
        // f.i.
        assertNextDate(new LocalDate(2020,1,1),  Frequency.QUARTERLY_PLUS1M, null);


    }

    private void assertNextDate(final LocalDate date, final Frequency frequency, final LocalDate expectedResult){
        Assertions.assertThat(service.nextDate(date, frequency)).isEqualTo(expectedResult);
    }

}