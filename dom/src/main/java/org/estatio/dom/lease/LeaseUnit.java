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
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.Status;
import org.estatio.dom.WithInterval;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.tag.Tag;
import org.estatio.dom.tag.Tags;
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
public class LeaseUnit extends EstatioTransactionalObject<LeaseUnit, Status> implements WithIntervalMutable<LeaseUnit> {

    public LeaseUnit() {
        super("lease, startDate desc nullsLast, unit", Status.UNLOCKED, Status.LOCKED);
    }

    // //////////////////////////////////////

    private Status status;

    @Disabled
    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(final Status status) {
        this.status = status;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "LEASE_ID")
    private Lease lease;

    @Title(sequence = "1", append = ":")
    @Hidden(where = Where.REFERENCES_PARENT)
    public Lease getLease() {
        return lease;
    }

    public void setLease(final Lease lease) {
        this.lease = lease;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "UNIT_ID")
    private UnitForLease unit;

    @Title(sequence = "2", append = ":")
    @Hidden(where = Where.REFERENCES_PARENT)
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
    @Disabled
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

    @javax.jdo.annotations.Column(name = "SIZETAG_ID")
    private Tag sizeTag;

    @Hidden
    public Tag getSizeTag() {
        return sizeTag;
    }

    public void setSizeTag(final Tag sizeTag) {
        this.sizeTag = sizeTag;
    }

    @Optional
    public String getSize() {
        final Tag existingTag = getSizeTag();
        return existingTag != null ? existingTag.getValue() : null;
    }

    public void setSize(final String size) {
        final Tag existingTag = getSizeTag();
        Tag tag = tags.tagFor(existingTag, this, "size", size);
        setSizeTag(tag);
    }

    public List<String> choicesSize() {
        return tags.choices(this, "size");
    }

    public LeaseUnit newSize(@Named("Tag") @Optional final String size) {
        setSize(size);
        return this;
    }

    public String default0NewSize() {
        return getSize();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "BRANDTAG_ID")
    private Tag brandTag;

    @Hidden
    public Tag getBrandTag() {
        return brandTag;
    }

    public void setBrandTag(final Tag brandTag) {
        this.brandTag = brandTag;
    }

    @Optional
    public String getBrand() {
        final Tag existingTag = getBrandTag();
        return existingTag != null ? existingTag.getValue() : null;
    }

    public void setBrand(final String brand) {
        final Tag existingTag = getBrandTag();
        Tag tag = tags.tagFor(existingTag, this, "brand", brand);
        setBrandTag(tag);
    }

    public List<String> choicesBrand() {
        return tags.choices(this, "brand");
    }

    public LeaseUnit newBrand(@Named("Tag") @Optional final String brand) {
        setBrand(brand);
        return this;
    }

    public String default0NewBrand() {
        return getBrand();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "SECTORTAG_ID")
    private Tag sectorTag;

    @Hidden
    public Tag getSectorTag() {
        return sectorTag;
    }

    public void setSectorTag(final Tag sectorTag) {
        this.sectorTag = sectorTag;
    }

    @Optional
    public String getSector() {
        final Tag existingTag = getSectorTag();
        return existingTag != null ? existingTag.getValue() : null;
    }

    public void setSector(final String sector) {
        final Tag existingTag = getSectorTag();
        Tag tag = tags.tagFor(existingTag, this, "sector", sector);
        setSectorTag(tag);
    }

    public List<String> choicesSector() {
        return tags.choices(this, "sector");
    }

    public LeaseUnit newSector(@Named("Tag") @Optional final String sector) {
        setSector(sector);
        return this;
    }

    public String default0NewSector() {
        return getSector();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "ACTIVITYTAG_ID")
    private Tag activityTag;

    @Hidden
    public Tag getActivityTag() {
        return activityTag;
    }

    public void setActivityTag(final Tag activityTag) {
        this.activityTag = activityTag;
    }

    @Optional
    public String getActivity() {
        final Tag existingTag = getActivityTag();
        return existingTag != null ? existingTag.getValue() : null;
    }

    public void setActivity(final String activity) {
        final Tag existingTag = getActivityTag();
        Tag tag = tags.tagFor(existingTag, this, "activity", activity);
        setActivityTag(tag);
    }

    public List<String> choicesActivity() {
        return tags.choices(this, "activity");
    }

    public LeaseUnit newActivity(@Named("Tag") @Optional final String activity) {
        setActivity(activity);
        return this;
    }

    public String default0NewActivity() {
        return getActivity();
    }

    // //////////////////////////////////////

    private Tags tags;

    public final void injectTags(final Tags tags) {
        this.tags = tags;
    }

}
