package org.estatio.module.asset.fixtures.erv;

import com.google.common.io.Resources;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.fixturescripts.FixtureResult;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

import org.estatio.module.asset.imports.ErvImport;

@DomainObject(
        objectType = "org.estatio.module.turnover.fixtures.ERVImportXlsxFixture"
)
public class ERVImportXlsxFixture extends ExcelFixture2 {

    @Override
    protected void execute(final ExecutionContext executionContext) {

        setExcelResource(Resources.getResource(getClass(), "ERVImport.xlsx"));

        setMatcher(sheetName -> {
            if(sheetName.startsWith("import")) {
                return new WorksheetSpec(
                        rowFactoryFor(ErvImport.class, executionContext),
                        sheetName,
                        Mode.RELAXED);
            }
            else {
                return null;
            }
        });

        super.execute(executionContext);

        for (FixtureResult result : executionContext.getResults()){
            if (result.getClassName().equals(ErvImport.class.getName())){
                ErvImport line = (ErvImport) result.getObject();
                line.importData(null);
            }
        }
    }

}
