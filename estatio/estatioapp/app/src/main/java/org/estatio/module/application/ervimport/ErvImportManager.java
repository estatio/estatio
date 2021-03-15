package org.estatio.module.application.ervimport;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.asset.dom.erv.EstimatedRentalValue;
import org.estatio.module.asset.dom.erv.EstimatedRentalValueRepository;
import org.estatio.module.asset.dom.erv.Type;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL, objectType = "org.estatio.module.application.ervimport.ErvImportManager")
public class ErvImportManager {

    public String title(){
        return "ERV import manager";
    }

    public ErvImportManager(){}

    public ErvImportManager(final Property property, final Type type, final LocalDate date){
        this.property = property;
        this.type = type;
        this.date = date;
    }

    @Getter @Setter
    private Property property;

    @Getter @Setter
    private Type type;

    @Getter @Setter
    private LocalDate date;

    @Getter @Setter
    private List<ErvImport> lines = Lists.newArrayList();

    public List<ErvImport> getLines(){
        List<ErvImport> result = new ArrayList<>();
        unitRepository.findByPropertyAndActiveOnDate(getProperty(), getDate()).stream().sorted().forEach(u->{

            final EstimatedRentalValue erv = estimatedRentalValueRepository.findUnique(u, getDate(), getType());
            ErvImport line;

            if (erv!=null){
                line = new ErvImport(erv);
                // try to find a previous value
                EstimatedRentalValue prevErv = estimatedRentalValueRepository.findByUnitAndType(u, getType()).stream().filter(e->e.getDate().isBefore(getDate())).findFirst().orElse(null);
                if (prevErv!=null){
                    line.setPreviousDate(prevErv.getDate());
                    line.setPreviousValue(prevErv.getValue());
                }
            } else {
                // try to find a previous value
                EstimatedRentalValue prevErv = estimatedRentalValueRepository.findByUnitAndType(u, getType()).stream().filter(e->e.getDate().isBefore(getDate())).findFirst().orElse(null);
                if (prevErv!=null){
                    line = new ErvImport(prevErv, getDate());
                } else {
                    // create a new blank line
                    line = new ErvImport(u, getDate(), getType());
                }
            }

            if (line!=null) {
                line.setCurrentBrand(brandNameFromOccupancy(u));
                result.add(line);
            }
        });

        return result;
    }

    public Blob downloadSpreadsheet(@Nullable final String fileName){
        String fileNameToUse = fileName!=null ? fileName : createFileName();
        return excelService.toExcel(getLines(), ErvImport.class, "ErvImport", fileNameToUse.concat(".xlsx"));
    }

    private String createFileName(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("Erv_");
        buffer.append(getProperty().getReference());
        buffer.append("_");
        buffer.append(getDate().toString("yyyy-MM-dd"));
        buffer.append("_");
        buffer.append(getType().name());
        return buffer.toString();
    }

    public ErvImportManager uploadSpreadsheet(
            @Parameter(fileAccept = ".xlsx")
    final Blob spreadsheet){
        excelService.fromExcel(spreadsheet, ErvImport.class, "ErvImport")
        .forEach(l->l.importData(null));
        return this;
    }

    private String brandNameFromOccupancy(final Unit unit){
        Occupancy occOnDate = occupancyRepository.occupanciesByUnitAndInterval(unit, LocalDateInterval.including(getDate(), getDate())).stream().findFirst().orElse(null);
        if (occOnDate!=null) {
            return occOnDate.getBrand()!=null ? occOnDate.getBrand().getName() : null;
        }
        return null;
    }

    @Inject
    EstimatedRentalValueRepository estimatedRentalValueRepository;

    @Inject
    UnitRepository unitRepository;

    @Inject OccupancyRepository occupancyRepository;

    @Inject
    ExcelService excelService;

}
