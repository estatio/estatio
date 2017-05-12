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
public class SupplierBankAccountImportManager {

    public SupplierBankAccountImportManager(){}

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public void importSheet(final Blob spreadSheet){

        List<List<?>> res = excelService.fromExcel(
                spreadSheet,
                sheetName -> {
                    if(sheetName.startsWith("oas_linkaddrlist")) {
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
        for (SupplierImportLine line : lines){
            line.importLine();
        }

        List<List<?>> baRes = excelService.fromExcel(
                spreadSheet,
                sheetName -> {
                    if(sheetName.startsWith("oas_linkbanklist")) {
                        return new WorksheetSpec(
                                BankAccountImportLine.class,
                                sheetName,
                                Mode.STRICT);
                    }
                    else
                        return null;
                }
        );
        List<BankAccountImportLine> bankAccountImportLines = (List) baRes.get(0);
        for (BankAccountImportLine bankAccountImportLine : bankAccountImportLines){
            bankAccountImportLine.importLine();
        }

    }

    @Inject
    ExcelService excelService;

}
