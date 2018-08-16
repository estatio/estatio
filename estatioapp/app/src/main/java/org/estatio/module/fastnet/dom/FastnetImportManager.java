package org.estatio.module.fastnet.dom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.schema.utils.jaxbadapters.JodaLocalDateStringAdapter;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlRootElement(name = "FastnetImportManager")
@XmlType(
        propOrder = {
                "exportDate",
                "nonMatchingDataLines",
                "partialMatchingDataLines",
                "linesWithoutKontraktNr",
                "noChargingDetails",
                "doubleExternalReferences",
                "chargeNotFound",
                "duplicateChargeReferences",
                "noUpdateNeeded",
                "linesForItemUpdate",
                "linesForItemCreation",
                "activeLeasesNotInImport",
                "discardedLines"

        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@DomainObject(objectType = "org.estatio.module.fastnet.dom.FastnetImportManager")
@NoArgsConstructor
public class FastnetImportManager {

    public String title() {
        return "Fastnet Import " + getExportDate().toString("yyyy-MM-dd");
    }

    @Getter @Setter
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate exportDate;

    @Setter
    private List<FastNetRentRollOnLeaseDataLine> nonMatchingDataLines = new ArrayList<>();

    @Programmatic
    public List<FastNetRentRollOnLeaseDataLine> getNonMatchingDataLines() {
        return this.nonMatchingDataLines;
    }

    @Setter
    private List<FastNetRentRollOnLeaseDataLine> partialMatchingDataLines = new ArrayList<>();

    @Programmatic
    public List<FastNetRentRollOnLeaseDataLine> getPartialMatchingDataLines() {
        return this.partialMatchingDataLines;
    }

    @Setter
    private List<FastNetRentRollOnLeaseDataLine> linesWithoutKontraktNr = new ArrayList<>();

    @Programmatic
    public List<FastNetRentRollOnLeaseDataLine> getLinesWithoutKontraktNr() {
        return this.linesWithoutKontraktNr;
    }

    @Setter
    private List<FastNetRentRollOnLeaseDataLine> noChargingDetails = new ArrayList<>();

    @Programmatic
    public List<FastNetRentRollOnLeaseDataLine> getNoChargingDetails() {
        return this.noChargingDetails;
    }

    @Setter
    private List<FastNetRentRollOnLeaseDataLine> doubleExternalReferences = new ArrayList<>();

    @Programmatic
    public List<FastNetRentRollOnLeaseDataLine> getDoubleExternalReferences() {
        return this.doubleExternalReferences;
    }

    @Setter
    private List<FastNetChargingOnLeaseDataLine> chargeNotFound = new ArrayList<>();

    @Programmatic
    public List<FastNetChargingOnLeaseDataLine> getChargeNotFound() {
        return this.chargeNotFound;
    }

    @Setter
    private List<FastNetChargingOnLeaseDataLine> duplicateChargeReferences = new ArrayList<>();

    @Programmatic
    public List<FastNetChargingOnLeaseDataLine> getDuplicateChargeReferences() {
        return this.duplicateChargeReferences;
    }

    @Setter
    private List<FastNetChargingOnLeaseDataLine> noUpdateNeeded = new ArrayList<>();

    @Programmatic
    public List<FastNetChargingOnLeaseDataLine> getNoUpdateNeeded() {
        return this.noUpdateNeeded;
    }

    @Setter
    private List<FastNetChargingOnLeaseDataLine> linesForItemUpdate = new ArrayList<>();

    @Programmatic
    public List<FastNetChargingOnLeaseDataLine> getLinesForItemUpdate() {
        return this.linesForItemUpdate;
    }

    @Setter
    private List<FastNetChargingOnLeaseDataLine> linesForItemCreation = new ArrayList<>();

    @Programmatic
    public List<FastNetChargingOnLeaseDataLine> getLinesForItemCreation() {
        return this.linesForItemCreation;
    }

    @Setter
    private List<FastNetChargingOnLeaseDataLine> discardedLines = new ArrayList<>();

    @Programmatic
    public List<FastNetChargingOnLeaseDataLine> getDiscardedLines() {
        return this.discardedLines;
    }

    @Setter
    private List<LeaseViewModel> activeLeasesNotInImport = new ArrayList<>();

    @Programmatic
    public List<LeaseViewModel> getActiveLeasesNotInImport() {
        return this.activeLeasesNotInImport;
    }

    @Action(associateWith = "readyForImport", publishing = Publishing.DISABLED)
    @ActionLayout(named = "import and apply")
    @CollectionLayout(defaultView = "excel")
    public FastnetImportManager doImport() {

        getLinesForItemUpdate().forEach(cdl -> {
            fastnetImportService.updateOrCreateItem(cdl);
        });
        getLinesForItemCreation().forEach(cdl -> {
            fastnetImportService.updateOrCreateItem(cdl);
        });
        getDuplicateChargeReferences().forEach(cdl -> {
            fastnetImportService.updateOrCreateItem(cdl);
        });
        getDiscardedLines().forEach(cdl -> {
            fastnetImportService.discard(cdl);
        });
        getNoUpdateNeeded().forEach(cdl -> {
            fastnetImportService.noUpdate(cdl);
        });

        return this;
    }

    @Action()
    public Blob downloadAnalysis() {
        WorksheetSpec spec0 = new WorksheetSpec(LeaseViewModel.class, "activeLeasesNotInImport");
        WorksheetContent content0 = new WorksheetContent(getActiveLeasesNotInImport(), spec0);
        WorksheetSpec spec1 = new WorksheetSpec(FastNetRentRollOnLeaseDataLine.class, "nonMatchingDataLines");
        WorksheetContent content1 = new WorksheetContent(getNonMatchingDataLines(), spec1);
        WorksheetSpec spec2 = new WorksheetSpec(FastNetRentRollOnLeaseDataLine.class, "partialMatchingDataLines");
        WorksheetContent content2 = new WorksheetContent(getPartialMatchingDataLines(), spec2);
        WorksheetSpec spec3 = new WorksheetSpec(FastNetRentRollOnLeaseDataLine.class, "noChargingDetails");
        WorksheetContent content3 = new WorksheetContent(getNoChargingDetails(), spec3);
        WorksheetSpec spec4 = new WorksheetSpec(FastNetRentRollOnLeaseDataLine.class, "doubleExternalReferences");
        WorksheetContent content4 = new WorksheetContent(getDoubleExternalReferences(), spec4);
        WorksheetSpec spec5 = new WorksheetSpec(FastNetChargingOnLeaseDataLine.class, "chargeNotFound");
        WorksheetContent content5 = new WorksheetContent(getChargeNotFound(), spec5);
        WorksheetSpec spec6 = new WorksheetSpec(FastNetChargingOnLeaseDataLine.class, "duplicateChargeReferences");
        WorksheetContent content6 = new WorksheetContent(getDuplicateChargeReferences(), spec6);
        WorksheetSpec spec7 = new WorksheetSpec(FastNetChargingOnLeaseDataLine.class, "noUpdateNeeded");
        WorksheetContent content7 = new WorksheetContent(getNoUpdateNeeded(), spec7);
        WorksheetSpec spec8 = new WorksheetSpec(FastNetChargingOnLeaseDataLine.class, "linesForItemUpdate");
        WorksheetContent content8 = new WorksheetContent(getLinesForItemUpdate(), spec8);
        WorksheetSpec spec9 = new WorksheetSpec(FastNetChargingOnLeaseDataLine.class, "linesForItemCreation");
        WorksheetContent content9 = new WorksheetContent(getLinesForItemCreation(), spec9);
        WorksheetSpec spec10 = new WorksheetSpec(FastNetChargingOnLeaseDataLine.class, "discardedLines");
        WorksheetContent content10 = new WorksheetContent(getDiscardedLines(), spec10);
        return excelService.toExcel(Arrays.asList(content0, content1, content2, content3, content4, content5, content6, content7, content8, content9, content10), "analysis export date " + getExportDate().toString("yyyy-MM-dd") + ".xlsx");
    }

    @Action
    public Blob downloadImportLog(){
        List<ChargingLineLogViewModel> linesWithLogMessage = chargingLineRepository.findByExportDate(getExportDate())
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
                .collect(Collectors.toList());
        WorksheetSpec spec0 = new WorksheetSpec(ChargingLineLogViewModel.class, "lines with log message");
        WorksheetContent content0 = new WorksheetContent(linesWithLogMessage, spec0);
        return excelService.toExcel(content0, "import log " + getExportDate().toString("yyyy-MM-dd") + ".xlsx");
    }

    @XmlTransient
    @Inject
    private ExcelService excelService;

    @XmlTransient
    @Inject
    FastnetImportService fastnetImportService;

    @XmlTransient
    @Inject
    ChargingLineRepository chargingLineRepository;

}