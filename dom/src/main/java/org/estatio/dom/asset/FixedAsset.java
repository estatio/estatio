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
package org.estatio.dom.asset;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.VersionStrategy;

import com.danhaywood.isis.wicket.gmap3.applib.Locatable;
import com.danhaywood.isis.wicket.gmap3.applib.Location;
import com.danhaywood.isis.wicket.gmap3.service.LocationLookupService;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Mask;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.Status;
import org.estatio.dom.WithNameComparable;
import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithNameUnique;
import org.estatio.dom.WithReferenceGetter;
import org.estatio.dom.WithReferenceUnique;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelOwner;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Query(name = "findAssetsByReferenceOrName", language = "JDOQL", value = "SELECT FROM org.estatio.dom.asset.FixedAsset WHERE reference.matches(:regex) || name.matches(:regex)")
@Bookmarkable
@AutoComplete(repository = FixedAssets.class, action = "autoComplete")
public abstract class FixedAsset extends EstatioTransactionalObject<FixedAsset, Status> implements WithNameComparable<FixedAsset>, WithReferenceUnique, Locatable,  CommunicationChannelOwner {

    public FixedAsset() {
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

    @javax.jdo.annotations.Unique(name = "FIXEDASSET_REFERENCE_UNIQUE_IDX")
    private String reference;

    @DescribedAs("Unique reference code for this asset")
    @Title(sequence = "1", prepend = "[", append = "] ")
    @MemberOrder(sequence = "1.1")
    @Mask("AAAAAAAA")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    // TODO when I added this annotation, per the @DescribedAs, then the
    // fixtures failed to load...
    // @javax.jdo.annotations.Unique(name = "NAME_IDX")
    private String name;

    @DescribedAs("Unique name for this property")
    @Title(sequence = "2")
    @MemberOrder(sequence = "1.2")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private Location location;

    @Override
    @Disabled
    @Optional
    @MemberOrder(sequence = "1.8")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    @Named("Lookup")
    @MemberOrder(name = "location", sequence = "1.9")
    public FixedAsset lookupLocation(@Named("Address") String address) {
        setLocation(locationLookupService.lookup(address));
        return this;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "asset")
    private SortedSet<FixedAssetRole> roles = new TreeSet<FixedAssetRole>();

    @Render(Type.EAGERLY)
    @MemberOrder(name = "Roles", sequence = "2.1")
    public SortedSet<FixedAssetRole> getRoles() {
        return roles;
    }

    public void setRoles(final SortedSet<FixedAssetRole> roles) {
        this.roles = roles;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    @MemberOrder(name = "Roles", sequence = "1")
    public FixedAssetRole addRole(@Named("party") Party party, @Named("type") FixedAssetRoleType type, @Named("startDate") @Optional LocalDate startDate, @Named("endDate") @Optional LocalDate endDate) {
        FixedAssetRole role = fixedAssetRoles.findRole(this, party, type, startDate, endDate);
        if (role == null) {
            role = fixedAssetRoles.newRole(this, party, type, startDate, endDate);
        }
        return role;
    }

    public List<Party> choices0AddRole() {
        // TODO: this doesn't look right.  Should we have an autoComplete here instead?
        return parties.allParties();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Join(column = "FIXEDASSET_ID", generateForeignKey = "false")
    @javax.jdo.annotations.Element(column = "COMMUNICATIONCHANNEL_ID", generateForeignKey = "false")
    private SortedSet<CommunicationChannel> communicationChannels = new TreeSet<CommunicationChannel>();

    @Render(Type.EAGERLY)
    @MemberOrder(name = "CommunicationChannels", sequence = "1")
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

    // TODO: this doesn't look correct; the communication channel is never persisted anywhere...
    @ActionSemantics(Of.NON_IDEMPOTENT) 
    @MemberOrder(name = "CommunicationChannels", sequence = "1")
    public CommunicationChannel addCommunicationChannel(final CommunicationChannelType communicationChannelType) {
        CommunicationChannel communicationChannel = communicationChannelType.create(getContainer());
        addToCommunicationChannels(communicationChannel);
        return communicationChannel;
    }

    @Programmatic
    public CommunicationChannel findCommunicationChannelForType(CommunicationChannelType type) {
        for (CommunicationChannel c : communicationChannels) {
            if (c.getType().equals(type)) {
                return c;
            }
        }
        return null;
    }

    // //////////////////////////////////////

    private FixedAssetRoles fixedAssetRoles;

    public void injectFixedAssetRoles(final FixedAssetRoles fixedAssetRoles) {
        this.fixedAssetRoles = fixedAssetRoles;
    }

    private Parties parties;

    public void injectParties(Parties parties) {
        this.parties = parties;
    }

    private LocationLookupService locationLookupService;

    public void injectLocationLookupService(LocationLookupService locationLookupService) {
        this.locationLookupService = locationLookupService;
    }

}
