package org.estatio.dom.agreement;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Lists;
import com.google.inject.name.Named;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithInterval;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.party.Party;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.estatio.services.clock.ClockService;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberGroups;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.filter.Filter;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
        name = "findByAgreementAndPartyAndTypeAndStartDate", language = "JDOQL", 
        value = "SELECT " +
        		"FROM org.estatio.dom.agreement.AgreementRole " +
        		"WHERE agreement == :agreement " +
        		"&& party == :party " +
        		"&& type == :type " +
        		"&& startDate == :startDate"),
	@javax.jdo.annotations.Query(
        name = "findByAgreementAndPartyAndTypeAndEndDate", language = "JDOQL", 
        value = "SELECT " +
                "FROM org.estatio.dom.agreement.AgreementRole " +
                "WHERE agreement == :agreement " +
                "&& party == :party " +
                "&& type == :type " +
                "&& endDate == :endDate"),
	@javax.jdo.annotations.Query(
        name = "findByAgreementAndTypeAndContainsDate", language = "JDOQL", 
        value = "SELECT " +
                "FROM org.estatio.dom.agreement.AgreementRole " +
                "WHERE agreement == :agreement " +
                "&& type == :type "+ 
                "&& (startDate == null | startDate < :date) "+
                "&& (endDate == null | endDate > :date) ")
})
@Bookmarkable(BookmarkPolicy.AS_CHILD)
@MemberGroups({"General", "Dates", "Related"})
public class AgreementRole extends EstatioTransactionalObject<AgreementRole> implements WithInterval<AgreementRole> {

    public AgreementRole() {
        super("agreement, party, startDate desc, type");
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "AGREEMENT_ID")
    private Agreement agreement;

    @Title(sequence = "3", prepend = ":")
    @MemberOrder(sequence = "1")
    @Hidden(where = Where.REFERENCES_PARENT)
    public Agreement getAgreement() {
        return agreement;
    }

    public void setAgreement(final Agreement agreement) {
        this.agreement = agreement;
    }

    public void modifyAgreement(final Agreement agreement) {
        Agreement currentAgreement = getAgreement();
        // check for no-op
        if (agreement == null || agreement.equals(currentAgreement)) {
            return;
        }
        // delegate to parent to associate
        if (currentAgreement != null) {
            currentAgreement.removeFromRoles(this);
        }
        agreement.addToRoles(this);
    }

    public void clearAgreement() {
        Agreement currentAgreement = getAgreement();
        // check for no-op
        if (currentAgreement == null) {
            return;
        }
        // delegate to parent to dissociate
        currentAgreement.removeFromRoles(this);
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "PARTY_ID")
    private Party party;

    @Title(sequence = "2", prepend = ":")
    @MemberOrder(sequence = "2")
    @Hidden(where = Where.REFERENCES_PARENT)
    public Party getParty() {
        return party;
    }

    public void setParty(final Party party) {
        this.party = party;
    }

    public void modifyParty(final Party party) {
        Party currentParty = getParty();
        if (party == null || party.equals(currentParty)) {
            return;
        }
        party.addToAgreements(this);
    }

    public void clearParty() {
        Party currentParty = getParty();
        if (currentParty == null) {
            return;
        }
        currentParty.removeFromAgreements(this);
    }

    // //////////////////////////////////////

    private AgreementRoleType type;

    @Title(sequence = "1")
    @MemberOrder(sequence = "3")
    public AgreementRoleType getType() {
        return type;
    }

    public void setType(final AgreementRoleType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    private LocalDate startDate;

    @MemberOrder(name="Dates", sequence = "4")
    @Optional
    @javax.jdo.annotations.Persistent
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    private LocalDate endDate;

    @MemberOrder(name="Dates", sequence = "5")
    @Optional
    @javax.jdo.annotations.Persistent
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // //////////////////////////////////////

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
    public AgreementRole getPrevious() {
        return getStartDate() != null
                ?agreementRoles.findByAgreementAndPartyAndTypeAndEndDate(getAgreement(), getParty(), getType(), getStartDate().minusDays(1))
                :null;
    }

    @MemberOrder(name="Related", sequence = "9.2")
    @Named("Next Role")
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @Optional
    @Override
    public AgreementRole getNext() {
        return getEndDate() != null
                ?agreementRoles.findByAgreementAndPartyAndTypeAndStartDate(getAgreement(), getParty(), getType(), getEndDate().plusDays(1))
                :null;
    }

    // //////////////////////////////////////

    private SortedSet<AgreementRoleCommunicationChannel> communicationChannels = new TreeSet<AgreementRoleCommunicationChannel>();

    @MemberOrder(sequence = "1")
    public SortedSet<AgreementRoleCommunicationChannel> getCommunicationChannels() {
        return communicationChannels;
    }

    public void setCommunicationChannels(final SortedSet<AgreementRoleCommunicationChannel> communinationChannels) {
        this.communicationChannels = communinationChannels;
    }

    public void addToCommunicationChannels(final AgreementRoleCommunicationChannel channel) {
        if (channel == null || getCommunicationChannels().contains(channel)) {
            return;
        }
        channel.clearRole();
        channel.setRole(this);
        getCommunicationChannels().add(channel);
    }

    public void removeFromCommunicationChannels(final AgreementRoleCommunicationChannel channel) {
        if (channel == null || !getCommunicationChannels().contains(channel)) {
            return;
        }
        channel.setRole(null);
        getCommunicationChannels().remove(channel);
    }

    // //////////////////////////////////////

    public AgreementRoleCommunicationChannel findCommunicationChannel(final AgremeentRoleCommunicationChannelType type, final LocalDate date) {
        return firstMatch(AgreementRoleCommunicationChannel.class, new Filter<AgreementRoleCommunicationChannel>() {
            @Override
            public boolean accept(AgreementRoleCommunicationChannel t) {
                return t.getType() == type && getInterval().contains(date);
            }
        });
    }

    // //////////////////////////////////////

    public AgreementRole addCommunicationChannel(@Named("Type") AgremeentRoleCommunicationChannelType type, @Named("Communication Channel") CommunicationChannel communicationChannel) {
        if (type != null && communicationChannel != null) {
            AgreementRoleCommunicationChannel arcc = findCommunicationChannel(type, clockService.now());
            if (arcc == null) {
                arcc = newTransientInstance(AgreementRoleCommunicationChannel.class);
                persistIfNotAlready(arcc);
                arcc.setStartDate(startDate);
                arcc.setCommunicationChannel(communicationChannel);
                arcc.setType(type);
                addToCommunicationChannels(arcc);
            }
        }
        return this;
    }

    public List<CommunicationChannel> choices1AddCommunicationChannel() {
        return Lists.newArrayList(getParty().getCommunicationChannels());
    }

    public CommunicationChannel default1AddCommunicationChannel() {
        return getParty().getCommunicationChannels().first();
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "7")
    public boolean isCurrent() {
        return isActiveOn(clockService.now());
    }

    private boolean isActiveOn(LocalDate localDate) {
        return getInterval().contains(localDate);
    }

    // //////////////////////////////////////

    private AgreementRoleCommunicationChannels agreementRoleCommunicationChannels;

    public void injectAgreementRoleCommunicationChannels(AgreementRoleCommunicationChannels agreementRoleCommunicationChannels) {
        this.agreementRoleCommunicationChannels = agreementRoleCommunicationChannels;
    }

    private ClockService clockService;

    public void injectClockService(final ClockService clockService) {
        this.clockService = clockService;
    }

    private AgreementRoles agreementRoles;
    
    public void injectAgreementRoles(AgreementRoles agreementRoles) {
        this.agreementRoles = agreementRoles;
    }

}
