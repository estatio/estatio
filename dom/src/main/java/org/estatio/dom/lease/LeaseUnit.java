/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.lease;

import java.util.List;

import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.Status;
import org.estatio.dom.WithInterval;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.lease.tags.Activities;
import org.estatio.dom.lease.tags.Activity;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.lease.tags.Brands;
import org.estatio.dom.lease.tags.Sector;
import org.estatio.dom.lease.tags.Sectors;
import org.estatio.dom.lease.tags.UnitSize;
import org.estatio.dom.lease.tags.UnitSizes;
import org.estatio.dom.tag.Taggable;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Index(
        name = "LEASE_UNIT_IDX",
        members = { "lease", "unit", "startDate" })
@javax.jdo.annotations.Unique(
        name = "LEASE_UNIT_IDX2",
        members = { "lease", "unit", "startDate" })
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByLeaseAndUnitAndStartDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseUnit "
                        + "WHERE lease == :lease "
                        + "&& unit == :unit "
                        + "&& startDate == :startDate"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseAndUnitAndEndDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseUnit "
                        + "WHERE lease == :lease "
                        + "&& unit == :unit "
                        + "&& endDate == :endDate")
})
@Named("Occupancy")
public class LeaseUnit extends EstatioTransactionalObject<LeaseUnit, Status> implements WithIntervalMutable<LeaseUnit>, Taggable {

    public LeaseUnit() {
        super("lease, startDate desc nullsLast, unit", Status.UNLOCKED, Status.LOCKED);
    }

    @Override
    public Status getLockable() {
        return getStatus();
    }

    @Override
    public void setLockable(Status lockable) {
        setStatus(lockable);
    }

    // //////////////////////////////////////

    private Status status;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Disabled
    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    // //////////////////////////////////////

    private Lease lease;

    @javax.jdo.annotations.Column(name = "LEASE_ID", allowsNull="false")
    @Title(sequence = "1", append = ":")
    @Hidden(where = Where.REFERENCES_PARENT)
    @Disabled
    public Lease getLease() {
        return lease;
    }

    public void setLease(final Lease lease) {
        this.lease = lease;
    }

    // //////////////////////////////////////

    private UnitForLease unit;

    @javax.jdo.annotations.Column(name = "UNIT_ID", allowsNull="false")
    @Title(sequence = "2", append = ":")
    @Hidden(where = Where.REFERENCES_PARENT)
    @Disabled
    public UnitForLease getUnit() {
        return unit;
    }

    public void setUnit(final UnitForLease unit) {
        this.unit = unit;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate startDate;

    @Optional
    @Disabled(reason="Change using ")
    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @Disabled
    @Optional
    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // //////////////////////////////////////

    private WithIntervalMutable.Helper<LeaseUnit> changeDates = new WithIntervalMutable.Helper<LeaseUnit>(this);

    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    public LeaseUnit changeDates(
            final @Named("Start Date") @Optional LocalDate startDate,
            final @Named("End Date") @Optional LocalDate endDate) {
        return changeDates.changeDates(startDate, endDate);
    }

    public String disableChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return isLocked() ? "Cannot modify when locked" : null;
    }

    @Override
    public LocalDate default0ChangeDates() {
        return changeDates.default0ChangeDates();
    }

    @Override
    public LocalDate default1ChangeDates() {
        return changeDates.default1ChangeDates();
    }

    @Override
    public String validateChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return changeDates.validateChangeDates(startDate, endDate);
    }



    // //////////////////////////////////////

    @Hidden
    @Override
    public Lease getWithIntervalParent() {
        return getLease();
    }

    @Hidden
    @Override
    public LocalDate getEffectiveStartDate() {
        return WithInterval.Util.effectiveStartDateOf(this);
    }

    @Hidden
    @Override
    public LocalDate getEffectiveEndDate() {
        return WithInterval.Util.effectiveEndDateOf(this);
    }

    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getEffectiveStartDate(), getEffectiveEndDate());
    }

    // //////////////////////////////////////

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(LocalDate localDate) {
        return getInterval().contains(localDate);
    }


    // //////////////////////////////////////


    private UnitSize unitSize;

    @javax.jdo.annotations.Column(name = "UNITSIZE_ID", allowsNull="true")
    @Hidden
    public UnitSize getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(final UnitSize unitSize) {
        this.unitSize = unitSize;
    }

    @javax.jdo.annotations.NotPersistent
    @NotPersisted
    @Disabled
    @Named("UnitSize")
    public String getUnitSizeName() {
        return getUnitSize() != null? getUnitSize().getName(): null;
    }
    public void setUnitSizeName(final String unitSizeName) {
        if(unitSizeName == null) {
            setUnitSize(null);
            return;
        }
        UnitSize unitSize = unitSizes.findByName(unitSizeName);
        if(unitSize == null) {
            unitSize = newTransientInstance(UnitSize.class);
            unitSize.setName(unitSizeName);
            setUnitSize(unitSize);
            persistIfNotAlready(unitSize);
        }
        setUnitSize(unitSize);
    }

    public LeaseUnit newUnitSize(final @Named("UnitSize") @Optional String unitSizeName) {
        setUnitSizeName(unitSizeName);
        return this;
    }
    public String disableNewUnitSize(
            final String unitSizeName) {
        return isLocked() ? "Cannot modify when locked" : null;
    }
    
    
    // //////////////////////////////////////
    
    
    private Sector sector;

    @javax.jdo.annotations.Column(name = "SECTOR_ID", allowsNull="true")
    @Hidden
    public Sector getSector() {
        return sector;
    }

    public void setSector(final Sector sector) {
        this.sector = sector;
    }

    @javax.jdo.annotations.NotPersistent
    @NotPersisted
    @Disabled
    @Named("Sector")
    public String getSectorName() {
        return getSector() != null? getSector().getName(): null;
    }
    public void setSectorName(final String sectorName) {
        if(sectorName == null) {
            setSector(null);
            setActivityName(null);
            return;
        }
        Sector sector = sectors.findByName(sectorName);
        if(sector == null) {
            sector = newTransientInstance(Sector.class);
            sector.setName(sectorName);
            setSector(sector);
            persistIfNotAlready(sector);
        }
        setSector(sector);
    }

    // //////////////////////////////////////


    private Activity activity;
    
    @javax.jdo.annotations.Column(name = "ACTIVITY_ID", allowsNull="true")
    @Hidden
    public Activity getActivity() {
        return activity;
    }
    
    public void setActivity(final Activity activity) {
        this.activity = activity;
    }
    
    @javax.jdo.annotations.NotPersistent
    @NotPersisted
    @Named("Activity")
    @Disabled
    public String getActivityName() {
        return getActivity() != null? getActivity().getName(): null;
    }
    public void setActivityName(final String activityName) {
        if(activityName == null) {
            setActivity(null);
            return;
        }
        Activity activity = activities.findBySectorAndName(getSector(), activityName);
        if(activity == null) {
            activity = newTransientInstance(Activity.class);
            activity.setSector(getSector());
            activity.setName(activityName);
            setActivity(activity);
            persistIfNotAlready(activity);
        }
        setActivity(activity);
    }
    
    @DescribedAs("Assign to new sector and activity")
    public LeaseUnit newSector(
            final @Named("Sector") String sectorName, 
            final @Named("Activity") String activityName) {
        setSectorName(sectorName);
        setActivityName(activityName);
        return this;
    }
    public String disableNewSector(
            final String sectorName,
            final String activityName) {
        return isLocked() ? "Cannot modify when locked" : null;
    }
    
    @DescribedAs("Assign to new activity (in an existing sector)")
    public LeaseUnit newActivity(
            final @Named("Sector") String sectorName,
            final @Named("Activity") String activityName) {
        setActivityName(activityName);
        return this;
    }
    public String disableNewActivity(
            final String sectorName,
            final String activityName) {
        return isLocked() ? "Cannot modify when locked" : null;
    }
    public List<String> choices0NewActivity() {
        return sectors.findUniqueNames();
    }

    // //////////////////////////////////////

    private Brand brand;
    
    @javax.jdo.annotations.Column(name = "BRAND_ID", allowsNull="true")
    @Hidden
    public Brand getBrand() {
        return brand;
    }
    
    public void setBrand(final Brand brand) {
        this.brand = brand;
    }
    
    @javax.jdo.annotations.NotPersistent
    @NotPersisted
    @Disabled
    @Named("Brand")
    public String getBrandName() {
        return getBrand() != null? getBrand().getName(): null;
    }
    public void setBrandName(final String brandName) {
        if(brandName == null) {
            setBrand(null);
            return;
        }
        Brand brand = brands.findByName(brandName);
        if(brand == null) {
            brand = newTransientInstance(Brand.class);
            brand.setName(brandName);
            setBrand(brand);
            persistIfNotAlready(brand);
        }
        setBrand(brand);
    }
    
    public LeaseUnit newBrand(final @Named("Brand") @Optional String brandName) {
        setBrandName(brandName);
        return this;
    }
    public String disableNewBrand(
            final String brandName) {
        return isLocked() ? "Cannot modify when locked" : null;
    }


    // //////////////////////////////////////
    
    @DescribedAs("Update unit size, sector, activity and/or brand")
    public LeaseUnit updateTags(
            final @Named("Unit size") @Optional String unitSizeName, 
            final @Named("Sector") @Optional String sectorName, 
            final @Named("Activity") @Optional String activityName,
            final @Named("Brand") @Optional String brandName) {
        setUnitSizeName(unitSizeName);
        setSectorName(sectorName);
        setActivityName(activityName);
        setBrandName(brandName);
        return this;
    }
    public String disableUpdateTags(
            final String unitSizeName, 
            final String sectorName, 
            final String activityName,
            final String brandName) {
        return isLocked() ? "Cannot modify when locked" : null;
    }
    public String default0UpdateTags() {
        return getUnitSizeName();
    }
    public List<String> choices0UpdateTags() {
        return unitSizes.findUniqueNames();
    }
    public String default1UpdateTags() {
        return getSectorName();
    }
    public List<String> choices1UpdateTags() {
        return sectors.findUniqueNames();
    }
    public String default2UpdateTags() {
        return getActivityName();
    }
    public List<String> choices2UpdateTags(
            final String unitSizeName, 
            final String sectorName) {
        final Sector sector = sectors.findByName(sectorName);
        return activities.findUniqueNames(sector);
    }
    public String default3UpdateTags() {
        return getBrandName();
    }
    public List<String> choices3UpdateTags() {
        return brands.findUniqueNames();
    }

    
    // //////////////////////////////////////


    private UnitSizes unitSizes;
    public final void injectUnitSizes(final UnitSizes unitSizes) {
        this.unitSizes = unitSizes;
    }
    
    private Brands brands;
    public final void injectBrands(final Brands brands) {
        this.brands = brands;
    }
    
    private Sectors sectors;
    public final void injectSectors(final Sectors sectors) {
        this.sectors = sectors;
    }

    private Activities activities;
    public final void injectActivities(final Activities activities) {
        this.activities = activities;
    }
    

}
