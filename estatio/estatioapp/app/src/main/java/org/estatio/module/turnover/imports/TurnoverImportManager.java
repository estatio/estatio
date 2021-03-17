package org.estatio.module.turnover.imports;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.party.dom.Person;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRepository;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnover.dom.entry.TurnoverEntryService;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL, objectType = "org.estatio.module.turnover.imports.TurnoverImportManager")
public class TurnoverImportManager {

    public String title(){
        return "Turnover import manager";
    }

    @Getter @Setter
    private Person reporter;

    @Getter @Setter
    private Property property;

    @Getter @Setter
    private Type type;

    @Getter @Setter
    private LocalDate date;


    public List<TurnoverImport> getLines(){

        return turnoverEntryService.findByReporterPropertyTypeAndDate(getReporter(), getProperty(), getType(), getDate())
        .stream()
        .map(to->new TurnoverImport(
                to.getConfig().getOccupancy().getLease().getReference(),
                to.getConfig().getOccupancy().getLease().getName(),
                to.getConfig().getOccupancy().getUnit().getReference(),
                to.getConfig().getOccupancy().getStartDate(),
                getDate(),
                to.getGrossAmount(),
                to.getNetAmount(),
                defaultVatPercentage(),
                getType().name(),
                to.getFrequency().name(),
                to.getCurrency().getReference(),
                to.isNonComparable() ? 1 : 0,
                to.getPurchaseCount(),
                to.getComments(),
                to.getReportedBy(),
                to.getReportedAt(),
                findTurnoverPreviousYear(to)!=null ? findTurnoverPreviousYear(to).getGrossAmount() : null,
                findTurnoverPreviousYear(to)!=null ? findTurnoverPreviousYear(to).getNetAmount() : null,
                findTurnoverPreviousYear(to)!=null ? findTurnoverPreviousYear(to).getPurchaseCount() : null
                ))
        .collect(Collectors.toList());
    }

    public Blob downloadSpreadsheet(@Nullable final String fileName){
        String fileNameToUse = fileName!=null ? fileName : createFileName();
        return excelService.toExcel(getLines(), TurnoverImport.class, "TurnoverImport", fileNameToUse.concat(".xlsx"));
    }

    private String createFileName(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("Turnover_");
        buffer.append(getProperty().getReference());
        buffer.append("_");
        buffer.append(getDate().toString("yyyy-MM-dd"));
        buffer.append("_");
        buffer.append(getType().name());
        return buffer.toString();
    }

    public TurnoverImportManager uploadSpreadsheet(
            @Parameter(fileAccept = ".xlsx")
    final Blob spreadsheet){
        excelService.fromExcel(spreadsheet, TurnoverImport.class, "TurnoverImport")
        .forEach(l->l.importData(null));
        return this;
    }

    private Turnover findTurnoverPreviousYear(final Turnover turnover){
        return turnoverEntryService.findTurnoverPreviousYear(turnover);
    }

    private BigDecimal defaultVatPercentage(){
        if (defaultTax()!=null){
            return defaultTax().taxRateFor(clockService.now()).getPercentage();
        }
        return null;
    }

    private Tax defaultTax(){
        String countryReference = getProperty().getCountry().getReference();
        switch (countryReference) {
        case "SWE":
            return taxRepository.findByReference("SESTD");
        default: return null;
        }
    }

    @Inject TaxRepository taxRepository;

    @Inject
    TurnoverEntryService turnoverEntryService;

    @Inject
    ExcelService excelService;

    @Inject ClockService clockService;

}
