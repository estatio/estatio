package org.estatio.capex.fixture.charge;

import com.google.common.io.Resources;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

public class IncomingChargeFixture extends ExcelFixture2 {

    @Override
    protected void execute(final ExecutionContext executionContext) {

        setExcelResource(Resources.getResource(getClass(), "CapexChargeHierarchy.xlsx"));

        setMatcher(sheetName -> {
            if(sheetName.startsWith("ChargeHierarchy")) {
                return new WorksheetSpec(
                        rowFactoryFor(IncomingChargeHandler.class, executionContext),
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
