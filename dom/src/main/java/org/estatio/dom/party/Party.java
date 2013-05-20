package org.estatio.dom.party;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;

@javax.jdo.annotations.PersistenceCapable/*(extensions={
        @Extension(vendorName="datanucleus", key="multitenancy-column-name", value="iid"),
        @Extension(vendorName="datanucleus", key="multitenancy-column-length", value="4"),
    })*/
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@AutoComplete(repository = Parties.class)
public abstract class Party extends EstatioTransactionalObject {

    // {{ Reference (attribute)
    private String reference;

    @Disabled
    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // }}

    // {{ Name (property)
    private String name;

    @MemberOrder(sequence = "2")
    @Title
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // }}

    // {{ CommunicationChannels (list, unidir)
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
        // check for no-op
        if (communicationChannel == null || getCommunicationChannels().contains(communicationChannel)) {
            return;
        }
        // associate new
        getCommunicationChannels().add(communicationChannel);
    }

    public void removeFromCommunicationChannels(final CommunicationChannel communicationChannel) {
        // check for no-op
        if (communicationChannel == null || !getCommunicationChannels().contains(communicationChannel)) {
            return;
        }
        // dissociate existing
        getCommunicationChannels().remove(communicationChannel);
    }

    @MemberOrder(name = "CommunicationChannels", sequence = "10")
    public CommunicationChannel addCommunicationChannel(final CommunicationChannelType communicationChannelType) {
        CommunicationChannel communicationChannel = communicationChannelType.create(getContainer());
        addToCommunicationChannels(communicationChannel);
        return communicationChannel;
    }

    // }}

    // {{ Agreements (Collection)
    @javax.jdo.annotations.Persistent(mappedBy="party")
    private Set<AgreementRole> agreements = new LinkedHashSet<AgreementRole>();

    @MemberOrder(name = "Agreements", sequence = "11")
    @Render(Type.EAGERLY)
    public Set<AgreementRole> getAgreements() {
        return agreements;
    }

    public void setAgreements(final Set<AgreementRole> agreements) {
        this.agreements = agreements;
    }

    public void addToAgreements(final AgreementRole agreementRole) {
        // check for no-op
        if (agreementRole == null || getAgreements().contains(agreementRole)) {
            return;
        }
        // dissociate arg from its current parent (if any).
        agreementRole.clearParty();
        // associate arg
        agreementRole.setParty(this);
        getAgreements().add(agreementRole);
    }

    public void removeFromAgreements(final AgreementRole agreementRole) {
        // check for no-op
        if (agreementRole == null || !getAgreements().contains(agreementRole)) {
            return;
        }
        // dissociate arg
        agreementRole.setParty(null);
        getAgreements().remove(agreementRole);
    }
    
    // }}

    // {{ Registrations (set, bidir)
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

    // }}


    // {{ Roles (set, bidir)
    @javax.jdo.annotations.Persistent(mappedBy = "party")
    private SortedSet<PartyRole> roles = new TreeSet<PartyRole>();

    @MemberOrder(name = "Roles", sequence = "14")
    public SortedSet<PartyRole> getRoles() {
        return roles;
    }

    public void setRoles(final SortedSet<PartyRole> roles) {
        this.roles = roles;
    }

    @MemberOrder(name = "Roles", sequence = "14")
    public Party addRole() {
        // TODO: some code here
        return this;
    }

    // }}

    
}
