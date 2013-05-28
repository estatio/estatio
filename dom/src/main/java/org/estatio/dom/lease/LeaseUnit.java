package org.estatio.dom.lease;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.asset.Unit;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
public class LeaseUnit extends EstatioTransactionalObject implements Comparable<LeaseUnit> {

    
    private Lease lease;

    @Title(sequence = "1", append = ":")
    @MemberOrder(sequence = "1")
    @Hidden(where = Where.REFERENCES_PARENT)
    public Lease getLease() {
        return lease;
    }

    public void setLease(final Lease lease) {
        this.lease = lease;
    }

    public void modifyLease(final Lease lease) {
        Lease currentLease = getLease();
        if (lease == null || lease.equals(currentLease)) {
            return;
        }
        lease.addToUnits(this);
    }

    public void clearLease() {
        Lease currentLease = getLease();
        if (currentLease == null) {
            return;
        }
        currentLease.removeFromUnits(this);
    }

    private UnitForLease unit;

    @Title(sequence = "2", append = ":")
    @MemberOrder(sequence = "2")
    @Hidden(where = Where.REFERENCES_PARENT)
    public UnitForLease getUnit() {
        return unit;
    }

    public void setUnit(final UnitForLease unit) {
        this.unit = unit;
    }

    public void modifyUnit(final UnitForLease unit) {
        UnitForLease currentUnit = getUnit();
        if (unit == null || unit.equals(currentUnit)) {
            return;
        }
        unit.addToLeases(this);
    }

    public void clearUnit() {
        UnitForLease currentUnit = getUnit();
        if (currentUnit == null) {
            return;
        }
        currentUnit.removeFromLeases(this);
    }

    
    
    @javax.jdo.annotations.Persistent
    private LocalDate startDate;

    @Optional
    @MemberOrder(sequence = "3")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    
    
    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @Optional
    @MemberOrder(sequence = "4")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

//    private LocalDate tenancyStartDate;
//
//    @Optional
//    @MemberOrder(sequence = "4")
//    public LocalDate getTenancyStartDate() {
//        return tenancyStartDate;
//    }
//
//    public void setTenancyStartDate(final LocalDate tenancyStartDate) {
//        this.tenancyStartDate = tenancyStartDate;
//    }
//
//    private LocalDate tenancyEndDate;
//
//    @Optional
//    @MemberOrder(sequence = "5")
//    public LocalDate getTenancyEndDate() {
//        return tenancyEndDate;
//    }
//
//    public void setTenancyEndDate(final LocalDate tenancyEndDate) {
//        this.tenancyEndDate = tenancyEndDate;
//    }

    private LeaseUnitBrand brand;

    @MemberOrder(sequence = "6")
    @Optional
    public LeaseUnitBrand getBrand() {
        return brand;
    }

    public void setBrand(final LeaseUnitBrand brand) {
        this.brand = brand;
    }

    private LeaseUnitSector sector;

    @MemberOrder(sequence = "7")
    @Optional
    public LeaseUnitSector getSector() {
        return sector;
    }

    public void setSector(final LeaseUnitSector sector) {
        this.sector = sector;
    }

    private LeaseUnitActivity activity;

    @MemberOrder(sequence = "8")
    @Optional
    public LeaseUnitActivity getActivity() {
        return activity;
    }

    public void setActivity(final LeaseUnitActivity activity) {
        this.activity = activity;
    }

    
    // {{ Comparable impl
    @Override
    @Hidden
    public int compareTo(LeaseUnit o) {
        Unit thisUnit = this.getUnit();
        Unit otherUnit = o.getUnit();
        if (thisUnit == null && otherUnit == null)
            return 0;
        if (thisUnit == null)
            return 1;
        if (otherUnit == null)
            return -1;
        return thisUnit.getReference().compareTo(otherUnit.getReference());
    }
    // }}
}
