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
package org.estatio.module.lease.dom.occupancy;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.eventbus.EventBusService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.base.dom.with.WithIntervalMutable;
import org.incode.module.country.dom.impl.Country;

import org.estatio.module.asset.dom.Unit;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.occupancy.salesarea.SalesAreaLicense;
import org.estatio.module.lease.dom.occupancy.salesarea.SalesAreaLicenseRepository;
import org.estatio.module.lease.dom.occupancy.tags.Activity;
import org.estatio.module.lease.dom.occupancy.tags.ActivityRepository;
import org.estatio.module.lease.dom.occupancy.tags.Brand;
import org.estatio.module.lease.dom.occupancy.tags.BrandCoverage;
import org.estatio.module.lease.dom.occupancy.tags.BrandRepository;
import org.estatio.module.lease.dom.occupancy.tags.Sector;
import org.estatio.module.lease.dom.occupancy.tags.SectorRepository;
import org.estatio.module.lease.dom.occupancy.tags.UnitSize;
import org.estatio.module.lease.dom.occupancy.tags.UnitSizeRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        , schema = "dbo"    // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
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
                        + "FROM org.estatio.module.lease.dom.occupancy.Occupancy "
                        + "WHERE unit == :unit "
                        + "ORDER BY startDate "),
        @javax.jdo.annotations.Query(
                name = "findByProperty", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.occupancy.Occupancy "
                        + "WHERE unit.property == :property "
                        + "ORDER BY startDate "),
        @javax.jdo.annotations.Query(
                name = "findByLease", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.occupancy.Occupancy "
                        + "WHERE lease == :lease "
                        + "ORDER BY startDate "),
        @javax.jdo.annotations.Query(
                name = "findByLeaseAndDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.occupancy.Occupancy "
                        + "WHERE lease == :lease "
                        + " && (startDate == null || startDate <= :date) "
                        + " && (endDate == null || endDate >= :dateAsEndDate) "
                        + "ORDER BY startDate "),
        @javax.jdo.annotations.Query(
                name = "findByLeaseAndUnitAndStartDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.occupancy.Occupancy "
                        + "WHERE lease == :lease "
                        + "&& unit == :unit "
                        + "&& startDate == :startDate"),
        @javax.jdo.annotations.Query(
                name = "findByBrand", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.occupancy.Occupancy "
                        + "WHERE brand == :brand "
                        + "&& (:includeTerminated || endDate == null || endDate >= :date)"),
        @javax.jdo.annotations.Query(
                name = "findBySector", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.occupancy.Occupancy "
                        + "WHERE sector == :sector"),
        @javax.jdo.annotations.Query(
                name = "findByActivity", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.occupancy.Occupancy "
                        + "WHERE activity == :activity")
})
@DomainObject(
        objectType = "org.estatio.dom.lease.Occupancy"
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
public class Occupancy
        extends UdoDomainObject2<Occupancy>
        implements WithIntervalMutable<Occupancy>, WithApplicationTenancyProperty {

    public Occupancy() {
        super("lease, startDate desc nullsLast, unit");
    }

    public static class RemoveEvent extends ActionDomainEvent<Occupancy> {}

    public String title() {
        return TitleBuilder.start()
                .withName(getBrand())
                .withName(getLease())
                .withName(getUnit())
                .withName(getStartDate())
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


    @javax.jdo.annotations.Column(name = "leaseId", allowsNull = "false")
    @Property(hidden = Where.REFERENCES_PARENT, editing = Editing.DISABLED)
    @Getter @Setter
    private Lease lease;


    @javax.jdo.annotations.Column(name = "unitId", allowsNull = "false")
    @Property(hidden = Where.REFERENCES_PARENT, editing = Editing.DISABLED)
    @Getter @Setter
    private Unit unit;


    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action", optionality = Optionality.OPTIONAL)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate startDate;


    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action", optionality = Optionality.OPTIONAL)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate endDate;

    private WithIntervalMutable.Helper<Occupancy> changeDates = new WithIntervalMutable.Helper<>(this);

    WithIntervalMutable.Helper<Occupancy> getChangeDates() {
        return changeDates;
    }

    @Override
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Occupancy changeDates(
            @Nullable final LocalDate startDate,
            @Nullable final LocalDate endDate) {
        return getChangeDates().changeDates(startDate, endDate);
    }
    public String disableChangeDates() {
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
    public String validateChangeDates(final LocalDate startDate, final LocalDate endDate) {
        return getChangeDates().validateChangeDates(startDate, endDate);
    }



    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Occupancy terminate(final LocalDate endDate) {
        setEndDate(endDate);
        return this;
    }
    public String validateTerminate(
            final LocalDate endDate) {
        return getEffectiveInterval().contains(endDate) ? null : "End date is not in range";
    }



    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE, domainEvent = Occupancy.RemoveEvent.class)
    public Object remove() {
        Lease lease = getLease();
        final SalesAreaLicense currentSalesAreaLicense = getCurrentSalesAreaLicense();
        if (currentSalesAreaLicense !=null){
            currentSalesAreaLicense.remove();
        }
        remove(this);
        return lease;
    }
    public String disableRemove() {
        return !EstatioRole.SUPERUSER.isApplicableFor(getUser()) ? "You need Superuser rights to remove an occupancy" : null;
    }

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

    @Action(semantics = SemanticsOf.SAFE)
    public LocalDate getEffectiveStartDate() {
        return getStartDate()==null ? getEffectiveInterval().startDate() : getStartDate();
    }

    @Action(semantics = SemanticsOf.SAFE)
    public LocalDate getEffectiveEndDate(){
        return getEndDate()==null ? getEffectiveInterval().endDate() : getEndDate();
    }

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(final LocalDate localDate) {
        return getInterval().contains(localDate);
    }



    @javax.jdo.annotations.Column(name = "unitSizeId", allowsNull = "true")
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action")
    @Getter @Setter
    private UnitSize unitSize;


    @javax.jdo.annotations.Column(name = "sectorId", allowsNull = "true")
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action")
    @Getter @Setter
    private Sector sector;


    @javax.jdo.annotations.Column(name = "activityId", allowsNull = "true")
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action")
    @Getter @Setter
    private Activity activity;


    @javax.jdo.annotations.Column(name = "brandId", allowsNull = "true")
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action")
    @Getter @Setter
    private Brand brand;


    @ActionLayout(describedAs = "Change unit size, sector, activity and/or brand")
    public Occupancy changeClassification(
            @Nullable final UnitSize unitSize,
            @Nullable final Sector sector,
            @Nullable final Activity activity,
            @Nullable final Brand brand) {
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
    public List<Activity> choices2ChangeClassification(final UnitSize unitSize, final Sector sector) {
        return activityRepository.findBySector(sector);
    }

    public String validateChangeClassification(final UnitSize unitSize, final Sector sector, final Activity activity, final Brand brand){
        if (activity!=null && !activityRepository.findBySector(sector).contains(activity)) return "Activity not found for sector";
        return null;
    }

    @Programmatic
    public Occupancy setBrandName(
            final String name,
            @Nullable final BrandCoverage brandCoverage,
            @Nullable final Country countryOfOrigin) {
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

    @javax.jdo.annotations.Column(allowsNull = "false", length = OccupancyReportingType.Meta.MAX_LEN)
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action", hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private OccupancyReportingType reportTurnover;


    @javax.jdo.annotations.Column(allowsNull = "false", length = OccupancyReportingType.Meta.MAX_LEN)
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action", hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private OccupancyReportingType reportRent;


    @javax.jdo.annotations.Column(allowsNull = "false", length = OccupancyReportingType.Meta.MAX_LEN)
    @Property(editing = Editing.DISABLED, editingDisabledReason = "Change using action", hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private OccupancyReportingType reportOCR;

    public static class OccupancyChangeReportingOptionsEvent
            extends ActionDomainEvent<Occupancy> {}

    public Occupancy changeReportingOptions(
            final OccupancyReportingType reportTurnover,
            final OccupancyReportingType reportRent,
            final OccupancyReportingType reportOCR) {
        setReportTurnover(reportTurnover);
        setReportRent(reportRent);
        setReportOCR(reportOCR);

        // fire event
        final Occupancy.OccupancyChangeReportingOptionsEvent event = new Occupancy.OccupancyChangeReportingOptionsEvent();
        event.setEventPhase(AbstractDomainEvent.Phase.EXECUTED);
        event.setSource(this);
        eventBusService.post(event);

        return this;
    }

    @RequiredArgsConstructor
    public enum OccupancyReportingType {

        NO("Don't report"),
        YES("Report"),
        SEPARATE("Report but exclude from main calculations");

        private final String title;
        public String title() {
            return title;
        }

        @UtilityClass
        public static class Meta {
            public final static int MAX_LEN = 30;
        }
    }


    public Occupancy verify() {
        Lease lease = getLease();

        if (ObjectUtils.compare(lease.getTenancyStartDate(), getStartDate()) != 0) {
            setStartDate(lease.getTenancyStartDate());
        }

        if (getEndDate() != null && (ObjectUtils.compare(lease.getTenancyEndDate(), getEndDate()) != 0)) {
            setEndDate(lease.getTenancyEndDate());
        }

        return this;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public SalesAreaLicense getCurrentSalesAreaLicense(){
        return salesAreaLicenseRepository.findMostRecentForOccupancy(this);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public BigDecimal getSalesAreaNonFood(){
        return getCurrentSalesAreaLicense() !=null ? getCurrentSalesAreaLicense().getSalesAreaNonFood() : null;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public BigDecimal getSalesAreaFood(){
        return getCurrentSalesAreaLicense() !=null ? getCurrentSalesAreaLicense().getSalesAreaFood() : null;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public BigDecimal getFoodAndBeveragesArea(){
        return getCurrentSalesAreaLicense() !=null ? getCurrentSalesAreaLicense().getFoodAndBeveragesArea() : null;
    }

    @Action
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Occupancy createSalesAreaLicense(
            @Nullable
            final BigDecimal salesAreaNonFood,
            @Nullable
            final BigDecimal salesAreaFood,
            @Nullable
            final BigDecimal foodAndBeveragesArea,
            final LocalDate startDate
    ){
        final SalesAreaLicense salesAreaLicense = salesAreaLicenseRepository.newSalesAreaLicense(
                this,
                getLease().getReference(),
                getLease().getReference().concat("-SAL"),
                startDate,
                lease.getSecondaryParty(),
                lease.getPrimaryParty(),
                salesAreaNonFood,
                salesAreaFood,
                foodAndBeveragesArea);
        return this;
    }

    public LocalDate default3CreateSalesAreaLicense(){
        return getEffectiveStartDate();
    }

    public boolean hideCreateSalesAreaLicense(){
        return getCurrentSalesAreaLicense()!=null ? true : false;
    }

    public String validateCreateSalesAreaLicense(
            @Nullable
            final BigDecimal salesAreaNonFood,
            @Nullable
            final BigDecimal salesAreaFood,
            @Nullable
            final BigDecimal foodAndBeveragesArea,
            @Nullable
            final LocalDate startDate){
        return SalesAreaLicense.validate(this, null, startDate, salesAreaFood, salesAreaNonFood, foodAndBeveragesArea);
    }

    @Action
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Occupancy createNextSalesAreaLicense(
            LocalDate startDate,
            @Nullable
            final BigDecimal salesAreaNonFood,
            @Nullable
            final BigDecimal salesAreaFood,
            @Nullable
            final BigDecimal foodAndBeveragesArea){
        getCurrentSalesAreaLicense().createNext(startDate, salesAreaNonFood, salesAreaFood, foodAndBeveragesArea);
        return this;
    }

    public boolean hideCreateNextSalesAreaLicense(){
        return getCurrentSalesAreaLicense()==null ? true : false;
    }

    public String validateCreateNextSalesAreaLicense(
            LocalDate startDate,
            @Nullable
            final BigDecimal salesAreaNonFood,
            @Nullable
            final BigDecimal salesAreaFood,
            @Nullable
            final BigDecimal foodAndBeveragesArea){
        return getCurrentSalesAreaLicense().validateCreateNext(startDate, salesAreaNonFood, salesAreaFood, foodAndBeveragesArea);
    }

    public LocalDate default0CreateNextSalesAreaLicense(){
        return getCurrentSalesAreaLicense().default0CreateNext();
    }

    @Inject
    BrandRepository brandRepository;

    @Inject
    SectorRepository sectorRepository;

    @Inject
    ActivityRepository activityRepository;

    @Inject
    UnitSizeRepository unitSizeRepository;

    @Inject
    SalesAreaLicenseRepository salesAreaLicenseRepository;

    @Inject EventBusService eventBusService;
}
