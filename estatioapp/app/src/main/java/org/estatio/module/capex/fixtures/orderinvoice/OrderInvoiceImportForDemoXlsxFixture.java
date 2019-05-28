package org.estatio.module.capex.fixtures.orderinvoice;

import javax.inject.Inject;

import com.google.common.io.Resources;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.services.factory.FactoryService;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

import org.estatio.module.capex.imports.OrderInvoiceImportHandler;
import org.estatio.module.capex.imports.OrderInvoiceLine;

@DomainObject(
        objectType = "org.estatio.module.capex.fixtures.orderinvoice.OrderInvoiceImportForDemoXlsxFixture"
)
public class OrderInvoiceImportForDemoXlsxFixture extends ExcelFixture2 {

    @Override
    protected void execute(final ExecutionContext executionContext) {

        setExcelResource(Resources.getResource(getClass(), "OrderInvoiceImportForDemo.xlsx"));

        setMatcher(sheetName -> {
            if(sheetName.startsWith("OXFORD")) {
                return new WorksheetSpec(
                        rowFactoryFor(OrderInvoiceImportHandler.class, executionContext),
                        sheetName,
                        Mode.STRICT);
            }
            else {
                return null;
            }
        });


        super.execute(executionContext);

        for (FixtureResult result : executionContext.getResults()){
            if (result.getClassName().equals(OrderInvoiceLine.class.getName())){
                OrderInvoiceLine line = (OrderInvoiceLine) result.getObject();
                OrderInvoiceLine._apply applyMixin = factoryService.mixin(OrderInvoiceLine._apply.class, line);
                if(applyMixin.disableAct() == null) {
                    applyMixin.act();
                }
            }
        }
    }


    @Inject
    FactoryService factoryService;

}
