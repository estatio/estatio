package org.estatio.module.turnover.fixtures;

import com.google.common.io.Resources;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.fixturescripts.FixtureResult;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

import org.estatio.module.turnover.imports.TurnoverImport;

@DomainObject(
        objectType = "org.estatio.module.turnover.fixtures.TurnoverImportXlsxFixture"
)
public class TurnoverImportXlsxFixture extends ExcelFixture2 {

    @Override
    protected void execute(final ExecutionContext executionContext) {

        setExcelResource(Resources.getResource(getClass(), "TurnoverImport.xlsx"));

        setMatcher(sheetName -> {
            if(sheetName.startsWith("TurnoverImport")) {
                return new WorksheetSpec(
                        rowFactoryFor(TurnoverImport.class, executionContext),
                        sheetName,
                        Mode.RELAXED);
            }
            else {
                return null;
            }
        });


        super.execute(executionContext);

        for (FixtureResult result : executionContext.getResults()){
            if (result.getClassName().equals(TurnoverImport.class.getName())){
                TurnoverImport line = (TurnoverImport) result.getObject();
                line.importData(null);
            }
        }
    }

}
