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
import com.google.common.collect.Sets;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Mask;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.Status;
import org.estatio.dom.WithNameComparable;
import org.estatio.dom.WithReferenceUnique;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRoleType;
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
        super("name", Status.UNLOCKED, Status.LOCKED);
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
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    @Named("Lookup")
    public FixedAsset lookupLocation(@Named("Address") String address) {
        setLocation(locationLookupService.lookup(address));
        return this;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "asset")
    private SortedSet<FixedAssetRole> roles = new TreeSet<FixedAssetRole>();

    @Render(Type.EAGERLY)
    public SortedSet<FixedAssetRole> getRoles() {
        return roles;
    }

    public void setRoles(final SortedSet<FixedAssetRole> roles) {
        this.roles = roles;
    }


    @Named("Create Initial")
    public FixedAsset createInitialRole(
            final @Named("Type") FixedAssetRoleType type,
            final Party party,
            final @Named("Start date") @Optional LocalDate startDate,
            final @Named("End date") @Optional LocalDate endDate) {
        createRole(type, party, startDate, endDate);
        return this;
    }
    
    public String validateCreateInitialRole(
            final FixedAssetRoleType type,
            final Party party,
            final LocalDate startDate,
            final LocalDate endDate) {
        if(startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return "End date cannot be earlier than start date";
        }
        if (!Sets.filter(getRoles(), type.matchingRole()).isEmpty()) {
            return "Add a successor/predecessor from existing role";
        }
        return null;
    }
    

    @Programmatic
    public FixedAssetRole createRole(final FixedAssetRoleType type, final Party party, final LocalDate startDate, final LocalDate endDate) {
        final FixedAssetRole role = newTransientInstance(FixedAssetRole.class);
        role.setStartDate(startDate);
        role.setEndDate(endDate);
        role.setType(type); // must do before associate with agreement, since part of AgreementRole#compareTo impl.

        role.setStatus(Status.UNLOCKED);
        
        // JDO will manage the relationship for us
        // see http://markmail.org/thread/b6lpzktr6hzysisp, Dan's email 2013-7-17
        role.setParty(party);
        role.setAsset(this);
        
        persistIfNotAlready(role);
        
        return role;
    }

    
    // //////////////////////////////////////

    @javax.jdo.annotations.Join(column = "FIXEDASSET_ID", generateForeignKey = "false")
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


    private LocationLookupService locationLookupService;

    public final void injectLocationLookupService(LocationLookupService locationLookupService) {
        this.locationLookupService = locationLookupService;
    }

}
