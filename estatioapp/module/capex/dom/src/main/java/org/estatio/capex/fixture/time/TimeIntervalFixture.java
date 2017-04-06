package org.estatio.capex.fixture.time;

import com.google.common.io.Resources;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

public class TimeIntervalFixture extends ExcelFixture2 {

    @Override
    protected void execute(final ExecutionContext executionContext) {

        setExcelResource(Resources.getResource(getClass(), "TimeIntervalHierarchy.xlsx"));

        setMatcher(sheetName -> {
            if(sheetName.startsWith("NaturalCalendar")) {
                return new WorksheetSpec(
                        rowFactoryFor(NaturalTimeIntervalHandler.class, executionContext),
                        sheetName,
                        Mode.STRICT);
            }
            else if(sheetName.startsWith("FinancialCalendar")) {
                return new WorksheetSpec(
                        rowFactoryFor(FinancialTimeIntervalHandler.class, executionContext),
                        sheetName,
                        Mode.STRICT);
            }
            else {
                return null;
            }
        });


        super.execute(executionContext);
    }




}
