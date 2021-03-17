package org.estatio.module.lease.imports;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.base.dom.Importable;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.OccupancyRepository;
import org.estatio.module.lease.dom.occupancy.salesarea.SalesAreaLicense;
import org.estatio.module.lease.dom.occupancy.salesarea.SalesAreaLicenseRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.lease.imports.SalesAreaLicenseImport"
)
public class SalesAreaLicenseImport implements ExcelFixtureRowHandler, Importable {

    public SalesAreaLicenseImport(){}

    public SalesAreaLicenseImport(final Occupancy occupancy){
        this.leaseReference = occupancy.getLease().getReference();
        this.unitReference = occupancy.getUnit().getReference();
        this.occupancyStartDate = occupancy.getStartDate();
        this.licenseStartDate = occupancy.getEffectiveStartDate();
    }

    public SalesAreaLicenseImport(final SalesAreaLicense license){
        this.leaseReference = license.getOccupancy().getLease().getReference();
        this.unitReference = license.getOccupancy().getUnit().getReference();
        this.occupancyStartDate = license.getOccupancy().getStartDate();
        this.licenseStartDate = license.getStartDate();
        this.salesAreaNonFood = license.getSalesAreaNonFood();
        this.salesAreaFood = license.getSalesAreaFood();
        this.foodAndBeveragesArea = license.getFoodAndBeveragesArea();
    }

    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String leaseReference;

    @Getter @Setter
    @MemberOrder(sequence = "2")
    private String unitReference;

    @Getter @Setter
    @MemberOrder(sequence = "3")
    private LocalDate occupancyStartDate;

    @Getter @Setter
    @MemberOrder(sequence = "4")
    private LocalDate licenseStartDate;

    @Getter @Setter
    @MemberOrder(sequence = "5")
    private BigDecimal salesAreaNonFood;

    @Getter @Setter
    @MemberOrder(sequence = "6")
    private BigDecimal salesAreaFood;

    @Getter @Setter
    @MemberOrder(sequence = "7")
    private BigDecimal foodAndBeveragesArea;


    @Programmatic
    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData(previousRow);
    }

    // REVIEW: other import view models have @Action annotation here...  but in any case, is this view model actually ever surfaced in the UI?
    public List<Object> importData() {
        return importData(null);
    }

    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {

        final Lease lease = fetchLease(leaseReference);
        final Unit unit = fetchUnit(unitReference);
        final Occupancy occupancy = fetchOccupancy(lease, unit);

        SalesAreaLicense salesAreaLicense;

        if (getSalesAreaNonFood()==null && getSalesAreaFood()==null && getFoodAndBeveragesArea()==null){
            return Collections.emptyList();
        }

        for (SalesAreaLicense license : salesAreaLicenseRepository.findByOccupancy(occupancy)){
            if (license.getStartDate().equals(getLicenseStartDate())){
                license.setSalesAreaNonFood(getSalesAreaNonFood());
                license.setSalesAreaFood(getSalesAreaFood());
                license.setFoodAndBeveragesArea(getFoodAndBeveragesArea());
                return Lists.newArrayList(license);
            }
        }

        // when no license
        if (salesAreaLicenseRepository.findMostRecentForOccupancy(occupancy)==null){
            try {
                wrapperFactory.wrap(occupancy)
                        .createSalesAreaLicense(getSalesAreaNonFood(), getSalesAreaFood(), getFoodAndBeveragesArea(),
                                getLicenseStartDate());
                salesAreaLicense = occupancy.getCurrentSalesAreaLicense();
                return Lists.newArrayList(salesAreaLicense);
            } catch (Exception e){
                throw new ApplicationException(e);
            }
        } else {
            // when there is a license
            try {
                wrapperFactory.wrap(occupancy).createNextSalesAreaLicense(getLicenseStartDate(), getSalesAreaNonFood(), getSalesAreaFood(), getFoodAndBeveragesArea());
                salesAreaLicense = occupancy.getCurrentSalesAreaLicense();
                return Lists.newArrayList(salesAreaLicense);
            } catch (Exception e){
                throw new ApplicationException(e);
            }
        }
    }

    private Lease fetchLease(final String leaseReference) {
        final Lease lease;
        lease = leaseRepository.findLeaseByReference(leaseReference.trim().replaceAll("~", "+"));
        if (lease == null) {
            throw new ApplicationException(String.format("Lease with reference %s not found.", leaseReference));
        }
        return lease;
    }

    private Unit fetchUnit(final String unitReference) {
        final Unit u;
        u = unitRepository.findUnitByReference(unitReference);
        if (u == null) {
            throw new ApplicationException(String.format("Unit with reference %s not found.", unitReference));
        }
        return u;
    }

    private Occupancy fetchOccupancy(final Lease lease, final Unit unit) {
        final Occupancy o;
        o = occupancyRepository.findByLeaseAndUnitAndStartDate(lease, unit, getOccupancyStartDate());
        if (o == null) {
            throw new ApplicationException(String.format("Occupancy for lease %s and unit %s with start date %s not found.",
                    lease.getReference(),
                    unit.getReference(),
                    getOccupancyStartDate()));
        }
        return o;
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    UnitRepository unitRepository;

    @Inject
    OccupancyRepository occupancyRepository;

    @Inject
    SalesAreaLicenseRepository salesAreaLicenseRepository;

    @Inject
    WrapperFactory wrapperFactory;
}
