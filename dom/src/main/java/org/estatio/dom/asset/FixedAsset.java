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

import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
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
import org.estatio.dom.communicationchannel.CommunicationChannelOwner;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=IdGeneratorStrategy.NATIVE, 
        column="id")
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME, 
        column="discriminator")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER, 
        column = "version")
@javax.jdo.annotations.Uniques({
    @javax.jdo.annotations.Unique(
            name="FixedAsset_reference_UNQ", members={"reference"})
})
@javax.jdo.annotations.Indices({
    // to cover the 'findAssetsByReferenceOrName' query
    // both in this superclass and the subclasses
     @javax.jdo.annotations.Index(
             name = "FixedAsset_reference_name_IDX", members = { "reference", "name" })
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findAssetsByReferenceOrName", language = "JDOQL",
                value = "SELECT FROM org.estatio.dom.asset.FixedAsset "
                        + "WHERE reference.matches(:regex) "
                        + "|| name.matches(:regex) ")
})
@Bookmarkable
@AutoComplete(repository = FixedAssets.class, action = "autoComplete")
public abstract class FixedAsset 
        extends EstatioTransactionalObject<FixedAsset, Status> 
        implements WithNameComparable<FixedAsset>, WithReferenceUnique, Locatable,  CommunicationChannelOwner {

    public FixedAsset() {
        super("name", Status.UNLOCKED, Status.LOCKED);
    }
    
    @Override
    public Status getLockable() {
        return getStatus();
    }

    @Override
    public void setLockable(final Status lockable) {
        setStatus(lockable);
    }

    // //////////////////////////////////////

    private Status status;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Hidden
    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }


    // //////////////////////////////////////

    private String reference;

    @javax.jdo.annotations.Column(allowsNull="false")
    @DescribedAs("Unique reference code for this asset")
    @Title(sequence = "1", prepend = "[", append = "] ")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

//    /**
//     * Although both {@link Property} and {@link Unit} (the two subclasses) have
//     * a name, they are mapped separately because they have different uniqueness
//     * constraints.
//     *
//     * <p>
//     * For {@link Property}, the {@link Property#getName() name} by itself is unique.
//     * 
//     * <p>
//     * For {@link Unit}, the combination of ({@link Unit#getProperty() property}, {@link Unit#getName() name}) 
//     * is unique.
//     */
    //public abstract String getName();
    
    private String name;

    @javax.jdo.annotations.Column(allowsNull="false")
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

    public void setLocation(final Location location) {
        this.location = location;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    @Named("Lookup")
    public FixedAsset lookupLocation(final @Named("Address") String address) {
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


    public FixedAsset newRole(
            final @Named("Type") FixedAssetRoleType type,
            final Party party,
            final @Named("Start date") @Optional LocalDate startDate,
            final @Named("End date") @Optional LocalDate endDate) {
        createRole(type, party, startDate, endDate);
        return this;
    }
    
    public String validateNewRole(
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
    public FixedAssetRole createRole(
            final FixedAssetRoleType type, final Party party, final LocalDate startDate, final LocalDate endDate) {
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



    private LocationLookupService locationLookupService;

    public final void injectLocationLookupService(final LocationLookupService locationLookupService) {
        this.locationLookupService = locationLookupService;
    }

}
