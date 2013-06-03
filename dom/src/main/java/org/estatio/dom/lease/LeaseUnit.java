package org.estatio.dom.lease;

import java.util.List;

import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Ordering;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Disabled;
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
import org.estatio.dom.utils.Orderings;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
public class LeaseUnit extends EstatioTransactionalObject implements Comparable<LeaseUnit>, WithInterval {

    
    // {{ lease, unit
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
    // }}

    
    
    // {{ StartDate, EndDate
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

    
    // }}


    // {{ tags
    private Tag brand;

    @Disabled
    @MemberOrder(sequence = "6")
    @Optional
    public Tag getBrand() {
        return brand;
    }

    public void setBrand(final Tag brand) {
        this.brand = brand;
    }
    @Named("Update")
    @MemberOrder(name="brand", sequence = "6.1")
    public LeaseUnit updateBrandTag(@Named("Tag") final String tagValue) {
        final Tag existingTag = getBrand();
        Tag tag = tags.tagFor(existingTag, this, "brand", tagValue);
        setBrand(tag);
        return this;
    }
    public String default0UpdateBrandTag() {
        final Tag existingTag = getBrand();
        return existingTag != null? existingTag.getValue(): null;
    }
    public List<String> choices0UpdateBrandTag() {
        return tags.choices(this, "brand");
    }
    @Named("New")
    @MemberOrder(name="brand", sequence = "6.2")
    public LeaseUnit newBrandTag(@Named("Tag") final String tagValue) {
        return updateBrandTag(tagValue);
    }
    public String default0NewBrandTag() {
        return default0UpdateBrandTag();
    }
    
    
    private Tag sector;

    @Disabled
    @MemberOrder(sequence = "7")
    @Optional
    public Tag getSector() {
        return sector;
    }

    public void setSector(final Tag sector) {
        this.sector = sector;
    }
    @Named("Update")
    @MemberOrder(name="sector", sequence = "7.1")
    public LeaseUnit updateSectorTag(@Named("Tag") final String tagValue) {
        final Tag existingTag = getSector();
        Tag tag = tags.tagFor(existingTag, this, "sector", tagValue);
        setSector(tag);
        return this;
    }
    public String default0UpdateSectorTag() {
        final Tag existingTag = getSector();
        return existingTag != null? existingTag.getValue(): null;
    }
    public List<String> choices0UpdateSectorTag() {
        return tags.choices(this, "sector");
    }
    @Named("New")
    @MemberOrder(name="sector", sequence = "7.2")
    public LeaseUnit newSectorTag(@Named("Tag") final String tagValue) {
        return updateSectorTag(tagValue);
    }
    public String default0NewSectorTag() {
        return default0UpdateSectorTag();
    }

    
    private Tag activity;

    @Disabled
    @MemberOrder(sequence = "8")
    @Optional
    public Tag getActivity() {
        return activity;
    }

    public void setActivity(final Tag activity) {
        this.activity = activity;
    }
    @Named("Update")
    @MemberOrder(name="activity", sequence = "7.1")
    public LeaseUnit updateActivityTag(@Named("Tag") final String tagValue) {
        final Tag existingTag = getSector();
        Tag tag = tags.tagFor(existingTag, this, "activity", tagValue);
        setActivity(tag);
        return this;
    }
    public String default0UpdateActivityTag() {
        final Tag existingTag = getActivity();
        return existingTag != null? existingTag.getValue(): null;
    }
    public List<String> choices0UpdateActivityTag() {
        return tags.choices(this, "activity");
    }
    @Named("New")
    @MemberOrder(name="activity", sequence = "8.2")
    public LeaseUnit newActivityTag(@Named("Tag") final String tagValue) {
        return updateActivityTag(tagValue);
    }
    public String default0NewActivityTag() {
        return default0UpdateActivityTag();
    }
    // }}

    
    // {{ Comparable impl
    @Override
    @Hidden
    public int compareTo(LeaseUnit other) {
        return ORDERING_BY_LEASE.compound(ORDERING_BY_UNIT).compound(ORDERING_BY_START_DATE).compare(this, other);
    }

    public static Ordering<LeaseUnit> ORDERING_BY_LEASE = new Ordering<LeaseUnit>() {
        public int compare(LeaseUnit p, LeaseUnit q) {
            return Ordering.natural().nullsFirst().compare(p.getLease(), q.getLease());
        }
    };
    public static Ordering<LeaseUnit> ORDERING_BY_UNIT = new Ordering<LeaseUnit>() {
        public int compare(LeaseUnit p, LeaseUnit q) {
            return Ordering.natural().nullsFirst().compare(p.getUnit(), q.getUnit());
        }
    };
    public static Ordering<LeaseUnit> ORDERING_BY_START_DATE = new Ordering<LeaseUnit>() {
        public int compare(LeaseUnit p, LeaseUnit q) {
            return Orderings.LOCAL_DATE_NATURAL_NULLS_FIRST.compare(p.getStartDate(), q.getStartDate());
        }
    };
    // }}
    
    
    // {{ injected: Tags
    private Tags tags;

    public void setTags(final Tags tags) {
        this.tags = tags;
    }
    // }}


}
