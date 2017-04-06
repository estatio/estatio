package org.estatio.fixture.time.charge;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

import org.estatio.dom.capex.time.CalendarType;
import org.estatio.dom.capex.time.TimeInterval;
import org.estatio.dom.capex.time.TimeIntervalRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class TimeIntervalHandler implements FixtureAwareRowHandler<TimeIntervalHandler> {

    @Getter @Setter
    private String name;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private CalendarType calendarType;

    @Getter @Setter
    private String naturalParent;

    @Getter @Setter
    private String financialParent;

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


    @Override
    public void handleRow(final TimeIntervalHandler previousRow) {

        if(executionContext != null && excelFixture2 != null) {
            executionContext.addResult(excelFixture2, this.handle(previousRow));
        }

    }

    private TimeInterval handle(final TimeIntervalHandler previousRow) {
        final TimeInterval naturalParentObj = timeIntervalRepository.findByName(naturalParent);
        final TimeInterval financialParentObj = timeIntervalRepository.findByName(financialParent);

        return timeIntervalRepository.findOrCreate(name, startDate, endDate, calendarType, naturalParentObj, financialParentObj);
    }

    @Inject
    TimeIntervalRepository timeIntervalRepository;

}

