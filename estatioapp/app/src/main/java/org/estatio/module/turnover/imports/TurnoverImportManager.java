package org.estatio.module.turnover.imports;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL, objectType = "org.estatio.module.turnover.imports.TurnoverImportManager")
public class TurnoverImportManager {

    @Getter @Setter
    private String propertyReference;

    @Getter @Setter
    private Type type;

    @Getter @Setter
    private Frequency frequency;

    @Getter @Setter
    private LocalDate date;

    @Getter @Setter
    private List<TurnoverImport> lines = Lists.newArrayList();


    public List<TurnoverImport> getLines(){
        return turnoverRepository.listAll()
        .stream()
        .map(to->new TurnoverImport(
                to.getOccupancy().getLease().getReference(),
                to.getOccupancy().getUnit().getReference(),
                to.getOccupancy().getStartDate(),
                getDate(),
                to.getGrossAmount(),
                to.getNetAmount(),
                getType().name(),
                getFrequency().name(),
                to.getCurrency().getReference(),
                to.isNonComparable() ? 1 : 0,
                to.getPurchaseCount(),
                to.getComments(),
                to.getReportedBy(),
                to.getReportedAt()
                ))
        .collect(Collectors.toList());
    }

    public TurnoverImportManager uploadSpreadsheet(
            @Parameter(fileAccept = ".xlsx")
    final Blob spreadsheet){
        excelService.fromExcel(spreadsheet, TurnoverImport.class, "TurnoverImport")
        .forEach(l->l.importData(null));
        return this;
    }

    @Inject
    TurnoverRepository turnoverRepository;

    @Inject
    ExcelService excelService;

}
