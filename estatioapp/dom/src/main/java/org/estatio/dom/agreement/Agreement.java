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
package org.estatio.dom.agreement;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RegEx;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.Chained;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.WithInterval;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.WithNameGetter;
import org.estatio.dom.WithReferenceComparable;
import org.estatio.dom.WithReferenceUnique;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.StringUtils;
import org.estatio.dom.utils.ValueUtils;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME,
        column = "discriminator")
@javax.jdo.annotations.Indices({
        // to cover the 'findAssetsByReferenceOrName' query
        // both in this superclass and the subclasses
        @javax.jdo.annotations.Index(
                name = "Lease_reference_name_IDX", members = { "reference", "name" })
})
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "Agreement_reference_UNQ", members = { "reference" })
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.agreement.Agreement "
                        + "WHERE reference == :reference"),
        @javax.jdo.annotations.Query(
                name = "findByTypeAndReferenceOrName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.agreement.Agreement "
                        + "WHERE type == :agreementType "
                        + "&& (reference.matches(:regex) || reference.matches(:regex))"),
        @javax.jdo.annotations.Query(
                name = "findByAgreementTypeAndRoleTypeAndParty", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.agreement.Agreement "
                        + "WHERE type == :agreementType"
                        + " && roles.contains(role)"
                        + " && role.type == :roleType"
                        + " && role.party == :party"
                        + " VARIABLES org.estatio.dom.agreement.AgreementRole role")
})
@Bookmarkable
@Immutable
public abstract class Agreement
        extends EstatioDomainObject<Agreement>
        implements WithReferenceComparable<Agreement>,
        WithReferenceUnique,
        WithIntervalMutable<Agreement>, Chained<Agreement>,
        WithNameGetter {

    public Agreement() {
        super("reference");
    }

    // //////////////////////////////////////

    private String reference;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.REFERENCE)
    @DescribedAs("Unique reference code for this agreement")
    @Title
    @RegEx(validation = RegexValidation.REFERENCE, caseSensitive = true)
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    private String name;

    @javax.jdo.annotations.Column(length = JdoColumnLength.NAME)
    @DescribedAs("Optional name for this agreement")
    @Hidden(where = Where.ALL_TABLES)
    @Optional
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // //////////////////////////////////////

    public abstract Party getPrimaryParty();

    public abstract Party getSecondaryParty();

    // //////////////////////////////////////

    protected Party findCurrentOrMostRecentParty(final String agreementRoleTypeTitle) {
        final AgreementRoleType art = agreementRoleTypes.findByTitle(agreementRoleTypeTitle);
        return findCurrentOrMostRecentParty(art);
    }

    protected Party findCurrentOrMostRecentParty(final AgreementRoleType art) {
        final AgreementRole currentOrMostRecentRole = findCurrentOrMostRecentAgreementRole(art);
        return partyOf(currentOrMostRecentRole);
    }

    protected AgreementRole findCurrentOrMostRecentAgreementRole(final String agreementRoleTypeTitle) {
        final AgreementRoleType art = agreementRoleTypes.findByTitle(agreementRoleTypeTitle);
        return findCurrentOrMostRecentAgreementRole(art);
    }

    protected AgreementRole findCurrentOrMostRecentAgreementRole(final AgreementRoleType agreementRoleType) {
        // all available roles
        final Iterable<AgreementRole> rolesOfType =
                Iterables.filter(getRoles(), AgreementRole.Predicates.whetherTypeIs(agreementRoleType));

        // try to find the one that is current...
        Iterable<AgreementRole> roles =
                Iterables.filter(rolesOfType, WithInterval.Predicates.<AgreementRole> whetherCurrentIs(true));

        // ... else the most recently ended one
        if (Iterables.isEmpty(roles)) {
            final List<AgreementRole> rolesInList = Lists.newArrayList(rolesOfType);
            roles = orderRolesByEffectiveEndDateReverseNullsFirst().leastOf(rolesInList, 1);
        }

        // and return the party
        final AgreementRole currentOrMostRecentRole = ValueUtils.firstElseNull(roles);
        return currentOrMostRecentRole;
    }

    protected Party partyOf(final AgreementRole agreementRole) {
        return AgreementRole.Functions.partyOf().apply(agreementRole);
    }

    private static Ordering<AgreementRole> orderRolesByEffectiveEndDateReverseNullsFirst() {
        return Ordering.natural().onResultOf(AgreementRole.Functions.effectiveEndDateOf()).reverse().nullsFirst();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate startDate;

    @Disabled
    @Optional
    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @Disabled
    @Optional
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

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

    private WithIntervalMutable.Helper<Agreement> changeDates = new WithIntervalMutable.Helper<Agreement>(this);

    WithIntervalMutable.Helper<Agreement> getChangeDates() {
        return changeDates;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    public Agreement changeDates(
            final @Named("Start Date") @Optional LocalDate startDate,
            final @Named("End Date") @Optional LocalDate endDate) {
        return getChangeDates().changeDates(startDate, endDate);
    }

    public String disableChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
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

    private AgreementType type;

    @javax.jdo.annotations.Column(name = "typeId", allowsNull = "false")
    @Hidden
    @Disabled
    public AgreementType getType() {
        return type;
    }

    public void setType(final AgreementType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "previousAgreementId")
    @javax.jdo.annotations.Persistent(mappedBy = "next")
    private Agreement previous;

    @Named("Previous Agreement")
    @Hidden(where = Where.ALL_TABLES)
    @Disabled
    @Optional
    @Override
    public Agreement getPrevious() {
        return previous;
    }

    public void setPrevious(final Agreement previous) {
        this.previous = previous;
    }

    public Agreement changePrevious(
            final Agreement previousAgreement) {
        setPrevious(previousAgreement);
        return this;
    }

    public List<Agreement> autoComplete0ChangePrevious(final String searchPhrase) {
        return agreements.findByTypeAndReferenceOrName(getType(), StringUtils.wildcardToCaseInsensitiveRegex("*".concat(searchPhrase).concat("*")));
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "nextAgreementId")
    private Agreement next;

    @Named("Next Agreement")
    @Hidden(where = Where.ALL_TABLES)
    @Disabled
    @Optional
    @Override
    public Agreement getNext() {
        return next;
    }

    public void setNext(final Agreement next) {
        this.next = next;
    }

    // //////////////////////////////////////

    private SortedSet<AgreementRole> roles = new TreeSet<AgreementRole>();

    @javax.jdo.annotations.Persistent(mappedBy = "agreement", defaultFetchGroup = "true")
    @Disabled
    @Render(Type.EAGERLY)
    public SortedSet<AgreementRole> getRoles() {
        return roles;
    }

    public void setRoles(final SortedSet<AgreementRole> actors) {
        this.roles = actors;
    }

    public Agreement newRole(
            final @Named("Type") AgreementRoleType type,
            final Party party,
            final @Named("Start date") @Optional LocalDate startDate,
            final @Named("End date") @Optional LocalDate endDate) {
        createRole(type, party, startDate, endDate);
        return this;
    }

    public String validateNewRole(
            final AgreementRoleType type,
            final Party party,
            final LocalDate startDate,
            final LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return "End date cannot be earlier than start date";
        }
        if (!Sets.filter(getRoles(), type.matchingRole()).isEmpty()) {
            return "Add a successor/predecessor to existing agreement role";
        }
        return null;
    }

    public List<AgreementRoleType> choices0NewRole() {
        return agreementRoleTypes.findApplicableTo(getType());
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

    // //////////////////////////////////////

    @Programmatic
    public AgreementRole findRole(final Party party, final AgreementRoleType type, final LocalDate date) {
        return agreementRoles.findByAgreementAndPartyAndTypeAndContainsDate(this, party, type, date);
    }

    @Programmatic
    public AgreementRole findRoleWithType(final AgreementRoleType agreementRoleType, final LocalDate date) {
        return agreementRoles.findByAgreementAndTypeAndContainsDate(this, agreementRoleType, date);
    }

    // //////////////////////////////////////

    protected Agreements agreements;

    public final void injectAgreements(final Agreements agreements) {
        this.agreements = agreements;
    }

    protected AgreementRoles agreementRoles;

    public final void injectAgreementRoles(final AgreementRoles agreementRoles) {
        this.agreementRoles = agreementRoles;
    }

    protected AgreementRoleTypes agreementRoleTypes;

    public final void injectAgreementRoleTypes(final AgreementRoleTypes agreementRoleTypes) {
        this.agreementRoleTypes = agreementRoleTypes;
    }

    protected AgreementTypes agreementTypes;

    public final void injectAgreementTypes(final AgreementTypes agreementTypes) {
        this.agreementTypes = agreementTypes;
    }

}
