package org.estatio.capex.fixture.time;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

import org.estatio.capex.dom.time.CalendarType;
import org.estatio.capex.dom.time.TimeInterval;
import org.estatio.capex.dom.time.TimeIntervalRepository;

import lombok.Getter;
import lombok.Setter;

public abstract class TimeIntervalHandler implements FixtureAwareRowHandler<TimeIntervalHandler> {

    @Getter
    private final CalendarType calendarType;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    protected abstract TimeInterval getParent();



    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private FixtureScript.ExecutionContext executionContext;

    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private ExcelFixture2 excelFixture2;

    protected TimeIntervalHandler(final CalendarType calendarType) {
        this.calendarType = calendarType;
    }

    @Override
    public void handleRow(final TimeIntervalHandler previousRow) {

        if(executionContext != null && excelFixture2 != null) {
            executionContext.addResult(excelFixture2, this.handle(previousRow));
        }

    }

    private TimeInterval handle(final TimeIntervalHandler previousRow) {
        return timeIntervalRepository.findOrCreate(name, startDate, endDate, calendarType, getParent());
    }

    @Inject
    TimeIntervalRepository timeIntervalRepository;

}

