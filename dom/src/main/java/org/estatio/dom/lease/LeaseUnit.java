package org.estatio.dom.lease;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.asset.Unit;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

@PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
public class LeaseUnit extends EstatioTransactionalObject implements Comparable<LeaseUnit> {

    // {{ Lease (property)
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
        // check for no-op
        if (lease == null || lease.equals(currentLease)) {
            return;
        }
        // delegate to parent to associate
        lease.addToUnits(this);
        // additional business logic
        // onModifyLease(currentLease, lease);
    }

    public void clearLease() {
        Lease currentLease = getLease();
        // check for no-op
        if (currentLease == null) {
            return;
        }
        // delegate to parent to dissociate
        currentLease.removeFromUnits(this);
        // additional business logic
        // onClearLease(currentLease);
    }

    // }}

    // {{ Unit (property)
    private Unit unit;

    @Title(sequence = "2", append = ":")
    @MemberOrder(sequence = "2")
    @Hidden(where = Where.REFERENCES_PARENT)
    public Unit getUnit() {
        return unit;
    }

    public void setUnit(final Unit unit) {
        this.unit = unit;
    }

    public void modifyUnit(final Unit unit) {
        Unit currentUnit = getUnit();
        // check for no-op
        if (unit == null || unit.equals(currentUnit)) {
            return;
        }
        // delegate to parent to associate
        unit.addToLeases(this);
        // additional business logic
        // onModifyUnit(currentUnit, unit);
    }

    public void clearUnit() {
        Unit currentUnit = getUnit();
        // check for no-op
        if (currentUnit == null) {
            return;
        }
        // delegate to parent to dissociate
        currentUnit.removeFromLeases(this);
        // additional business logic
        // onClearUnit(currentUnit);
    }

    // {{ StartDate (property)
    private LocalDate startDate;

    @Persistent
    @Optional
    @MemberOrder(sequence = "3")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    // }}

    // {{ EndDate (property)
    // meaning, all meant to bound an item to a period.
    private LocalDate endDate;

    @Persistent
    @Optional
    @MemberOrder(sequence = "4")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // }}

    // {{ TenancyStartDate (property)
    private LocalDate tenancyStartDate;

    @Optional
    @MemberOrder(sequence = "4")
    public LocalDate getTenancyStartDate() {
        return tenancyStartDate;
    }

    public void setTenancyStartDate(final LocalDate tenancyStartDate) {
        this.tenancyStartDate = tenancyStartDate;
    }

    // }}

    // {{ TenancyEndDate (property)
    private LocalDate tenancyEndDate;

    @Optional
    @MemberOrder(sequence = "5")
    public LocalDate getTenancyEndDate() {
        return tenancyEndDate;
    }

    public void setTenancyEndDate(final LocalDate tenancyEndDate) {
        this.tenancyEndDate = tenancyEndDate;
    }

    // }}

    // {{ Brand (property)
    private String brand;

    @Optional
    @MemberOrder(sequence = "6")
    public String getBrand() {
        return brand;
    }

    public void setBrand(final String brand) {
        this.brand = brand;
    }

    // }}

    // {{ Sector (property)
    private String sector;

    @Optional
    @MemberOrder(sequence = "7")
    public String getSector() {
        return sector;
    }

    public void setSector(final String sector) {
        this.sector = sector;
    }

    // }}

    // {{ Activity (property)
    private String activity;

    @Optional
    @MemberOrder(sequence = "8")
    public String getActivity() {
        return activity;
    }

    public void setActivity(final String activity) {
        this.activity = activity;
    }

    // }}

    @Override
    @Hidden
    public int compareTo(LeaseUnit o) {
        return this.getUnit().getReference().compareTo(o.getUnit().getReference());
    }
}
