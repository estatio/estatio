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

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioMutableObject;
import org.estatio.dom.JdoColumnLength;
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

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Unique(
        name = "Occupancy_lease_unit_startDate_UNQ",
        members = { "lease", "unit", "startDate" })
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByLeaseAndUnitAndStartDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.Occupancy "
                        + "WHERE lease == :lease "
                        + "&& unit == :unit "
                        + "&& startDate == :startDate"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseAndUnitAndEndDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.Occupancy "
                        + "WHERE lease == :lease "
                        + "&& unit == :unit "
                        + "&& endDate == :endDate")
})
public class Occupancy
        extends EstatioMutableObject<Occupancy>
        implements WithIntervalMutable<Occupancy>, Taggable {

    public Occupancy() {
        super("lease, startDate desc nullsLast, unit");
    }

    // //////////////////////////////////////

    private Lease lease;

    @javax.jdo.annotations.Column(name = "leaseId", allowsNull = "false")
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

    @javax.jdo.annotations.Column(name = "unitId", allowsNull = "false")
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

    @Disabled(reason = "Change using action")
    @Optional
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

    @Disabled(reason = "Change using action")
    @Optional
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // //////////////////////////////////////

    private WithIntervalMutable.Helper<Occupancy> changeDates = new WithIntervalMutable.Helper<Occupancy>(this);

    WithIntervalMutable.Helper<Occupancy> getChangeDates() {
        return changeDates;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    public Occupancy changeDates(
            final @Named("Start Date") @Optional LocalDate startDate,
            final @Named("End Date") @Optional LocalDate endDate) {
        return getChangeDates().changeDates(startDate, endDate);
    }

    public String disableChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return null;
    }

    @Override
    public LocalDate default0ChangeDates() {
        return getChangeDates().default0ChangeDates();
    }

    @Override
    public LocalDate default1ChangeDates() {
        return getChangeDates().default1ChangeDates();
    }

    @Override
    public String validateChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return getChangeDates().validateChangeDates(startDate, endDate);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    public Occupancy terminate(
            final @Named("End Date") LocalDate endDate) {
        setEndDate(endDate);
        return this;
    }

    public String validateTerminate(
            final LocalDate endDate) {
        return getEffectiveInterval().contains(endDate) ? null : "End date is not in range";
    }

    // //////////////////////////////////////

    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Override
    @Programmatic
    public LocalDateInterval getEffectiveInterval() {
        return getInterval().overlap(this.getLease().getEffectiveInterval());
    }

    // //////////////////////////////////////

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(final LocalDate localDate) {
        return getInterval().contains(localDate);
    }

    // //////////////////////////////////////

    private UnitSize unitSize;

    @Disabled(reason = "Change using action")
    @javax.jdo.annotations.Column(name = "unitSizeId", allowsNull = "true")
    public UnitSize getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(final UnitSize unitSize) {
        this.unitSize = unitSize;
    }

    // //////////////////////////////////////

    private Sector sector;

    @Disabled(reason = "Change using action")
    @javax.jdo.annotations.Column(name = "sectorId", allowsNull = "true")
    public Sector getSector() {
        return sector;
    }

    public void setSector(final Sector sector) {
        this.sector = sector;
    }

    // //////////////////////////////////////

    private Activity activity;

    @Disabled(reason = "Change using action")
    @javax.jdo.annotations.Column(name = "activityId", allowsNull = "true")
    public Activity getActivity() {
        return activity;
    }

    public void setActivity(final Activity activity) {
        this.activity = activity;
    }

    // //////////////////////////////////////

    private Brand brand;

    @Disabled(reason = "Change using action")
    @javax.jdo.annotations.Column(name = "brandId", allowsNull = "true")
    public Brand getBrand() {
        return brand;
    }

    public void setBrand(final Brand brand) {
        this.brand = brand;
    }

    // //////////////////////////////////////

    @DescribedAs("Update unit size, sector, activity and/or brand")
    public Occupancy updateClassification(
            final @Named("Unit size") @Optional UnitSize unitSize,
            final @Named("Sector") @Optional Sector sector,
            final @Named("Activity") @Optional Activity activity,
            final @Named("Brand") @Optional Brand brand) {
        setUnitSize(unitSize);
        setSector(sector);
        setActivity(activity);
        setBrand(brand);
        return this;
    }

    public List<Activity> choices2UpdateClassification(
            final UnitSize unitSize,
            final Sector sector) {
        return activities.findBySector(sector);
    }

    // //////////////////////////////////////

    @Programmatic
    public Occupancy setBrandName(final String name) {
        setBrand(brands.findOrCreate(name));
        return this;
    }

    @Programmatic
    public Occupancy setUnitSizeName(final String name) {
        setUnitSize(unitSizes.findOrCreate(name));
        return this;
    }

    @Programmatic
    public Occupancy setSectorName(final String name) {
        setSector(sectors.findOrCreate(name));
        return this;
    }

    @Programmatic
    public Occupancy setActivityName(final String name) {
        setActivity(activities.findOrCreate(getSector(), name));
        return this;
    }

    // //////////////////////////////////////

    private OccupancyReportingType reportTurnover;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.OCCUPANCY_REPORTING_TYPE_ENUM)
    @Disabled(reason = "Change using action")
    @Hidden(where = Where.PARENTED_TABLES)
    public OccupancyReportingType getReportTurnover() {
        return reportTurnover;
    }

    public void setReportTurnover(final OccupancyReportingType reportTurnover) {
        this.reportTurnover = reportTurnover;
    }

    // //////////////////////////////////////

    private OccupancyReportingType reportRent;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.OCCUPANCY_REPORTING_TYPE_ENUM)
    @Disabled(reason = "Change using action")
    @Hidden(where = Where.PARENTED_TABLES)
    public OccupancyReportingType getReportRent() {
        return reportRent;
    }

    public void setReportRent(final OccupancyReportingType reportRent) {
        this.reportRent = reportRent;
    }

    // //////////////////////////////////////

    private OccupancyReportingType reportOCR;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.OCCUPANCY_REPORTING_TYPE_ENUM)
    @Disabled(reason = "Change using action")
    @Hidden(where = Where.PARENTED_TABLES)
    public OccupancyReportingType getReportOCR() {
        return reportOCR;
    }

    public void setReportOCR(final OccupancyReportingType reportOCR) {
        this.reportOCR = reportOCR;
    }

    // //////////////////////////////////////

    public Occupancy updateReportingOptions(
            final @Named("Report Turnover") OccupancyReportingType reportTurnover,
            final @Named("Report Rent") OccupancyReportingType reportRent,
            final @Named("Report OCR") OccupancyReportingType reportOCR) {
        setReportTurnover(reportTurnover);
        setReportRent(reportRent);
        setReportOCR(reportOCR);
        return this;
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

    // //////////////////////////////////////

    public enum OccupancyReportingType {
        NO("Don't report"),
        YES("Report"),
        SEPARATE("Report but exclude from main calculations");

        private final String title;

        private OccupancyReportingType(final String title) {
            this.title = title;
        }

        public String title() {
            return title;
        }

    }

}
