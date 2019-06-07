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
package org.estatio.module.agreement.dom;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Collection;
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
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.incode.module.base.dom.Chained;
import org.incode.module.base.dom.types.NameType;
import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.base.dom.with.WithInterval;
import org.incode.module.base.dom.with.WithIntervalMutable;
import org.incode.module.base.dom.with.WithNameGetter;
import org.incode.module.base.dom.with.WithReferenceGetter;

import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.agreement.dom.role.IAgreementRoleType;
import org.estatio.module.agreement.dom.type.AgreementType;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.party.dom.Party;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"    // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.VALUE_MAP,
        column = "discriminator",
        value = "org.estatio.dom.agreement.Agreement"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "Lease_reference_name_IDX", members = { "reference", "name" }),
        @javax.jdo.annotations.Index( // TODO: REVIEW - can't see point of this index and Lease_reference_name_IDX. Also, rename?
                name = "Lease_reference_name_type_IDX", members = { "reference", "name", "type" }),
        @javax.jdo.annotations.Index( // to support queries matching by name only, eg
                name = "Agreement_name_reference_IDX", members = { "name", "reference" }),
})
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "Agreement_reference_type_UNQ", members = { "reference", "type" })
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.agreement.dom.Agreement "
                        + "WHERE reference == :reference"),
        @javax.jdo.annotations.Query(
                name = "findByTypeAndReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.agreement.dom.Agreement "
                        + "WHERE type == :agreementType "
                        + "&& reference == :reference"),
        @javax.jdo.annotations.Query(
                name = "findByTypeAndReferenceOrName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.agreement.dom.Agreement "
                        + "WHERE type == :agreementType "
                        + "&& (reference.matches(:regex) || reference.matches(:regex))"),
        @javax.jdo.annotations.Query(
                name = "findByAgreementTypeAndRoleTypeAndParty", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.agreement.dom.Agreement "
                        + "WHERE type == :agreementType"
                        + " && roles.contains(role)"
                        + " && role.type == :roleType"
                        + " && role.party == :party"
                        + " VARIABLES org.estatio.module.agreement.dom.AgreementRole role")
})
@DomainObject(editing = Editing.DISABLED)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public abstract class Agreement
        extends UdoDomainObject2<Agreement>
        implements
        WithReferenceGetter,
        //WithReferenceComparable<Agreement>,
        WithIntervalMutable<Agreement>,
        Chained<Agreement>,
        WithNameGetter {

    @NotPersistent
    protected final IAgreementRoleType primaryRoleType;
    @NotPersistent
    protected final IAgreementRoleType secondaryRoleType;

    public Agreement(final IAgreementRoleType primaryRoleType, final IAgreementRoleType secondaryRoleType) {
        super("type,reference");
        this.primaryRoleType = primaryRoleType;
        this.secondaryRoleType = secondaryRoleType;
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getName())
                .withReference(getReference())
                .toString();
    }

    @javax.jdo.annotations.Column(allowsNull = "false", length = ReferenceType.Meta.MAX_LEN)
    @Property(regexPattern = ReferenceType.Meta.REGEX)
    @PropertyLayout(describedAs = "Unique reference code for this agreement")
    @Getter @Setter
    private String reference;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(length = NameType.Meta.MAX_LEN)
    @Property(optionality = Optionality.OPTIONAL, hidden = Where.ALL_TABLES)
    @PropertyLayout(describedAs = "Optional name for this agreement")
    @Getter @Setter
    private String name;

    // //////////////////////////////////////

    @Property(notPersisted = true)
    public Party getPrimaryParty() {
        // can probably also use:
        // final AgreementRoleType art = primaryRoleType.findOrCreateUsing(agreementRoleTypeRepository);
        final AgreementRoleType art = agreementRoleTypeRepository.findByTitle(primaryRoleType.getTitle());
        final AgreementRole currentOrMostRecentRole = findCurrentOrMostRecentAgreementRole(art);
        return partyOf(currentOrMostRecentRole);
    }

    @Programmatic
    public Party primaryPartyAsOf(final LocalDate date) {
        // can probably also use:
        // final AgreementRoleType art = primaryRoleType.findOrCreateUsing(agreementRoleTypeRepository);
        final AgreementRoleType art = agreementRoleTypeRepository.findByTitle(primaryRoleType.getTitle());
        final AgreementRole currentOrMostRecentRole = findAgreementRoleAsOf(art, date);
        return partyOf(currentOrMostRecentRole);
    }

    @Property(notPersisted = true)
    public Party getSecondaryParty() {
        final AgreementRoleType art = agreementRoleTypeRepository.findByTitle(secondaryRoleType.getTitle());
        final AgreementRole currentOrMostRecentRole = findCurrentOrMostRecentAgreementRole(art);
        return partyOf(currentOrMostRecentRole);
    }

    // //////////////////////////////////////

    @Programmatic
    protected AgreementRole getPrimaryAgreementRole() {
        // can probably also use:
        // final AgreementRoleType art = primaryRoleType.findOrCreateUsing(agreementRoleTypeRepository);
        final AgreementRoleType art = agreementRoleTypeRepository.findByTitle(primaryRoleType.getTitle());
        return findCurrentOrMostRecentAgreementRole(art);
    }


    @Programmatic
    protected AgreementRole getSecondaryAgreementRole() {
        // can probably also use:
        // final AgreementRoleType art = primaryRoleType.findOrCreateUsing(agreementRoleTypeRepository);
        final AgreementRoleType art = agreementRoleTypeRepository.findByTitle(secondaryRoleType.getTitle());
        return findCurrentOrMostRecentAgreementRole(art);
    }

    // //////////////////////////////////////

    protected Party findCurrentOrMostRecentParty(final String agreementRoleTypeTitle) {
        final AgreementRole currentOrMostRecentRole = findCurrentOrMostRecentAgreementRole(agreementRoleTypeTitle);
        return partyOf(currentOrMostRecentRole);
    }

    protected Party findCurrentOrMostRecentParty(final AgreementRoleType art) {
        final AgreementRole currentOrMostRecentRole = findCurrentOrMostRecentAgreementRole(art);
        return partyOf(currentOrMostRecentRole);
    }

    protected AgreementRole findCurrentOrMostRecentAgreementRole(final String agreementRoleTypeTitle) {
        final AgreementRoleType art = agreementRoleTypeRepository.findByTitle(agreementRoleTypeTitle);
        return findCurrentOrMostRecentAgreementRole(art);
    }

    protected AgreementRole findCurrentOrMostRecentAgreementRole(final AgreementRoleType agreementRoleType) {
        // all available roles
        final Iterable<AgreementRole> rolesOfType =
                Iterables.filter(getRoles(), AgreementRole.Predicates.whetherTypeIs(agreementRoleType));

        // try to find the one that is current...
        Iterable<AgreementRole> roles =
                Iterables.filter(rolesOfType, WithInterval.Predicates.<AgreementRole>whetherCurrentIs(true));

        // ... else the most recently ended one
        if (Iterables.isEmpty(roles)) {
            final List<AgreementRole> rolesInList = Lists.newArrayList(rolesOfType);
            roles = orderRolesByEffectiveEndDateReverseNullsFirst().leastOf(rolesInList, 1);
        }

        // and return the party
        final AgreementRole currentOrMostRecentRole = firstElseNull(roles);
        return currentOrMostRecentRole;
    }

    protected AgreementRole findAgreementRoleAsOf(final AgreementRoleType agreementRoleType, final LocalDate date) {
        return Lists.newArrayList(getRoles()).stream()
                        .filter(ar -> ar != null && ar.getType() == agreementRoleType)
                        .filter(ar -> ar.getInterval().contains(date))
                        .findFirst().orElse(null);
    }

    private static <T> T firstElseNull(final Iterable<T> iterable) {
        Iterator<T> iterator = iterable.iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    protected Party partyOf(final AgreementRole agreementRole) {
        return AgreementRole.Functions.partyOf().apply(agreementRole);
    }

    private static Ordering<AgreementRole> orderRolesByEffectiveEndDateReverseNullsFirst() {
        return Ordering.natural().onResultOf(AgreementRole.Functions.effectiveEndDateOf()).reverse().nullsFirst();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    @Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private LocalDate startDate;

    @javax.jdo.annotations.Persistent
    @Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private LocalDate endDate;

    // //////////////////////////////////////

    @Programmatic
    @Override
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Programmatic
    public LocalDateInterval getEffectiveInterval() {
        return new LocalDateInterval(getStartDate(), getEndDate());
    }

    // //////////////////////////////////////

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(final LocalDate date) {
        return getEffectiveInterval().contains(date);
    }

    // //////////////////////////////////////

    private WithIntervalMutable.Helper<Agreement> changeDates = new WithIntervalMutable.Helper<>(this);

    WithIntervalMutable.Helper<Agreement> getChangeDates() {
        return changeDates;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @Override
    public Agreement changeDates(
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        return getChangeDates().changeDates(startDate, endDate);
    }

    public String disableChangeDates() {
        return null;
    }

    @Override
    public LocalDate default0ChangeDates() {
        return getChangeDates().default0ChangeDates();
    }

    @Override
    public LocalDate default1ChangeDates() {
        return getChangeDates().default1ChangeDates();
    }

    @Override
    public String validateChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return getChangeDates().validateChangeDates(startDate, endDate);
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "typeId", allowsNull = "false")
    @Property(hidden = Where.EVERYWHERE, editing = Editing.DISABLED)
    @Getter @Setter
    private AgreementType type;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "previousAgreementId")
    @javax.jdo.annotations.Persistent(mappedBy = "next")
    @Property(optionality = Optionality.OPTIONAL, editing = Editing.DISABLED, hidden = Where.ALL_TABLES)
    @PropertyLayout(named = "Previous Agreement")
    @Getter @Setter
    private Agreement previous;

    public abstract Agreement changePrevious(final Agreement previousAgreement);

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "nextAgreementId")
    @Property(optionality = Optionality.OPTIONAL, editing = Editing.DISABLED, hidden = Where.ALL_TABLES)
    @PropertyLayout(named = "Next Agreement")
    @Getter @Setter
    private Agreement next;

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "agreement", defaultFetchGroup = "true")
    @Collection(editing = Editing.DISABLED)
    @CollectionLayout(render = RenderType.EAGERLY)
    @Getter @Setter
    private SortedSet<AgreementRole> roles = new TreeSet<>();

    @MemberOrder(name = "roles", sequence = "1")
    public Agreement newRole(
            final AgreementRoleType type,
            final Party party,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        createRole(type, party, startDate, endDate);
        return this;
    }

    public String validateNewRole(
            final AgreementRoleType art,
            final Party newParty,
            final LocalDate startDate,
            final LocalDate endDate) {

        Party currentParty = findCurrentOrMostRecentParty(art);
        if (currentParty != null && !Objects.equal(currentParty.getApplicationTenancy(), newParty.getApplicationTenancy())) {
            return "The application level of the new party must be the same as that of the current party";
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return "End date cannot be earlier than start date";
        }

        if (!Sets.filter(getRoles(), AgreementRole.Predicates.matchingRoleAndPeriod(art, startDate, endDate)).isEmpty()) {
            return "There is already a role for this type and period";
        }
        return null;
    }

    public List<AgreementRoleType> choices0NewRole() {
        return agreementRoleTypeRepository.findApplicableTo(getType());
    }

    public LocalDate default2NewRole() {
        return getEffectiveInterval().startDate();
    }

    public LocalDate default3NewRole() {
        return getEffectiveInterval().endDate();
    }

    /**
     * Provided for BDD "glue"; delegated to by
     * {@link #newRole(AgreementRoleType, Party, LocalDate, LocalDate)}.
     */
    @Programmatic
    public AgreementRole createRole(
            final AgreementRoleType type,
            final Party party,
            final LocalDate startDate,
            final LocalDate endDate) {
        final AgreementRole role = newTransientInstance(AgreementRole.class);
        role.setStartDate(startDate);
        role.setEndDate(endDate);
        role.setType(type); // must do before associate with agreement, since
        // part of AgreementRole#compareTo impl.
        // JDO will manage the relationship for us
        // see http://markmail.org/thread/b6lpzktr6hzysisp, Dan's email
        // 2013-7-17
        role.setParty(party);
        role.setAgreement(this);

        persistIfNotAlready(role);

        return role;
    }

    @Programmatic
    public AgreementRole findRole(final Party party, final AgreementRoleType type, final LocalDate date) {
        return agreementRoleRepository.findByAgreementAndPartyAndTypeAndContainsDate(this, party, type, date);
    }

    @Programmatic
    public AgreementRole findRoleWithType(final AgreementRoleType agreementRoleType, final LocalDate date) {
        return agreementRoleRepository.findByAgreementAndTypeAndContainsDate(this, agreementRoleType, date);
    }

    @Programmatic
    public AgreementRole findRoleWithType(final IAgreementRoleType IAgreementRoleType, final LocalDate date) {
        return findRoleWithType(agreementRoleTypeRepository.find(IAgreementRoleType), date);
    }

    @Inject
    public AgreementRepository agreementRepository;

    @Inject
    public AgreementRoleRepository agreementRoleRepository;

    @Inject
    public AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    public AgreementTypeRepository agreementTypeRepository;

}
