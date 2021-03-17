package org.estatio.module.fastnet.dom;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class ChargingLineTest {

    @Test
    public void keyToChargeReference() throws Exception {

        // given
        ChargingLine line = new ChargingLine();
        // when
        line.setKod("123");
        line.setKod2("4");
        // then
        Assertions.assertThat(line.keyToChargeReference()).isEqualTo("SE123-4");

    }

    @Test
    public void discarded_or_applied() throws Exception {

        // given
        ChargingLine line = new ChargingLine();
        // then
        Assertions.assertThat(line.discardedOrAggregatedOrApplied()).isFalse();

        // and when
        line.setImportStatus(ImportStatus.DISCARDED);
        // then
        Assertions.assertThat(line.discardedOrAggregatedOrApplied()).isTrue();

        // and when
        line.setImportStatus(ImportStatus.LEASE_ITEM_CREATED);
        // then
        Assertions.assertThat(line.discardedOrAggregatedOrApplied()).isFalse();

        // and when
        line.setImportStatus(null);
        line.setApplied(new LocalDate());
        // then
        Assertions.assertThat(line.discardedOrAggregatedOrApplied()).isTrue();

        // and when
        line.setImportStatus(ImportStatus.LEASE_ITEM_CREATED);
        line.setApplied(new LocalDate());
        // then
        Assertions.assertThat(line.discardedOrAggregatedOrApplied()).isTrue();

        // and when
        line.setImportStatus(ImportStatus.DISCARDED);
        line.setApplied(new LocalDate());
        // then
        Assertions.assertThat(line.discardedOrAggregatedOrApplied()).isTrue();

    }

    @Test
    public void apply_discarded_works() throws Exception {

        // given
        ChargingLine line = new ChargingLine();
        // when
        line.setApplied(new LocalDate());
        line.setImportStatus(ImportStatus.LEASE_ITEM_CREATED);
        // then
        Assertions.assertThat(line.apply()).isEqualTo(ImportStatus.LEASE_ITEM_CREATED);

    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock ClockService mockClockService;

    @Test
    public void append_import_log_works() throws Exception {

        // given
        ChargingLine line = new ChargingLine();
        line.clockService = mockClockService;
        line.setImportLog("first message");

        // expect
        context.checking(new Expectations(){{
            allowing(mockClockService).nowAsLocalDateTime();
            will(returnValue(LocalDateTime.parse("2018-01-01")));
        }});

        // when
        line.appendImportLog("second message");
        // then
        Assertions.assertThat(line.getImportLog()).isEqualTo("2018-01-01 00:00:00 second message first message");

        // and given
        String stringOf254Chars = "";
        for (int i=0; i<254; i++ ){
            stringOf254Chars = stringOf254Chars.concat("X");
        }
        line.setImportLog(stringOf254Chars);
        Assertions.assertThat(line.getImportLog().length()).isEqualTo(254);

        // when
        line.appendImportLog("second message");
        // then
        Assertions.assertThat(line.getImportLog().length()).isEqualTo(254);
        Assertions.assertThat(line.getImportLog()).startsWith("2018-01-01 00:00:00 second message XXX");

        // and given
        line.setImportLog(null);
        // when
        line.appendImportLog("some message");
        // then
        Assertions.assertThat(line.getImportLog()).isEqualTo("2018-01-01 00:00:00 some message");
    }


}