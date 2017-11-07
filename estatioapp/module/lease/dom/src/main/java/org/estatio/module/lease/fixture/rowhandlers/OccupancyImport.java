package org.estatio.module.lease.fixture.rowhandlers;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.estatio.dom.Importable;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.UnitRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.OccupancyRepository;
import org.estatio.module.lease.dom.Occupancy;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.OccupancyImport"
)
public class OccupancyImport implements ExcelFixtureRowHandler, Importable {

    private static final Logger LOG = LoggerFactory.getLogger(OccupancyImport.class);

    @Getter @Setter
    private String leaseReference;

    @Getter @Setter
    private String unitReference;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private String size;

    @Getter @Setter
    private String brand;

    @Getter @Setter
    private String sector;

    @Getter @Setter
    private String activity;

    @Getter @Setter
    private String reportTurnover;

    @Getter @Setter
    private String reportRent;

    @Getter @Setter
    private String reportOCR;

    static int counter = 0;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList(LeaseImport.class, UnitImport.class);
//    }

    @Override
    @Programmatic
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData(previousRow);
    }

    // REVIEW: is this view model actually ever surfaced in the UI?
    @Action(invokeOn = InvokeOn.OBJECT_AND_COLLECTION, publishing = Publishing.DISABLED, semantics = SemanticsOf.IDEMPOTENT)
    public List<Object> importData() {
        return importData(null);
    }

    @Override
    @Programmatic
    public List<Object> importData(Object previousRow) {

        final Lease lease = fetchLease(leaseReference);
        final Unit unit = unitRepository.findUnitByReference(unitReference);
        if (unitReference != null && unit == null) {
            throw new ApplicationException(String.format("Unit with reference %s not found.", unitReference));
        }
        Occupancy occupancy = occupancyRepository.findByLeaseAndUnitAndStartDate(lease, unit, startDate);
        if (occupancy == null) {
            occupancy = occupancyRepository.newOccupancy(lease, unit, startDate);
        }

        occupancy.setEndDate(endDate);
        occupancy.setUnitSizeName(size);
        occupancy.setBrandName(brand != null ? brand.replaceAll("\\p{C}", "").trim() : null, null, null);
        occupancy.setSectorName(sector);
        occupancy.setActivityName(activity);
        occupancy.setReportTurnover(reportTurnover != null ? Occupancy.OccupancyReportingType.valueOf(reportTurnover) : Occupancy.OccupancyReportingType.NO);
        occupancy.setReportRent(reportRent != null ? Occupancy.OccupancyReportingType.valueOf(reportRent) : Occupancy.OccupancyReportingType.NO);
        occupancy.setReportOCR(reportOCR != null ? Occupancy.OccupancyReportingType.valueOf(reportOCR) : Occupancy.OccupancyReportingType.NO);

        return Lists.newArrayList(occupancy);

    }

    private Lease fetchLease(final String leaseReference) {
        final Lease lease;
        lease = leaseRepository.findLeaseByReference(leaseReference.trim().replaceAll("~", "+"));
        if (lease == null) {
            throw new ApplicationException(String.format("Lease with reference %s not found.", leaseReference));
        }
        return lease;
    }

    @Inject
    private OccupancyRepository occupancyRepository;

    @Inject
    private UnitRepository unitRepository;

    @Inject
    LeaseRepository leaseRepository;

}
