package org.estatio.capex.dom.coda.contributions;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.capex.dom.coda.CodaMapping;
import org.estatio.capex.dom.coda.CodaMappingImport;
import org.estatio.capex.dom.coda.CodaMappingRepository;

@DomainObject(nature = Nature.VIEW_MODEL, objectType = "blblblblagadfglafgladfg")
public class CodaMappingManager {

    public String title() {
        return "Coda Mapping Manager";
    }

    public static final String SHEET_NAME = "mapping";

    @Action(semantics = SemanticsOf.SAFE)
    public Blob downloadToExcel(final String fileName) {

        final List<CodaMappingImport> exports = getMappings().stream()
                .map(x -> new CodaMappingImport(x))
                .collect(Collectors.toList());

        WorksheetSpec spec = new WorksheetSpec(CodaMappingImport.class, SHEET_NAME);
        WorksheetContent worksheetContent = new WorksheetContent(exports, spec);
        return excelService.toExcel(worksheetContent, fileName);
    }

    public String default0DownloadToExcel() {
        return "CODAMapping.xlsx";
    }

    @Action(publishing = Publishing.DISABLED, semantics = SemanticsOf.IDEMPOTENT)
    public CodaMappingManager upload(
            @Parameter(fileAccept = ".xlsx")
            final Blob spreadsheet) {
        List<CodaMappingImport> lineItems =
                excelService.fromExcel(spreadsheet, CodaMappingImport.class, SHEET_NAME);
        CodaMappingImport previousRow = null;
        for (CodaMappingImport lineItem : lineItems) {
            lineItem.handleRow(previousRow);
            previousRow = lineItem;
        }
        return this;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    List<CodaMapping> getMappings() {
        return codaMappingRepository.all();
    }

    @Inject CodaMappingRepository codaMappingRepository;

    @Inject ExcelService excelService;

}
