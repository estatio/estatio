package com.eurocommercialproperties.estatio.dom.party;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannel;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannelType;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy=IdGeneratorStrategy.IDENTITY)
@javax.jdo.annotations.Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
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
    // REVIEW: changed this from set of PartyRoleType, which I suspect was wrong (in any case can't have sets of enums)
    @javax.jdo.annotations.Persistent(mappedBy="party")
    private Set<PartyRole> roles = new LinkedHashSet<PartyRole>();

    @MemberOrder(sequence = "1")
    public Set<PartyRole> getRoles() {
        return roles;
    }

    public void setRoles(final Set<PartyRole> roles) {
        this.roles = roles;
    }
    // }}

    
    // {{ CommunicationChannels (list, unidir)
    @javax.jdo.annotations.Join // to avoid FK in CommunicationChannel back to Party
    private List<CommunicationChannel> communicationChannels = new ArrayList<CommunicationChannel>();

    @MemberOrder(sequence = "2.1")
    public List<CommunicationChannel> getCommunicationChannels() {
        return communicationChannels;
    }

    public void setCommunicationChannels(final List<CommunicationChannel> communicationChannels) {
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
