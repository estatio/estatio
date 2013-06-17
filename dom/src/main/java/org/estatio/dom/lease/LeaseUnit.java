package org.estatio.dom.lease;

import java.util.List;

import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Ordering;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithInterval;
import org.estatio.dom.tag.Tag;
import org.estatio.dom.tag.Tags;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Index(name = "LEASE_UNIT_IDX", members = { "lease", "unit", "startDate" })
@javax.jdo.annotations.Unique(name = "LEASE_UNIT_IDX2", members = { "lease", "unit", "startDate" })
@javax.jdo.annotations.Queries({ 
    @javax.jdo.annotations.Query(
            name = "leaseUnit_find", language = "JDOQL", 
            value = "SELECT " +
            		"FROM org.estatio.dom.lease.LeaseUnit " +
            		"WHERE lease == :lease " +
            		"&& unit == :unit " +
            		"&& startDate == :startDate") })
public class LeaseUnit extends EstatioTransactionalObject implements Comparable<LeaseUnit>, WithInterval {

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

    // //////////////////////////////////////

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

    // //////////////////////////////////////

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

    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    // //////////////////////////////////////

    private Tag brandTag;

    @Hidden
    public Tag getBrandTag() {
        return brandTag;
    }

    public void setBrandTag(final Tag brandTag) {
        this.brandTag = brandTag;
    }

    @MemberOrder(sequence = "6")
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

    @MemberOrder(name = "Brand", sequence = "6.1")
    public LeaseUnit newBrand(@Named("Tag") @Optional final String brand) {
        setBrand(brand);
        return this;
    }

    public String default0NewBrand() {
        return getBrand();
    }

    // //////////////////////////////////////

    private Tag sectorTag;

    @Hidden
    public Tag getSectorTag() {
        return sectorTag;
    }

    public void setSectorTag(final Tag sectorTag) {
        this.sectorTag = sectorTag;
    }

    @MemberOrder(sequence = "7")
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

    @MemberOrder(name = "Sector", sequence = "7.1")
    public LeaseUnit newSector(@Named("Tag") @Optional final String sector) {
        setSector(sector);
        return this;
    }

    public String default0NewSector() {
        return getSector();
    }

    // //////////////////////////////////////

    private Tag activityTag;

    @Hidden
    public Tag getActivityTag() {
        return activityTag;
    }

    public void setActivityTag(final Tag activityTag) {
        this.activityTag = activityTag;
    }

    @MemberOrder(sequence = "8")
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

    @MemberOrder(name = "Activity", sequence = "8.1")
    public LeaseUnit newActivity(@Named("Tag") @Optional final String activity) {
        setActivity(activity);
        return this;
    }

    public String default0NewActivity() {
        return getActivity();
    }

    // //////////////////////////////////////

    @Override
    @Hidden
    public int compareTo(LeaseUnit other) {
        return ORDERING_BY_LEASE.compound(ORDERING_BY_UNIT).compound(ORDERING_BY_START_DATE_DESC).compare(this, other);
    }

    public final static Ordering<LeaseUnit> ORDERING_BY_LEASE = new Ordering<LeaseUnit>() {
        public int compare(LeaseUnit p, LeaseUnit q) {
            return Ordering.natural().nullsFirst().compare(p.getLease(), q.getLease());
        }
    };
    public final static Ordering<LeaseUnit> ORDERING_BY_UNIT = new Ordering<LeaseUnit>() {
        public int compare(LeaseUnit p, LeaseUnit q) {
            return Ordering.natural().nullsFirst().compare(p.getUnit(), q.getUnit());
        }
    };
    public final static Ordering<LeaseUnit> ORDERING_BY_START_DATE_DESC = new Ordering<LeaseUnit>() {
        public int compare(LeaseUnit p, LeaseUnit q) {
            return Ordering.natural().nullsLast().reverse().compare(p.getStartDate(), q.getStartDate());
        }
    };

    // //////////////////////////////////////

    private Tags tags;

    public void injectTags(final Tags tags) {
        this.tags = tags;
    }

}
