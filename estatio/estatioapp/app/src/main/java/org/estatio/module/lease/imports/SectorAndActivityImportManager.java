package org.estatio.module.lease.imports;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetContent;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

import org.estatio.module.lease.dom.occupancy.OccupancyRepository;
import org.estatio.module.lease.dom.occupancy.tags.Activity;
import org.estatio.module.lease.dom.occupancy.tags.ActivityRepository;
import org.estatio.module.lease.dom.occupancy.tags.Sector;
import org.estatio.module.lease.dom.occupancy.tags.SectorRepository;

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

    public List<Sector> getSectors() {
        return sectorRepository.allSectors()
                .stream()
                .sorted(Comparator.comparing(Sector::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    public List<Activity> getActivities() {
        return activityRepository.allActivities()
                .stream()
                .sorted(Comparator.comparing(Activity::getSector).thenComparing(Activity::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    private List<SectorAndActivityImport> getSectorAndActivityLines(){
        List<SectorAndActivityImport> result = new ArrayList<>();
        getSectors().forEach(sector -> result.addAll(getSectorAndActivityImports(sector)));
        return result;
    }

    private List<SectorAndActivityImport> getSectorAndActivityImports(final Sector s) {
        List<SectorAndActivityImport> result = new ArrayList<>();
        SectorAndActivityImport impSector = new SectorAndActivityImport();
        impSector.setSectorName(s.getName());
        impSector.setSectorDescription(s.getDescription());
        impSector.setSectorSortOrder(s.getSortOrder());

        if (s.getActivities().isEmpty()) {
            result.add(impSector);
        } else {
            Lists.newArrayList(s.getActivities())
                    .stream()
                    .sorted(Comparator.comparing(Activity::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                    .forEach(a -> {
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
        SectorAndActivityImport prev = null;
        for (SectorAndActivityImport imp : newSectorsAndActivities){
            imp.importData(prev);
            prev = imp;
        }

        return new SectorAndActivityImportManager();
    }

    public String validateUpload(final Blob spreadSheet) {
        final List<SectorAndActivityImport> newSectorsAndActivities = excelService.fromExcel(spreadSheet, SectorAndActivityImport.class, "Sectors and Activities", Mode.RELAXED);
        final List<SectorAndActivityImport> oldSectorsAndActivities = getSectorAndActivityLines();
        List<String> errors = new ArrayList<>();
        // first try to remove activities
        String activitiesError = tryToRemoveActivities(getActivitiesToRemove(oldSectorsAndActivities, newSectorsAndActivities));
        if (activitiesError!=null) errors.add(activitiesError);
        // then try to remove sectors
        String sectorsError = tryToRemoveSectors(getSectorsToRemove(oldSectorsAndActivities, newSectorsAndActivities));
        if (sectorsError!=null) errors.add(sectorsError);

        return errors.isEmpty() ? null : errors.stream().collect(Collectors.joining("\n"));
    }

    private List<SectorAndActivityImport> getActivitiesToRemove(List<SectorAndActivityImport> oldImps, List<SectorAndActivityImport> newImps) {
        return oldImps
                .stream()
                .filter(imp -> imp.getActivityName()!=null)
                .filter(imp -> newImps.stream().allMatch(newImp -> {
                    if (StringUtils.equals(newImp.getSectorName(), imp.getSectorName())) {
                        return !StringUtils.equals(newImp.getActivityName(), imp.getActivityName());
                    } else {
                        return true;
                    }
                }))
                .collect(Collectors.toList());
    }

    private List<String> getSectorsToRemove(List<SectorAndActivityImport> oldImps, List<SectorAndActivityImport> newImps) {
        return oldImps
                .stream()
                .filter(imp -> newImps.stream().allMatch(newImp -> !StringUtils.equals(imp.getSectorName(), newImp.getSectorName())))
                .map(SectorAndActivityImport::getSectorName)
                .collect(Collectors.toList());
    }

    private String tryToRemoveActivities(List<SectorAndActivityImport> activitiesToRemove) {
        List<String> nonremovableActivities = new ArrayList<>();
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
            return String.format("The following activities are already in use, cannot be removed: %s",
                    nonremovableActivities.stream().collect(Collectors.joining(", ")));
        }

        return null;
    }

    private String tryToRemoveSectors(List<String> sectorsToRemove) {
        List<String> nonremovableSectors = new ArrayList<>();
        sectorsToRemove.forEach(sectorName -> {
            Sector sector = sectorRepository.findByName(sectorName);
            if (occupancyRepository.findBySector(sector).isEmpty() && activityRepository.findBySector(sector).isEmpty()) {
                repositoryService.remove(sector);
            } else {
                nonremovableSectors.add(sectorName);
            }
        });
        if (!nonremovableSectors.isEmpty()) {
            return String.format("The following sectors are already in use, cannot be removed: %s",
                    nonremovableSectors.stream().collect(Collectors.joining(", ")));
        }

        return null;
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
