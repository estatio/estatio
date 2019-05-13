package org.estatio.module.asset.imports;

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

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.asset.dom.erv.EstimatedRentalValue;
import org.estatio.module.asset.dom.erv.EstimatedRentalValueRepository;
import org.estatio.module.asset.dom.erv.Type;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL, objectType = "org.estatio.module.asset.imports.ErvImportManager")
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
        unitRepository.findByProperty(getProperty()).forEach(u->{
            final EstimatedRentalValue erv = estimatedRentalValueRepository.findUnique(u, getDate(), getType());
            if (erv!=null){
                ErvImport line = new ErvImport(erv);
                line.setDate(getDate());
                line.setValue(erv.getValue());
                result.add(line);
            } else {
                // try to find a previous value
                EstimatedRentalValue prevErv = estimatedRentalValueRepository.findByUnitAndType(u, getType()).stream().filter(e->e.getDate().isBefore(getDate())).findFirst().orElse(null);
                if (prevErv!=null){
                    ErvImport line = new ErvImport(prevErv);
                    line.setDate(getDate());
                    result.add(line);
                }
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

    @Inject
    EstimatedRentalValueRepository estimatedRentalValueRepository;

    @Inject
    UnitRepository unitRepository;

    @Inject
    ExcelService excelService;

}
