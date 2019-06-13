package org.estatio.module.party.imports;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.PartyRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.module.party.imports.ImportChamberOfCommerceCodesService"
)
public class ImportChamberOfCommerceCodesService {

    private static final Logger LOG = LoggerFactory.getLogger(ImportChamberOfCommerceCodesService.class);

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public void importChamberOfCommerceCodes(final Blob sheet) {
        WorksheetSpec spec = new WorksheetSpec(ChamberOfCommerceImportLine.class, "Sheet1");
        List<ChamberOfCommerceImportLine> lines = excelService.fromExcel(sheet, spec);
        for (ChamberOfCommerceImportLine line : lines) {
            Organisation org = (Organisation) partyRepository.findPartyByReference(line.getReference());
            if (org == null) {
                LOG.error(String.format("No organisation found for reference %s", line.getReference()));
            } else {
                org.setChamberOfCommerceCode(line.getCode());
            }
        }
    }

    @Inject ExcelService excelService;

    @Inject PartyRepository partyRepository;

}
