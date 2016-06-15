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

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.app.security.EstatioRole;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.geography.Country;
import org.estatio.dom.lease.tags.ActivityRepository;
import org.estatio.dom.lease.tags.Activity;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.lease.tags.BrandCoverage;
import org.estatio.dom.lease.tags.BrandRepository;
import org.estatio.dom.lease.tags.Sector;
import org.estatio.dom.lease.tags.SectorRepository;
import org.estatio.dom.lease.tags.UnitSize;
import org.estatio.dom.lease.tags.UnitSizeRepository;
import org.estatio.dom.utils.TitleBuilder;
import org.estatio.dom.valuetypes.LocalDateInterval;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Unique(
        name = "Occupancy_lease_unit_startDate_UNQ",
        members = {"lease", "unit", "startDate"})
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
        implements WithIntervalMutable<Occupancy>, WithApplicationTenancyProperty {

    public Occupancy() {
        super("lease, startDate desc nullsLast, unit");
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getStartDate())
                .withTupleElement(getLease())
                .withTupleElement(getUnit())
                .toString();
    }

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        // could equally have derived from the lease;
        // they should always be in sync.
        return getUnit().getApplicationTenancy();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "leaseId", allowsNull = "false")
    @Property(hidden = Where.REFERENCES_PARENT, editing = Editing.DISABLED)
    @Getter @Setter
    private Lease lease;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "unitId", allowsNull = "false")
    @Property(hidden = Where.REFERENCES_PARENT, editing = Editing.DISABLED)
    @Getter @Setter
    private Unit unit;

    // //////////////////////////////////////

    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action", optionality = Optionality.OPTIONAL)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate startDate;

    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action", optionality = Optionality.OPTIONAL)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate endDate;

    // //////////////////////////////////////

    private WithIntervalMutable.Helper<Occupancy> changeDates = new WithIntervalMutable.Helper<>(this);

    WithIntervalMutable.Helper<Occupancy> getChangeDates() {
        return changeDates;
    }

    @Override
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Occupancy changeDates(
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
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
            final LocalDate endDate) {
        setEndDate(endDate);
        return this;
    }

    public String validateTerminate(
            final LocalDate endDate) {
        return getEffectiveInterval().contains(endDate) ? null : "End date is not in range";
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Object remove() {
        Lease lease = getLease();
        getContainer().remove(this);
        return lease;
    }

    public String disableRemove() {
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

    @javax.jdo.annotations.Column(name = "unitSizeId", allowsNull = "true")
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action")
    @Getter @Setter
    private UnitSize unitSize;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "sectorId", allowsNull = "true")
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action")
    @Getter @Setter
    private Sector sector;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "activityId", allowsNull = "true")
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action")
    @Getter @Setter
    private Activity activity;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "brandId", allowsNull = "true")
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action")
    @Getter @Setter
    private Brand brand;

    // //////////////////////////////////////

    @ActionLayout(describedAs = "Change unit size, sector, activity and/or brand")
    public Occupancy changeClassification(
            final @Parameter(optionality = Optionality.OPTIONAL) UnitSize unitSize,
            final @Parameter(optionality = Optionality.OPTIONAL) Sector sector,
            final @Parameter(optionality = Optionality.OPTIONAL) Activity activity,
            final @Parameter(optionality = Optionality.OPTIONAL) Brand brand) {
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
        return activityRepository.findBySector(sector);
    }

    // //////////////////////////////////////

    @Programmatic
    public Occupancy setBrandName(
            final String name,
            @Parameter(optionality = Optionality.OPTIONAL) final BrandCoverage brandCoverage,
            @Parameter(optionality = Optionality.OPTIONAL) final Country countryOfOrigin) {
        setBrand(brandRepository.findOrCreate(getApplicationTenancy(), name, brandCoverage, countryOfOrigin));
        return this;
    }

    @Programmatic
    public Occupancy setUnitSizeName(final String name) {
        setUnitSize(unitSizeRepository.findOrCreate(name));
        return this;
    }

    @Programmatic
    public Occupancy setSectorName(final String name) {
        setSector(sectorRepository.findOrCreate(name));
        return this;
    }

    @Programmatic
    public Occupancy setActivityName(final String name) {
        setActivity(activityRepository.findOrCreate(getSector(), name));
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
            final OccupancyReportingType reportTurnover,
            final OccupancyReportingType reportRent,
            final OccupancyReportingType reportOCR) {
        setReportTurnover(reportTurnover);
        setReportRent(reportRent);
        setReportOCR(reportOCR);
        return this;
    }

    // //////////////////////////////////////

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
        Lease lease = getLease();

        if (ObjectUtils.compare(lease.getTenancyStartDate(), getStartDate()) != 0) {
            setStartDate(lease.getTenancyStartDate());
        }

        if (getEndDate() != null && (ObjectUtils.compare(lease.getTenancyEndDate(), getEndDate()) != 0)) {
            setEndDate(lease.getTenancyEndDate());
        }
    }

    @Inject
    private BrandRepository brandRepository;

    @Inject
    private SectorRepository sectorRepository;

    @Inject
    private ActivityRepository activityRepository;

    @Inject
    private UnitSizeRepository unitSizeRepository;
}
