/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.app.security.EstatioRole;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.asset.Unit;
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
                name = "findByUnit", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.Occupancy "
                        + "WHERE unit == :unit "
                        + "ORDER BY startDate "),
        @javax.jdo.annotations.Query(
                name = "findByLease", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.Occupancy "
                        + "WHERE lease == :lease "
                        + "ORDER BY startDate "),
        @javax.jdo.annotations.Query(
                name = "findByLeaseAndDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.Occupancy "
                        + "WHERE lease == :lease "
                        + " && (startDate == null || startDate <= :date) "
                        + " && (endDate == null || endDate >= :dateAsEndDate) "
                        + "ORDER BY startDate "),
        @javax.jdo.annotations.Query(
                name = "findByLeaseAndUnitAndStartDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.Occupancy "
                        + "WHERE lease == :lease "
                        + "&& unit == :unit "
                        + "&& startDate == :startDate"),
        @javax.jdo.annotations.Query(
                name = "findByBrand", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.Occupancy "
                        + "WHERE brand == :brand "
                        + "&& (:includeTerminated || endDate == null || endDate >= :date)")
})
public class Occupancy
        extends EstatioDomainObject<Occupancy>
        implements WithIntervalMutable<Occupancy>, Taggable {

    public Occupancy() {
        super("lease, startDate desc nullsLast, unit");
    }

    // //////////////////////////////////////

    private Lease lease;

    @javax.jdo.annotations.Column(name = "leaseId", allowsNull = "false")
    @Title(sequence = "1", append = ":")
    @Property(hidden = Where.REFERENCES_PARENT, editing = Editing.DISABLED)
    public Lease getLease() {
        return lease;
    }

    public void setLease(final Lease lease) {
        this.lease = lease;
    }

    // //////////////////////////////////////

    private Unit unit;

    @javax.jdo.annotations.Column(name = "unitId", allowsNull = "false")
    @Title(sequence = "2", append = ":")
    @Property(hidden = Where.REFERENCES_PARENT, editing = Editing.DISABLED)
    public Unit getUnit() {
        return unit;
    }

    public void setUnit(final Unit unit) {
        this.unit = unit;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate startDate;

    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action", optional = Optionality.TRUE)
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

    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action", optional = Optionality.TRUE)
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

    @Override
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Occupancy changeDates(
            final @Parameter(optional = Optionality.TRUE) @ParameterLayout(named = "Start Date") LocalDate startDate,
            final @Parameter(optional = Optionality.TRUE) @ParameterLayout(named = "End Date") LocalDate endDate) {
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

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Occupancy terminate(
            final @ParameterLayout(named = "End Date") LocalDate endDate) {
        setEndDate(endDate);
        return this;
    }

    public String validateTerminate(
            final LocalDate endDate) {
        return getEffectiveInterval().contains(endDate) ? null : "End date is not in range";
    }

    // //////////////////////////////////////

    public Object remove(
            final @ParameterLayout(named = "Are you sure?") boolean confirm) {
        if (confirm) {
            Lease lease = getLease();
            getContainer().remove(this);
            return lease;
        } else {
            return this;
        }
    }

    public String disableRemove(boolean confirm) {
        return !EstatioRole.ADMINISTRATOR.isApplicableFor(getUser()) ? "You need administrator rights to remove an occupancy" : null;
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

    @javax.jdo.annotations.Column(name = "unitSizeId", allowsNull = "true")
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action")
    public UnitSize getUnitSize() {
        return unitSize;
    }

    public void setUnitSize(final UnitSize unitSize) {
        this.unitSize = unitSize;
    }

    // //////////////////////////////////////

    private Sector sector;

    @javax.jdo.annotations.Column(name = "sectorId", allowsNull = "true")
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action")
    public Sector getSector() {
        return sector;
    }

    public void setSector(final Sector sector) {
        this.sector = sector;
    }

    // //////////////////////////////////////

    private Activity activity;

    @javax.jdo.annotations.Column(name = "activityId", allowsNull = "true")
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action")
    public Activity getActivity() {
        return activity;
    }

    public void setActivity(final Activity activity) {
        this.activity = activity;
    }

    // //////////////////////////////////////

    private Brand brand;

    @javax.jdo.annotations.Column(name = "brandId", allowsNull = "true")
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action")
    public Brand getBrand() {
        return brand;
    }

    public void setBrand(final Brand brand) {
        this.brand = brand;
    }

    // //////////////////////////////////////

    @ActionLayout(describedAs = "Change unit size, sector, activity and/or brand")
    public Occupancy changeClassification(
            final @Parameter(optional = Optionality.TRUE) @ParameterLayout(named = "Unit size") UnitSize unitSize,
            final @Parameter(optional = Optionality.TRUE) @ParameterLayout(named = "Sector") Sector sector,
            final @Parameter(optional = Optionality.TRUE) @ParameterLayout(named = "Activity") Activity activity,
            final @Parameter(optional = Optionality.TRUE) @ParameterLayout(named = "Brand") Brand brand) {
        setUnitSize(unitSize);
        setSector(sector);
        setActivity(activity);
        setBrand(brand);
        return this;
    }

    public UnitSize default0ChangeClassification() {
        return getUnitSize();
    }

    public Sector default1ChangeClassification() {
        return getSector();
    }

    public Activity default2ChangeClassification() {
        return getActivity();
    }

    public Brand default3ChangeClassification() {
        return getBrand();
    }

    public List<Activity> choices2ChangeClassification(
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
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action", hidden = Where.PARENTED_TABLES)
    public OccupancyReportingType getReportTurnover() {
        return reportTurnover;
    }

    public void setReportTurnover(final OccupancyReportingType reportTurnover) {
        this.reportTurnover = reportTurnover;
    }

    // //////////////////////////////////////

    private OccupancyReportingType reportRent;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.OCCUPANCY_REPORTING_TYPE_ENUM)
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action", hidden = Where.PARENTED_TABLES)
    public OccupancyReportingType getReportRent() {
        return reportRent;
    }

    public void setReportRent(final OccupancyReportingType reportRent) {
        this.reportRent = reportRent;
    }

    // //////////////////////////////////////

    private OccupancyReportingType reportOCR;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.OCCUPANCY_REPORTING_TYPE_ENUM)
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action", hidden = Where.PARENTED_TABLES)
    public OccupancyReportingType getReportOCR() {
        return reportOCR;
    }

    public void setReportOCR(final OccupancyReportingType reportOCR) {
        this.reportOCR = reportOCR;
    }

    // //////////////////////////////////////

    public Occupancy changeReportingOptions(
            final @ParameterLayout(named = "Report Turnover") OccupancyReportingType reportTurnover,
            final @ParameterLayout(named = "Report Rent") OccupancyReportingType reportRent,
            final @ParameterLayout(named = "Report OCR") OccupancyReportingType reportOCR) {
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

    public void verify() {
        Lease verifyLease = getLease();

        if (verifyLease.getTenancyEndDate() == null) {
            if (verifyLease.getEndDate() != null && !verifyLease.getEndDate().equals(getEndDate())) {
                setEndDate(verifyLease.getEndDate());
            } else if (verifyLease.getEndDate() == null) {
                setEndDate(null);
            }
        } else {
            if (!verifyLease.getTenancyEndDate().equals(getEndDate())) {
                setEndDate(verifyLease.getTenancyEndDate());
            }
        }
    }
}
