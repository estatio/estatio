package org.estatio.module.lease.imports;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.value.Blob;

import org.estatio.module.lease.dom.occupancy.OccupancyRepository;
import org.estatio.module.lease.dom.occupancy.tags.Activity;
import org.estatio.module.lease.dom.occupancy.tags.ActivityRepository;
import org.estatio.module.lease.dom.occupancy.tags.Sector;
import org.estatio.module.lease.dom.occupancy.tags.SectorRepository;
import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.lease.imports.SectorAndActivityImportManager"
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
        sectors.forEach(sector -> result.addAll(getSectorAndActivityImports(sector)));
        return result;
    }

    private List<SectorAndActivityImport> getSectorAndActivityImports(final Sector s) {
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

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-download")
    public Blob download(final String filename){
        WorksheetSpec sectorAndActivityLineSpec = new WorksheetSpec(SectorAndActivityImport.class, "Sectors and Activities");
        WorksheetContent sectorAndActivityLineContent = new WorksheetContent(getSectorAndActivityLines(), sectorAndActivityLineSpec);
        return excelService.toExcel(sectorAndActivityLineContent, filename);
    }

    public String default0Download(){
        return "Sectors and Activities " + clockService.now().toString("dd-MM-yyyy") + ".xlsx";
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public SectorAndActivityImportManager upload(final Blob spreadSheet){
        List<SectorAndActivityImport> newSectorsAndActivities = excelService.fromExcel(spreadSheet, SectorAndActivityImport.class, "Sectors and Activities", Mode.RELAXED);
        newSectorsAndActivities.forEach(imp -> imp.importData(null));

        return new SectorAndActivityImportManager();
    }

    public String validateUpload(final Blob spreadSheet) {
        List<SectorAndActivityImport> newSectorsAndActivities = excelService.fromExcel(spreadSheet, SectorAndActivityImport.class, "Sectors and Activities", Mode.RELAXED);
        List<String> errors = tryToRemoveSectorsAndActivities(getSectorsAndActivitiesToRemove(getSectorAndActivityLines(), newSectorsAndActivities));

        return errors.isEmpty() ? null : errors.stream().collect(Collectors.joining("\n"));
    }

    private List<SectorAndActivityImport> getSectorsAndActivitiesToRemove(List<SectorAndActivityImport> oldImps, List<SectorAndActivityImport> newImps) {
        return oldImps.stream().filter(imp -> newImps.stream().allMatch(newImp -> {
            if (StringUtils.equals(newImp.getSectorName(), imp.getSectorName())) {
                return !StringUtils.equals(newImp.getActivityName(), imp.getActivityName());
            } else {
                return true;
            }
        })).collect(Collectors.toList());
    }

    private List<String> tryToRemoveSectorsAndActivities(List<SectorAndActivityImport> toRemove) {
        List<String> errors = new ArrayList<>();

        // Try to remove activities first
        List<String> nonremovableActivities = new ArrayList<>();
        List<SectorAndActivityImport> activitiesToRemove = toRemove.stream().filter(imp -> imp.getActivityName()!=null).collect(Collectors.toList());
        activitiesToRemove.forEach(imp -> {
            Sector sector = sectorRepository.findByName(imp.getSectorName());
            Activity activity = activityRepository.findBySectorAndName(sector, imp.getActivityName());
            if (occupancyRepository.findByActivity(activity).isEmpty()) {
                repositoryService.remove(activity);
            } else {
                nonremovableActivities.add(imp.getActivityName());
            }
        });
        if (!nonremovableActivities.isEmpty()) {
            errors.add(String.format("The following activities are already in use, cannot be removed: %s",
                    nonremovableActivities.stream().collect(Collectors.joining(", "))));
        }
        
        // Then try to remove sectors
        List<String> nonremovableSectors = new ArrayList<>();
        List<SectorAndActivityImport> sectorsToRemove = toRemove.stream().filter(imp -> imp.getActivityName()==null).collect(Collectors.toList());
        sectorsToRemove.forEach(imp -> {
            Sector sector = sectorRepository.findByName(imp.getSectorName());
            if (occupancyRepository.findBySector(sector).isEmpty() && activityRepository.findBySector(sector).isEmpty()) {
                repositoryService.remove(sector);
            } else {
                nonremovableSectors.add(imp.getSectorName());
            }
        });
        if (!nonremovableSectors.isEmpty()) {
            errors.add(String.format("The following sectors are already in use, cannot be removed: %s",
                    nonremovableSectors.stream().collect(Collectors.joining(", "))));
        }

        return errors;
    }

    @Inject
    ExcelService excelService;

    @Inject
    ClockService clockService;

    @Inject
    RepositoryService repositoryService;

    @Inject
    SectorRepository sectorRepository;

    @Inject
    ActivityRepository activityRepository;

    @Inject
    OccupancyRepository occupancyRepository;

}
