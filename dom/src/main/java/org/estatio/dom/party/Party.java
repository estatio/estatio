package org.estatio.dom.party;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;

import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;

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
    private SortedSet<PartyRole> roles = new TreeSet<PartyRole>();

    @MemberOrder(sequence = "20")
    public SortedSet<PartyRole> getRoles() {
        return roles;
    }

    public void setRoles(final SortedSet<PartyRole> roles) {
        this.roles = roles;
    }

    // }}

    // {{ Registrations (set, bidir)
    //@javax.jdo.annotations.Persistent(mappedBy = "party")
    private SortedSet<PartyRegistration> registrations = new TreeSet<PartyRegistration>();

    @MemberOrder(sequence = "21")
    public SortedSet<PartyRegistration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(final SortedSet<PartyRegistration> registrations) {
        this.registrations = registrations;
    }

    // }}

    // {{ CommunicationChannels (list, unidir)
    @Join(column = "PARTY_ID", generateForeignKey = "false")
    @Element(column = "COMMUNICATIONCHANNEL_ID", generateForeignKey = "false")
    private SortedSet<CommunicationChannel> communicationChannels = new TreeSet<CommunicationChannel>();

    @MemberOrder(sequence = "10")
    public SortedSet<CommunicationChannel> getCommunicationChannels() {
        return communicationChannels;
    }

    public void setCommunicationChannels(final SortedSet<CommunicationChannel> communicationChannels) {
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
