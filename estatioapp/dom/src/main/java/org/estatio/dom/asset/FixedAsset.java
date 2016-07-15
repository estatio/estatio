/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Sets;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.WithNameComparable;
import org.estatio.dom.WithReferenceUnique;
import org.estatio.dom.asset.ownership.FixedAssetOwnership;
import org.estatio.dom.asset.ownership.FixedAssetOwnershipRepository;
import org.estatio.dom.communicationchannel.CommunicationChannelOwner;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.TitleBuilder;
import org.estatio.dom.valuetypes.LocalDateInterval;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME,
        column = "discriminator")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "FixedAsset_reference_UNQ", members = { "reference" })
})
@javax.jdo.annotations.Indices({
        // to cover the 'findAssetsByReferenceOrName' query
        // both in this superclass and the subclasses
        @javax.jdo.annotations.Index(
                name = "FixedAsset_reference_name_IDX", members = { "reference", "name" })
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "matchByReferenceOrName", language = "JDOQL",
                value = "SELECT FROM org.estatio.dom.asset.FixedAsset "
                        + "WHERE reference.matches(:regex) "
                        + "|| name.matches(:regex) ")
})
@DomainObject(
        editing = Editing.DISABLED,
        autoCompleteRepository = FixedAssetRepository.class,
        autoCompleteAction = "autoComplete"
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public abstract class FixedAsset<X extends FixedAsset<X>>
        extends EstatioDomainObject<X>
        implements WithNameComparable<X>, WithReferenceUnique, CommunicationChannelOwner {

    public FixedAsset() {
        super("name");
    }

    public String title() {
        return TitleBuilder.start()
                .withReference(getReference())
                .withName(getName())
                .toString();
    }

    @Inject
    FixedAssetRoleRepository fixedAssetRoleRepository;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.REFERENCE)
    @Property(regexPattern = RegexValidation.REFERENCE)
    @PropertyLayout(describedAs = "Unique reference code for this asset")
    @Getter @Setter
    private String reference;

    // //////////////////////////////////////

    // /**
    // * Although both {@link Property} and {@link Unit} (the two subclasses)
    // have
    // * a name, they are mapped separately because they have different
    // uniqueness
    // * constraints.
    // *
    // * <p>
    // * For {@link Property}, the {@link Property#getName() name} by itself is
    // unique.
    // *
    // * <p>
    // * For {@link Unit}, the combination of ({@link Unit#getProperty()
    // property}, {@link Unit#getName() name})
    // * is unique.
    // */
    // public abstract String getName();

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.NAME)
    @PropertyLayout(describedAs = "Unique name for this property")
    @Getter @Setter
    private String name;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.REFERENCE)
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String externalReference;

    @MemberOrder(name = "externalReference", sequence = "1")
    public FixedAsset changeExternalReference(final String externalReference) {
        setExternalReference(externalReference);
        return this;
    }

    public String default0ChangeExternalReference(final String externalReference) {
        return getExternalReference();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "fixedAsset")
    @CollectionLayout(defaultView = "table")
    @Getter @Setter
    private SortedSet<FixedAssetOwnership> owners = new TreeSet<>();

    public FixedAsset addOwner(final Party newOwner, final OwnershipType type) {
        FixedAssetOwnership fixedAssetOwnership = fixedAssetOwnershipRepository.newOwnership(newOwner, type, this);
        getOwners().add(fixedAssetOwnership);
        return this;
    }

    public List<Party> choices0AddOwner() {
        List<FixedAssetRole> roles = fixedAssetRoleRepository.findByAssetAndType(this, FixedAssetRoleType.PROPERTY_OWNER);
        return roles.stream().map(FixedAssetRole::getParty).collect(Collectors.toList());
    }

    public String validateAddOwner(final Party newOwner, final OwnershipType type) {
        if (getOwners().stream().filter(owner -> owner.getOwner().equals(newOwner)).toArray().length > 0) {
            return "This owner already has its share defined";
        } else if (type.equals(OwnershipType.FULL) && getOwners().size() != 0 && (getOwners().stream().filter(owner -> owner.getOwnershipType().equals(OwnershipType.FULL)).toArray().length == 0)) {
            return "This owner can not be a full owner as there is already a shared owner defined";
        } else {
            return null;
        }
    }

    public String disableAddOwner(final Party newOwner, final OwnershipType type) {
        if (getOwners().stream().filter(owner -> owner.getOwnershipType().equals(OwnershipType.FULL)).toArray().length > 0) {
            return "This property is fully owned. To add another owner, first change the ownership type of the current owner to 'shared'";
        } else {
            return null;
        }
    }

    @Action(hidden = Where.ALL_TABLES)
    public boolean getFullOwnership() {
        final SortedSet<FixedAssetOwnership> owners = getOwners();
        return (owners.size() == 1 && owners.first().getOwnershipType().equals(OwnershipType.FULL));
    }

    // //////////////////////////////////////

    @CollectionLayout(render = RenderType.EAGERLY)
    @javax.jdo.annotations.Persistent(mappedBy = "asset")
    @Getter @Setter
    private SortedSet<FixedAssetRole> roles = new TreeSet<>();

    public FixedAsset newRole(
            final FixedAssetRoleType type,
            final Party party,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        createRole(type, party, startDate, endDate);
        return this;
    }

    public String validateNewRole(
            final FixedAssetRoleType type,
            final Party party,
            final LocalDate startDate,
            final LocalDate endDate) {
        List<FixedAssetRole> currentRoles = fixedAssetRoleRepository.findAllForPropertyAndPartyAndType(this, party, type);
        for (FixedAssetRole fixedAssetRole : currentRoles) {
            LocalDateInterval existingInterval = new LocalDateInterval(fixedAssetRole.getStartDate(), fixedAssetRole.getEndDate());
            LocalDateInterval newInterval = new LocalDateInterval(startDate, endDate);
            if (existingInterval.overlaps(newInterval)) {
                return "The provided dates overlap with a current role of this type and party";
            }
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
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
        role.setType(type); // must do before associate with agreement, since
        // part of AgreementRole#compareTo impl.

        // JDO will manage the relationship for us
        // see http://markmail.org/thread/b6lpzktr6hzysisp, Dan's email
        // 2013-7-17
        role.setParty(party);
        role.setAsset(this);

        persistIfNotAlready(role);

        return role;
    }

    @Inject
    FixedAssetOwnershipRepository fixedAssetOwnershipRepository;
}
