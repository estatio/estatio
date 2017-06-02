package org.estatio.capex.dom.impmgr;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.capex.dom.impmgr.SellerImport"
)
public class SupplierImportManager {

    public SupplierImportManager(){}

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public void importSheet(final Blob spreadSheet){

        List<List<?>> res = excelService.fromExcel(
                spreadSheet,
                sheetName -> {
                    if(sheetName.startsWith("suppliers")) {
                        return new WorksheetSpec(
                                SupplierImportLine.class,
                                sheetName,
                                Mode.STRICT);
                    }
                    else
                        return null;
                }
        );
        List<SupplierImportLine> lines = (List) res.get(0);
        SupplierImportLine previousRow = null;
        for (SupplierImportLine line : lines){
            line.importData(previousRow);
            previousRow = line;
        }

    }

    @Inject
    ExcelService excelService;

}
