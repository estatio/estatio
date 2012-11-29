package com.eurocommercialproperties.estatio.dom.party;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;

import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannel;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannelType;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;

//@ObjectType("PRTY")
@PersistenceCapable
public abstract class Party extends AbstractDomainObject {

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

    // {{ Roles (set, bidir)
    // REVIEW: changed this startDate set of PartyRoleType, which I suspect was wrong
    // (in any case can't have sets of enums)
    @javax.jdo.annotations.Persistent(mappedBy = "party")
    private Set<PartyRole> roles = new LinkedHashSet<PartyRole>();

    @MemberOrder(sequence = "20")
    public Set<PartyRole> getRoles() {
        return roles;
    }

    public void setRoles(final Set<PartyRole> roles) {
        this.roles = roles;
    }

    // }}

    // {{ Registrations (set, bidir)
    //@javax.jdo.annotations.Persistent(mappedBy = "party")
    private Set<PartyRegistration> registrations; // = new LinkedHashSet<PartyRegistration>();

    @MemberOrder(sequence = "21")
    public Set<PartyRegistration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(final Set<PartyRegistration> registrations) {
        this.registrations = registrations;
    }

    // }}

    // {{ CommunicationChannels (list, unidir)
    @Join(column = "PARTY_ID", generateForeignKey = "false")
    @Element(column = "COMMUNICATIONCHANNEL_ID", generateForeignKey = "false")
    private Set<CommunicationChannel> communicationChannels = new LinkedHashSet<CommunicationChannel>();

    @MemberOrder(sequence = "10")
    public Set<CommunicationChannel> getCommunicationChannels() {
        return communicationChannels;
    }

    public void setCommunicationChannels(final Set<CommunicationChannel> communicationChannels) {
        this.communicationChannels = communicationChannels;
    }

    public CommunicationChannel addCommunicationChannel(final CommunicationChannelType communicationChannelType) {
        CommunicationChannel communicationChannel = communicationChannelType.create(getContainer());
        communicationChannels.add(communicationChannel);
        return communicationChannel;
    }

    @Hidden
    public void addCommunicationChannel(CommunicationChannel communicationChannel) {
        communicationChannels.add(communicationChannel);
    }

    // }}

}
