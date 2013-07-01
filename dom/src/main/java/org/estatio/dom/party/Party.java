package org.estatio.dom.party;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.VersionStrategy;

import org.estatio.dom.Status;
import org.estatio.dom.WithNameComparable;
import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithReferenceGetter;
import org.estatio.dom.WithReferenceUnique;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelOwner;
import org.estatio.dom.communicationchannel.CommunicationChannelType;

import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({ 
    @javax.jdo.annotations.Query(name = "findByReferenceOrName", language = "JDOQL", value = "SELECT FROM org.estatio.dom.party.Party WHERE reference.matches(:searchPattern) || name.matches(:searchPattern)") })
@javax.jdo.annotations.Index(name = "PARTY_REFERENCE_NAME_IDX", members = { "reference", "name" })
@AutoComplete(repository = Parties.class, action = "autoComplete")
public abstract class Party extends EstatioTransactionalObject<Party, Status> implements WithNameComparable<Party>, WithReferenceUnique, CommunicationChannelOwner {

    public Party() {
        super("name", Status.LOCKED, Status.UNLOCKED);
    }

    // //////////////////////////////////////

    private Status status;

    @Hidden
    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(final Status status) {
        this.status = status;
    }
    
    // //////////////////////////////////////

    @javax.jdo.annotations.Unique(name = "PARTY_REFERENCE_UNIQUE_IDX")
    private String reference;

    @Disabled
    @MemberOrder(sequence = "1")
    @javax.jdo.annotations.Index(name = "PARTY_REFERENCE_IDX")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    private String name;

    @MemberOrder(sequence = "2")
    @Title
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Provided so that subclasses can override and disable.
     */
    public String disableName() {
        return null;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Join(column = "PARTY_ID", generateForeignKey = "false")
    @javax.jdo.annotations.Element(column = "COMMUNICATIONCHANNEL_ID", generateForeignKey = "false")
    private SortedSet<CommunicationChannel> communicationChannels = new TreeSet<CommunicationChannel>();

    @MemberOrder(name = "CommunicationChannels", sequence = "10")
    @Render(Type.EAGERLY)
    public SortedSet<CommunicationChannel> getCommunicationChannels() {
        return communicationChannels;
    }

    public void setCommunicationChannels(final SortedSet<CommunicationChannel> communicationChannels) {
        this.communicationChannels = communicationChannels;
    }

    public void addToCommunicationChannels(final CommunicationChannel communicationChannel) {
        if (communicationChannel == null || getCommunicationChannels().contains(communicationChannel)) {
            return;
        }
        getCommunicationChannels().add(communicationChannel);
    }

    public void removeFromCommunicationChannels(final CommunicationChannel communicationChannel) {
        if (communicationChannel == null || !getCommunicationChannels().contains(communicationChannel)) {
            return;
        }
        getCommunicationChannels().remove(communicationChannel);
    }

    @MemberOrder(name = "CommunicationChannels", sequence = "10")
    public CommunicationChannel addCommunicationChannel(final CommunicationChannelType communicationChannelType) {
        CommunicationChannel communicationChannel = communicationChannelType.create(getContainer());
        addToCommunicationChannels(communicationChannel);
        return communicationChannel;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "party")
    private SortedSet<AgreementRole> agreements = new TreeSet<AgreementRole>();

    @MemberOrder(name = "Agreements", sequence = "11")
    @Render(Type.EAGERLY)
    public SortedSet<AgreementRole> getAgreements() {
        return agreements;
    }

    public void setAgreements(final SortedSet<AgreementRole> agreements) {
        this.agreements = agreements;
    }

    public void addToAgreements(final AgreementRole agreementRole) {
        if (agreementRole == null || getAgreements().contains(agreementRole)) {
            return;
        }
        agreementRole.clearParty();
        agreementRole.setParty(this);
        getAgreements().add(agreementRole);
    }

    public void removeFromAgreements(final AgreementRole agreementRole) {
        if (agreementRole == null || !getAgreements().contains(agreementRole)) {
            return;
        }
        agreementRole.setParty(null);
        getAgreements().remove(agreementRole);
    }

    // //////////////////////////////////////

    // TODO: is this in scope, or can we remove?
    // if in scope, is it a bidir requiring mappedBy?
    // @javax.jdo.annotations.Persistent(mappedBy = "party")
    private SortedSet<PartyRegistration> registrations = new TreeSet<PartyRegistration>();

    @MemberOrder(name = "Registrations", sequence = "12")
    @Render(Type.EAGERLY)
    public SortedSet<PartyRegistration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(final SortedSet<PartyRegistration> registrations) {
        this.registrations = registrations;
    }

    @MemberOrder(name = "Registrations", sequence = "12")
    public Party addRegistration() {
        return this;
    }

}
