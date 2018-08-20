package org.estatio.module.fastnet.app;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import org.estatio.module.fastnet.dom.ChargingLine;
import org.estatio.module.fastnet.dom.ChargingLineLogViewModel;
import org.estatio.module.fastnet.dom.ChargingLineRepository;
import org.estatio.module.fastnet.dom.FastnetImportManager;
import org.estatio.module.fastnet.dom.FastnetImportService;
import org.estatio.module.fastnet.dom.ImportStatus;
import org.estatio.module.fastnet.dom.RentRollLineRepository;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY, objectType = "org.estatio.module.fastnet.app.FastnetImportMenu")
@DomainServiceLayout(named = "Leases")
public class FastnetImportMenu {

    @Action(semantics = SemanticsOf.IDEMPOTENT, publishing = Publishing.DISABLED)
    public FastnetImportManager applyFastnetDataImport(final LocalDate exportDate) {
        return fastnetImportService.importFastnetData(exportDate);
    }

    public List<LocalDate> choices0ApplyFastnetDataImport() {
        return rentRollLineRepository.findUniqueExportDates();
    }

    public LocalDate default0ApplyFastnetDataImport() {
        return choices0ApplyFastnetDataImport().isEmpty() ? null : choices0ApplyFastnetDataImport().get(0);
    }

    @Action(semantics = SemanticsOf.SAFE, publishing = Publishing.DISABLED)
    public Blob fastnetImportLog(final LocalDate date){
        List<ChargingLine> lines = chargingLineRepository.findByExportDate(date);
        List<ChargingLineLogViewModel> linesWithLogMessage = lines
                .stream()
                .filter(line->line.getImportLog()!=null)
                .map(line->new ChargingLineLogViewModel(
                        line.getImportLog(),
                        line.getApplied(),
                        line.getImportStatus(),
                        line.getLease()!=null ? line.getLease().getReference() : null,
                        line.getKeyToLeaseExternalReference(),
                        line.getKeyToChargeReference(),
                        line.getFromDat(),
                        line.getTomDat(),
                        line.getArsBel(),
                        line.getExportDate()
                ))
                .sorted()
                .collect(Collectors.toList());
        List<ChargingLineLogViewModel> linesWithOutMessage = lines
                .stream()
                .filter(line->line.getImportLog()==null && line.getApplied()!=null && line.getImportStatus()!=null && line.getImportStatus()!=ImportStatus.NO_UPDATE_NEEDED && line.getImportStatus()!=ImportStatus.DISCARDED)
                .map(line->new ChargingLineLogViewModel(
                        line.getImportLog(),
                        line.getApplied(),
                        line.getImportStatus(),
                        line.getLease()!=null ? line.getLease().getReference() : null,
                        line.getKeyToLeaseExternalReference(),
                        line.getKeyToChargeReference(),
                        line.getFromDat(),
                        line.getTomDat(),
                        line.getArsBel(),
                        line.getExportDate()
                ))
                .sorted()
                .collect(Collectors.toList());
        WorksheetSpec spec0 = new WorksheetSpec(ChargingLineLogViewModel.class, "lines with log message");
        WorksheetContent content0 = new WorksheetContent(linesWithLogMessage, spec0);
        WorksheetSpec spec1 = new WorksheetSpec(ChargingLineLogViewModel.class, "applied lines without message");
        WorksheetContent content1 = new WorksheetContent(linesWithOutMessage, spec1);
        return excelService.toExcel(Arrays.asList(content0, content1), "import log " + date.toString("yyyy-MM-dd") + ".xlsx");
    }

    public List<LocalDate> choices0FastnetImportLog() {
        return rentRollLineRepository.findUniqueExportDates();
    }

    @Inject
    private FastnetImportService fastnetImportService;

    @Inject
    private RentRollLineRepository rentRollLineRepository;

    @Inject
    ChargingLineRepository chargingLineRepository;

    @Inject
    ExcelService excelService;

}
