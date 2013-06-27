package org.estatio.dom.asset;

import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberGroups;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithInterval;
import org.estatio.dom.party.Party;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Query(
        name = "findRoleByAssetAndPartyAndType", language = "JDOQL", 
        value = "SELECT " +
        		"FROM org.estatio.dom.asset.FixedAssetRole " +
        		"WHERE asset == :asset " +
        		"&& party == :party " +
        		"&& type == :type")
@Bookmarkable(BookmarkPolicy.AS_CHILD)
@MemberGroups({"General", "Dates", "Related"})
public class FixedAssetRole extends EstatioTransactionalObject<FixedAssetRole> implements WithInterval<FixedAssetRole> {

    public FixedAssetRole() {
        super("asset, party, startDate desc, type");
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name="ASSET_ID")
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

    @javax.jdo.annotations.Column(name="PARTY_ID")
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

    @MemberOrder(name="Dates", sequence = "4")
    @Optional
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @MemberOrder(name="Dates", sequence = "5")
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

    @MemberOrder(name="Related", sequence = "9.1")
    @Named("Previous Role")
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @Optional
    @Override
    public FixedAssetRole getPrevious() {
        return getStartDate() != null
                ?fixedAssetRoles.findByAssetAndPartyAndTypeAndEndDate(getAsset(), getParty(), getType(), getStartDate().minusDays(1))
                :null;
    }

    @MemberOrder(name="Related", sequence = "9.1")
    @Named("Next Role")
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @Optional
    @Override
    public FixedAssetRole getNext() {
        return getEndDate() != null
                ?fixedAssetRoles.findByAssetAndPartyAndTypeAndStartDate(getAsset(), getParty(), getType(), getEndDate().plusDays(1))
                :null;
    }

    // //////////////////////////////////////

    private FixedAssetRoles fixedAssetRoles;
    public void injectFixedAssetRoles(FixedAssetRoles fixedAssetRoles) {
        this.fixedAssetRoles = fixedAssetRoles;
    }
    
}
