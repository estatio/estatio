package org.estatio.module.lease.imports;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.estatio.module.base.dom.Importable;
import org.estatio.module.lease.dom.occupancy.tags.Activity;
import org.estatio.module.lease.dom.occupancy.tags.ActivityRepository;
import org.estatio.module.lease.dom.occupancy.tags.Sector;
import org.estatio.module.lease.dom.occupancy.tags.SectorRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.SectorAndActivityImport"
)
public class SectorAndActivityImport implements Importable, ExcelFixtureRowHandler {

    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String sectorName;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private String sectorDescription;

    @Getter @Setter
    @MemberOrder(sequence = "3")
    private Integer sectorSortOrder;

    @Getter @Setter
    @MemberOrder(sequence = "4")
    private String activityName;

    @Getter @Setter
    @MemberOrder(sequence = "5")
    private String activityDescription;

    @Getter @Setter
    @MemberOrder(sequence = "6")
    private Integer activitySortOrder;

    @Override
    public List<Object> handleRow(final FixtureScript.ExecutionContext executionContext, final ExcelFixture excelFixture, final Object previousRow) {
        SectorAndActivityImport previousImport = (SectorAndActivityImport) previousRow;
        return importData(previousImport);
    }

    @Override
    public List<Object> importData(final Object previousRow) {

        final SectorAndActivityImport previousSectorImport = (SectorAndActivityImport) previousRow;

        final List<Object> createdSectorsAndActivities = Lists.newArrayList();
        if (getSectorName() != null && getSectorDescription() != null) {
            Sector specifiedSector = sectorRepository.findOrCreate(getSectorName());
            specifiedSector.setDescription(getSectorDescription());
            specifiedSector.setSortOrder(getSectorSortOrder());
        }

        if (getSectorName() == null) {
            final String previousName = previousSectorImport.getSectorName();
            if (previousName == null) {
                throw new IllegalArgumentException("Sector name is null and previous row's sector name also null");
            }
            setSectorName(previousName);
        }
        final Sector sector = sectorRepository.findOrCreate(getSectorName());
        createdSectorsAndActivities.add(sector);

        if (getActivityName() != null && getActivityDescription() != null) {
            final Activity activity = activityRepository.findOrCreate(sector, getActivityName());
            activity.setDescription(getActivityDescription());
            activity.setSortOrder(getActivitySortOrder());
            createdSectorsAndActivities.add(activity);
        }

        return createdSectorsAndActivities;
    }

    @Inject
    private SectorRepository sectorRepository;

    @Inject
    private ActivityRepository activityRepository;

}