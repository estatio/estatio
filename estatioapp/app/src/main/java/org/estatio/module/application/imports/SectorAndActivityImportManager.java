package org.estatio.module.application.imports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.value.Blob;

import org.estatio.module.lease.dom.occupancy.tags.Sector;
import org.estatio.module.lease.dom.occupancy.tags.SectorRepository;
import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.application.imports.SectorAndActivityImportManager"
)
public class SectorAndActivityImportManager {

    public SectorAndActivityImportManager() {
    }

    public String title(){
        return "Sector And Activity Import Manager";
    }

    public List<SectorAndActivityImport> getSectorAndActivityLines(){
        List<SectorAndActivityImport> result = new ArrayList<>();
        List<Sector> sectors = sectorRepository.allSectors();
        sectors.forEach(sector -> {
            result.addAll(whenHavingActivities(sector));
//            if (sector.getActivities().isEmpty()) {
//                result.add(whenHavingNoActivities(sector));
//            } else {
//                result.addAll(whenHavingActivities(sector));
//            }
        });
        return result;
    }

    private List<SectorAndActivityImport> whenHavingActivities(final Sector s) {
        List<SectorAndActivityImport> result = new ArrayList<>();
        SectorAndActivityImport impSector = new SectorAndActivityImport();
        impSector.setSectorName(s.getName());
        impSector.setSectorDescription(s.getDescription());
        impSector.setSectorSortOrder(s.getSortOrder());
        result.add(impSector);

        if (!s.getActivities().isEmpty()) {
            Lists.newArrayList(s.getActivities()).forEach(a -> {
                SectorAndActivityImport imp = new SectorAndActivityImport();
                imp.setSectorName(s.getName());
                imp.setSectorDescription(s.getDescription());
                imp.setSectorSortOrder(s.getSortOrder());
                imp.setActivityName(a.getName());
                imp.setActivityDescription(a.getDescription());
                imp.setActivitySortOrder(a.getSortOrder());
                result.add(imp);
            });
        }

        return result;
    }

//    private SectorAndActivityImport whenHavingNoActivities(final Sector s) {
//        SectorAndActivityImport imp = new SectorAndActivityImport();
//        imp.setSectorName(s.getName());
//        imp.setSectorDescription(s.getDescription());
//        imp.setSectorSortOrder(s.getSortOrder());
//        return imp;
//    }

    @Action(semantics = SemanticsOf.SAFE)
    public Blob download(final String filename){
        WorksheetSpec sectorAndActivityLineSpec = new WorksheetSpec(SectorAndActivityImport.class, "Sectors and Activities");
        WorksheetContent sectorAndActivityLineContent = new WorksheetContent(getSectorAndActivityLines(), sectorAndActivityLineSpec);
        return excelService.toExcel(Arrays.asList(sectorAndActivityLineContent), filename);
    }

    public String default0Download(){
        return "Sectors and Activities " + clockService.now().toString("dd-MM-yyyy") + ".xlsx";
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public SectorAndActivityImportManager upload(final Blob spreadSheet){
        excelService.fromExcel(spreadSheet, SectorAndActivityImport.class, "Sectors and Activities", Mode.RELAXED).forEach(imp->imp.importData(null));
        return new SectorAndActivityImportManager();
    }

    @Inject
    ExcelService excelService;

    @Inject
    ClockService clockService;

    @Inject
    SectorRepository sectorRepository;

}
