package org.estatio.capex.fixture.time;

import org.estatio.capex.dom.time.CalendarType;
import org.estatio.capex.dom.time.TimeInterval;

public class FinancialTimeIntervalHandler extends TimeIntervalHandler {

    public FinancialTimeIntervalHandler() {
        super(CalendarType.FINANCIAL);
    }

    @Override
    protected TimeInterval getParent() {
        return null;
    }
}

