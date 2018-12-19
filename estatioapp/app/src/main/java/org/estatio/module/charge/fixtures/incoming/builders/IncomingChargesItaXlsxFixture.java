package org.estatio.module.charge.fixtures.incoming.builders;

import com.google.common.io.Resources;

import org.apache.isis.applib.annotation.DomainObject;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

import org.estatio.module.capex.imports.IncomingChargeImportAdapter;

@DomainObject(
        objectType = "org.estatio.module.ecpimport.fixtures.excel.IncomingChargeFixtureForIta"
)
public class IncomingChargesItaXlsxFixture extends ExcelFixture2 {

    @Override
    protected void execute(final ExecutionContext executionContext) {

        setExcelResource(Resources.getResource(getClass(), "IncomingChargesIta.xlsx"));

        setMatcher(sheetName -> {

            if(sheetName.startsWith("work type")) {
                return new WorksheetSpec(
                        rowFactoryFor(IncomingChargeImportAdapter.class, executionContext),
                        sheetName,
                        Mode.RELAXED);
            }
            else
                return null;
        });

        setSequencer(specs -> specs);

        super.execute(executionContext);
    }

}
