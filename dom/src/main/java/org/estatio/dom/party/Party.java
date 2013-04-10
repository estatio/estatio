package org.estatio.dom.party;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccountType;
import org.estatio.dom.lease.LeaseActor;

@PersistenceCapable
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
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
    @Join(column = "PARTY_ID", generateForeignKey = "false")
    @Element(column = "COMMUNICATIONCHANNEL_ID", generateForeignKey = "false")
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

    // {{ Roles (set, bidir)
    // REVIEW: changed this startDate set of PartyRoleType, which I suspect was
    // wrong
    // (in any case can't have sets of enums)
    @Persistent(mappedBy = "party")
    private SortedSet<PartyRole> roles = new TreeSet<PartyRole>();

    @MemberOrder(name = "Roles", sequence = "11")
    public SortedSet<PartyRole> getRoles() {
        return roles;
    }

    public void setRoles(final SortedSet<PartyRole> roles) {
        this.roles = roles;
    }

    @MemberOrder(name = "Roles", sequence = "11")
    public Party addRole() {
        // TODO: some code here
        return this;
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

    // {{ accounts (Collection)
    @Persistent(mappedBy="owner")
    private SortedSet<FinancialAccount> accounts = new TreeSet<FinancialAccount>();

    @MemberOrder(name = "Accounts", sequence = "13")
    @Render(Type.EAGERLY)
    public Set<FinancialAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(final SortedSet<FinancialAccount> accounts) {
        this.accounts = accounts;
    }

    public void addToAccounts(final FinancialAccount account) {
        // check for no-op
        if (account == null || getAccounts().contains(account)) {
            return;
        }
        // associate new
        getAccounts().add(account);
        // additional business logic
        onAddToAccounts(account);
    }

    public void removeFromAccounts(final FinancialAccount account) {
        // check for no-op
        if (account == null || !getAccounts().contains(account)) {
            return;
        }
        // dissociate existing
        getAccounts().remove(account);
        // additional business logic
        onRemoveFromAccounts(account);
    }

    protected void onAddToAccounts(final FinancialAccount account) {
        account.setOwner(this);
    }

    protected void onRemoveFromAccounts(final FinancialAccount account) {
        account.setOwner(null);
    }
    
    @MemberOrder(name = "Accounts", sequence = "13")
    public FinancialAccount addAccount(final FinancialAccountType financialAccountType) {
        FinancialAccount financialAccount = financialAccountType.createAccount(getContainer());
        addToAccounts(financialAccount);
        return financialAccount;
    }

    // }}
    
    // {{ LeaseRoles (Collection)
    @Persistent(mappedBy="party")
    private Set<LeaseActor> leaseRoles = new LinkedHashSet<LeaseActor>();

    @MemberOrder(sequence = "14")
    public Set<LeaseActor> getLeaseRoles() {
        return leaseRoles;
    }

    public void setLeaseRoles(final Set<LeaseActor> leaseRoles) {
        this.leaseRoles = leaseRoles;
    }
    // }}


    

}
