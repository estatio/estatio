package org.estatio.module.lease.imports;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.message.MessageService;
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
        objectType = "org.estatio.module.lease.imports.SectorAndActivityImportExportManager"
)
public class SectorAndActivityImportExportManager {

    public SectorAndActivityImportExportManager() {
    }

    public String title(){
        return "Sector And Activity Import Export Manager";
    }

    public List<SectorAndActivityImportExport> getSectorAndActivityLines(){
        List<SectorAndActivityImportExport> result = new ArrayList<>();
        List<Sector> sectors = sectorRepository.allSectors();
        sectors.forEach(sector -> result.addAll(getSectorAndActivityImports(sector)));
        return result;
    }

    private List<SectorAndActivityImportExport> getSectorAndActivityImports(final Sector s) {
        List<SectorAndActivityImportExport> result = new ArrayList<>();
        SectorAndActivityImportExport impSector = new SectorAndActivityImportExport();
        impSector.setSectorName(s.getName());
        impSector.setSectorDescription(s.getDescription());
        impSector.setSectorSortOrder(s.getSortOrder());
        result.add(impSector);

        if (!s.getActivities().isEmpty()) {
            Lists.newArrayList(s.getActivities()).forEach(a -> {
                SectorAndActivityImportExport imp = new SectorAndActivityImportExport();
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
        WorksheetSpec sectorAndActivityLineSpec = new WorksheetSpec(SectorAndActivityImportExport.class, "Sectors and Activities");
        WorksheetContent sectorAndActivityLineContent = new WorksheetContent(getSectorAndActivityLines(), sectorAndActivityLineSpec);
        return excelService.toExcel(sectorAndActivityLineContent, filename);
    }

    public String default0Download(){
        return "Sectors and Activities " + clockService.now().toString("dd-MM-yyyy") + ".xlsx";
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public SectorAndActivityImportExportManager upload(final Blob spreadSheet){
        List<SectorAndActivityImportExport> allSectorsAndActivities = getSectorAndActivityLines();
        List<SectorAndActivityImportExport> newSectorsAndActivities = excelService.fromExcel(spreadSheet, SectorAndActivityImportExport.class, "Sectors and Activities", Mode.RELAXED);
        if (tryToRemoveSectorsAndActivities(getSectorsAndActivitiesToRemove(allSectorsAndActivities, newSectorsAndActivities))) {
            newSectorsAndActivities.forEach(imp -> imp.importData(null));
        }

        return new SectorAndActivityImportExportManager();
    }

    private List<SectorAndActivityImportExport> getSectorsAndActivitiesToRemove(List<SectorAndActivityImportExport> oldImps, List<SectorAndActivityImportExport> newImps) {
        return oldImps.stream().filter(imp -> newImps.stream().allMatch(newImp -> {
            if (StringUtils.equals(newImp.getSectorName(), imp.getSectorName())) {
                return !StringUtils.equals(newImp.getActivityName(), imp.getActivityName());
            } else {
                return true;
            }
        })).collect(Collectors.toList());
    }

    private boolean tryToRemoveSectorsAndActivities(List<SectorAndActivityImportExport> toRemove) {
        boolean success = true;

        // Try to remove activities first
        List<String> nonremovableActivities = new ArrayList<>();
        List<SectorAndActivityImportExport> activitiesToRemove = toRemove.stream().filter(imp -> imp.getActivityName()!=null).collect(Collectors.toList());
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
            success = false;
            messageService.raiseError(String.format("The following activities are already in use, cannot be removed: %s",
                    nonremovableActivities.stream().collect(Collectors.joining(", "))));
        }
        
        // Then try to remove sectors
        List<String> nonremovableSectors = new ArrayList<>();
        List<SectorAndActivityImportExport> sectorsToRemove = toRemove.stream().filter(imp -> imp.getActivityName()==null).collect(Collectors.toList());
        sectorsToRemove.forEach(imp -> {
            Sector sector = sectorRepository.findByName(imp.getSectorName());
            if (occupancyRepository.findBySector(sector).isEmpty() && activityRepository.findBySector(sector).isEmpty()) {
                repositoryService.remove(sector);
            } else {
                nonremovableSectors.add(imp.getSectorName());
            }
        });

        if (!nonremovableSectors.isEmpty()) {
            success = false;
            messageService.raiseError(String.format("The following sectors are already in use, cannot be removed: %s",
                    nonremovableSectors.stream().collect(Collectors.joining(", "))));
        }

        return success;
    }

    @Inject
    ExcelService excelService;

    @Inject
    ClockService clockService;

    @Inject
    MessageService messageService;

    @Inject
    RepositoryService repositoryService;

    @Inject
    SectorRepository sectorRepository;

    @Inject
    ActivityRepository activityRepository;

    @Inject
    OccupancyRepository occupancyRepository;

}
