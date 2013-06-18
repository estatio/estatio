package org.estatio.dom.asset;

import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Objects;
import com.google.common.collect.Ordering;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.Comparisons;
import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithInterval;
import org.estatio.dom.party.Party;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Query(
        name = "fixedAssetRole_findByAssetParty", language = "JDOQL", 
        value = "SELECT " +
        		"FROM org.estatio.dom.asset.FixedAssetRole " +
        		"WHERE asset == :asset " +
        		"&& party == :party " +
        		"&& type == :type")
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public class FixedAssetRole extends EstatioTransactionalObject implements Comparable<FixedAssetRole>, WithInterval {

    private FixedAsset asset;

    @Title(sequence = "3", prepend = ":")
    @MemberOrder(sequence = "1")
    @Hidden(where = Where.REFERENCES_PARENT)
    @Disabled
    public FixedAsset getAsset() {
        return asset;
    }

    public void setAsset(final FixedAsset asset) {
        this.asset = asset;
    }

    // //////////////////////////////////////

    private Party party;

    @Title(sequence = "2", prepend = ":")
    @MemberOrder(sequence = "2")
    @Hidden(where = Where.REFERENCES_PARENT)
    @Disabled
    public Party getParty() {
        return party;
    }

    public void setParty(final Party party) {
        this.party = party;
    }

    // //////////////////////////////////////

    private FixedAssetRoleType type;

    @Disabled
    @MemberOrder(sequence = "3")
    @Title(sequence = "1")
    public FixedAssetRoleType getType() {
        return type;
    }

    public void setType(final FixedAssetRoleType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    private LocalDate startDate;

    @MemberOrder(sequence = "4")
    @Optional
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @MemberOrder(sequence = "5")
    @Optional
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

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("asset", getAsset() != null ? getAsset().getReference() : null).add("party", getParty() != null ? getParty().getName() : null).add("startDate", getStartDate()).add("type", getType()).toString();
    }

    // //////////////////////////////////////

    @Hidden
    @Override
    public int compareTo(FixedAssetRole other) {
        //return ORDERING_BY_ASSET.compound(ORDERING_BY_PARTY).compound(ORDERING_BY_START_DATE_DESC).compound(ORDERING_BY_TYPE).compare(this, other);
        return Comparisons.compare(this, other, "asset, party, startDate desc, type");
    }

//    public final static Ordering<FixedAssetRole> ORDERING_BY_ASSET = new Ordering<FixedAssetRole>() {
//        public int compare(FixedAssetRole p, FixedAssetRole q) {
//            return Ordering.natural().nullsFirst().compare(p.getAsset(), q.getAsset());
//        }
//    };
//    public final static Ordering<FixedAssetRole> ORDERING_BY_PARTY = new Ordering<FixedAssetRole>() {
//        public int compare(FixedAssetRole p, FixedAssetRole q) {
//            return Ordering.natural().nullsFirst().compare(p.getParty(), q.getParty());
//        }
//    };
//    public final static Ordering<FixedAssetRole> ORDERING_BY_TYPE = new Ordering<FixedAssetRole>() {
//        public int compare(FixedAssetRole p, FixedAssetRole q) {
//            return Ordering.natural().nullsFirst().compare(p.getType(), q.getType());
//        }
//    };

}
