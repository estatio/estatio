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
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;

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
                        + "&& (:includeTerminated || endDate == null || endDate >= :date)")
})
@DomainObject(
        objectType = "org.estatio.dom.lease.Occupancy"
)
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



    public Occupancy changeReportingOptions(
            final OccupancyReportingType reportTurnover,
            final OccupancyReportingType reportRent,
            final OccupancyReportingType reportOCR) {
        setReportTurnover(reportTurnover);
        setReportRent(reportRent);
        setReportOCR(reportOCR);
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
    BrandRepository brandRepository;

    @Inject
    SectorRepository sectorRepository;

    @Inject
    ActivityRepository activityRepository;

    @Inject
    UnitSizeRepository unitSizeRepository;
}
