/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.party;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.Status;
import org.estatio.dom.WithNameComparable;
import org.estatio.dom.WithReferenceUnique;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelOwner;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({ 
    @javax.jdo.annotations.Query(
            name = "findByReferenceOrName", language = "JDOQL", 
            value = "SELECT "
                    + "FROM org.estatio.dom.party.Party "
                    + "WHERE reference.matches(:referenceOrName) "
                    +    "|| name.matches(:referenceOrName)") })
@javax.jdo.annotations.Index(name = "PARTY_REFERENCE_NAME_IDX", members = { "reference", "name" })
@AutoComplete(repository = Parties.class, action = "autoComplete")
@Bookmarkable
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
    @javax.jdo.annotations.Index(name = "PARTY_REFERENCE_IDX")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    private String name;

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

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "party")
    private SortedSet<AgreementRole> agreements = new TreeSet<AgreementRole>();

    @Render(Type.EAGERLY)
    public SortedSet<AgreementRole> getAgreements() {
        return agreements;
    }

    public void setAgreements(final SortedSet<AgreementRole> agreements) {
        this.agreements = agreements;
    }

    // //////////////////////////////////////

    // TODO: is this in scope, or can we remove?
    // if in scope, is it a bidir requiring mappedBy?
    // @javax.jdo.annotations.Persistent(mappedBy = "party")
    private SortedSet<PartyRegistration> registrations = new TreeSet<PartyRegistration>();

    @Render(Type.EAGERLY)
    public SortedSet<PartyRegistration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(final SortedSet<PartyRegistration> registrations) {
        this.registrations = registrations;
    }

    public Party addRegistration() {
        return this;
    }

}
