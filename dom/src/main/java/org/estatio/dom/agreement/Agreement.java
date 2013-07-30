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
package org.estatio.dom.agreement;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
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
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.Lockable;
import org.estatio.dom.Status;
import org.estatio.dom.WithInterval;
import org.estatio.dom.Chained;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.WithNameGetter;
import org.estatio.dom.WithReferenceComparable;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.ValueUtils;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT FROM org.estatio.dom.agreement.Agreement WHERE reference.matches(:reference)"),
        @javax.jdo.annotations.Query(
                name = "findByAgreementTypeAndRoleTypeAndParty", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.agreement.Agreement " +
                        "WHERE agreementType == :agreementType" +
                        " && roles.contains(role)" +
                        " && role.type == :roleType" +
                        " && role.party == :party" +
                        " VARIABLES org.estatio.dom.agreement.AgreementRole role")
})
@Bookmarkable
public abstract class Agreement<S extends Lockable> extends EstatioTransactionalObject<Agreement<S>, S> implements WithReferenceComparable<Agreement<S>>, WithIntervalMutable<Agreement<S>>, Chained<Agreement<S>>, WithNameGetter {

    public Agreement(S statusToLock, S statusToUnlock) {
        super("reference", statusToLock, statusToUnlock);
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Unique(name = "AGREEMENT_REFERENCE_UNIQUE_IDX")
    private String reference;

    @DescribedAs("Unique reference code for this agreement")
    @Title
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    public String disableReference() {
        return getStatus().isLocked() ? "Cannot modify when locked" : null;
    }

    // //////////////////////////////////////

    private String name;

    @DescribedAs("Optional name for this agreement")
    @Hidden(where = Where.ALL_TABLES)
    @Optional
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String disableName() {
        return getStatus().isLocked() ? "Cannot modify when locked" : null;
    }

    // //////////////////////////////////////

    public abstract Party getPrimaryParty();

    public abstract Party getSecondaryParty();

    // //////////////////////////////////////

    protected Party findParty(final String agreementRoleTypeTitle) {
        final AgreementRoleType art = agreementRoleTypes.findByTitle(agreementRoleTypeTitle);
        return findParty(art);
    }

    protected Party findParty(AgreementRoleType agreementRoleType) {
        final Predicate<AgreementRole> currentAgreementRoleOfType = currentAgreementRoleOfType(agreementRoleType);
        final Iterable<Party> parties = Iterables.transform(Iterables.filter(getRoles(), currentAgreementRoleOfType), partyOfAgreementRole());
        return ValueUtils.firstElseNull(parties);
    }

    private static Function<AgreementRole, Party> partyOfAgreementRole() {
        return new Function<AgreementRole, Party>() {
            public Party apply(AgreementRole agreementRole) {
                return agreementRole != null ? agreementRole.getParty() : null;
            }
        };
    }

    private static Predicate<AgreementRole> currentAgreementRoleOfType(final AgreementRoleType art) {
        return new Predicate<AgreementRole>() {
            public boolean apply(AgreementRole candidate) {
                return candidate != null ? candidate.getType() == art && candidate.isCurrent() : false;
            }
        };
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
    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // //////////////////////////////////////

    @Hidden
    @Override
    public WithInterval<?> getWithIntervalParent() {
        return null;
    }

    @Hidden
    @Override
    public LocalDate getEffectiveStartDate() {
        return WithInterval.Util.effectiveStartDateOf(this);
    }

    @Hidden
    @Override
    public LocalDate getEffectiveEndDate() {
        return WithInterval.Util.effectiveEndDateOf(this);
    }

    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    // //////////////////////////////////////

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(LocalDate localDate) {
        return getInterval().contains(localDate);
    }

    // //////////////////////////////////////

    private WithIntervalMutable.ChangeDates<Agreement<S>> changeDates = new WithIntervalMutable.ChangeDates<Agreement<S>>(this);

    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    public Agreement<S> changeDates(
            final @Named("Start Date") @Optional LocalDate startDate,
            final @Named("End Date") @Optional LocalDate endDate) {
        return changeDates.changeDates(startDate, endDate);
    }

    public String disableChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return getStatus().isLocked() ? "Cannot modify when locked" : null;
    }

    @Override
    public LocalDate default0ChangeDates() {
        return changeDates.default0ChangeDates();
    }

    @Override
    public LocalDate default1ChangeDates() {
        return changeDates.default1ChangeDates();
    }

    @Override
    public String validateChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return changeDates.validateChangeDates(startDate, endDate);
    }

    // //////////////////////////////////////

    @Programmatic
    public LocalDateInterval getEffectiveInterval() {
        return LocalDateInterval.including(getStartDate(), getTerminationDate());
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate terminationDate;

    @Optional
    @Disabled
    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(final LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "AGREEMENTTYPE_ID")
    private AgreementType agreementType;

    @Hidden(where = Where.ALL_TABLES)
    @Disabled
    public AgreementType getAgreementType() {
        return agreementType;
    }

    public void setAgreementType(final AgreementType type) {
        this.agreementType = type;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "PREVIOUS_ID")
    @javax.jdo.annotations.Persistent(mappedBy = "next")
    private Agreement<S> previous;

    @Named("Previous Agreement")
    @Hidden(where = Where.ALL_TABLES)
    @Disabled
    @Optional
    @Override
    public Agreement<S> getPrevious() {
        return previous;
    }

    public void setPrevious(final Agreement<S> previous) {
        this.previous = previous;
    }

    public void modifyPrevious(final Agreement<S> previous) {
        Agreement<S> currentPrevious = getPrevious();
        // check for no-op
        if (previous == null || previous.equals(currentPrevious)) {
            return;
        }
        // dissociate existing
        clearPrevious();
        // associate new
        previous.setNext(this);
        setPrevious(previous);
    }

    public void clearPrevious() {
        Agreement<S> currentPreviousAgreement = getPrevious();
        // check for no-op
        if (currentPreviousAgreement == null) {
            return;
        }
        // dissociate existing
        currentPreviousAgreement.setNext(null);
        setPrevious(null);
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "NEXT_ID")
    private Agreement<S> next;

    @Named("Next Agreement")
    @Hidden(where = Where.ALL_TABLES)
    @Disabled
    @Optional
    @Override
    public Agreement<S> getNext() {
        return next;
    }

    public void setNext(final Agreement<S> next) {
        this.next = next;
    }

    public void modifyNext(final Agreement<S> next) {
        Agreement<S> currentNext = getNext();
        // check for no-op
        if (next == null || next.equals(currentNext)) {
            return;
        }
        // delegate to parent(s) to (re-)associate
        if (currentNext != null) {
            currentNext.clearPrevious();
        }
        next.modifyPrevious(this);
    }

    public void clearNext() {
        Agreement<S> currentNext = getNext();
        // check for no-op
        if (currentNext == null) {
            return;
        }
        // delegate to parent to dissociate
        currentNext.clearPrevious();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "agreement")
    private SortedSet<AgreementRole> roles = new TreeSet<AgreementRole>();

    @Disabled
    @Render(Type.EAGERLY)
    public SortedSet<AgreementRole> getRoles() {
        return roles;
    }

    public void setRoles(final SortedSet<AgreementRole> actors) {
        this.roles = actors;
    }

    @Named("Create Initial")
    public Agreement<S> createInitialRole(
            final @Named("Type") AgreementRoleType type,
            final Party party,
            final @Named("Start date") @Optional LocalDate startDate,
            final @Named("End date") @Optional LocalDate endDate) {
        createRole(type, party, startDate, endDate);
        return this;
    }
    public String validateCreateInitialRole(
            final AgreementRoleType type,
            final Party party,
            final LocalDate startDate,
            final LocalDate endDate) {
        if(startDate != null && endDate != null && startDate.equals(endDate)) {
            return "End date must be after start date";
        }
        if(!Sets.filter(getRoles(), type.matchingRole()).isEmpty()) {
            return "Add a successor/predecessor to existing agreement role";
        }
        return null;
    }

    public List<AgreementRoleType> choices0CreateInitialRole() {
        return agreementRoleTypes.findApplicableTo(getAgreementType());
    }
    
    public LocalDate default2CreateInitialRole() {
        return getEffectiveStartDate();
    }
    
    public LocalDate default3CreateInitialRole() {
        return getEffectiveEndDate();
    }

    /**
     * Provided for BDD "glue"; delegated to by {@link #createInitialRole(AgreementRoleType, Party, LocalDate, LocalDate)}.
     */
    @Programmatic
    public AgreementRole createRole(final AgreementRoleType type, final Party party, final LocalDate startDate, final LocalDate endDate) {
        final AgreementRole role = newTransientInstance(AgreementRole.class);
        role.setStartDate(startDate);
        role.setEndDate(endDate);
        role.setType(type); // must do before associate with agreement, since part of AgreementRole#compareTo impl.

        role.setStatus(Status.UNLOCKED);
        
        // JDO will manage the relationship for us
        // see http://markmail.org/thread/b6lpzktr6hzysisp, Dan's email 2013-7-17
        role.setParty(party);
        role.setAgreement(this);
        
        persistIfNotAlready(role);
        
        return role;
    }

    
    // //////////////////////////////////////

    @Programmatic
    AgreementRole findRole(Party party, AgreementRoleType type, LocalDate startDate) {
        return agreementRoles.findByAgreementAndPartyAndTypeAndStartDate(this, party, type, startDate);
    }

    @Programmatic
    public AgreementRole findRoleWithType(AgreementRoleType agreementRoleType, LocalDate date) {
        return agreementRoles.findByAgreementAndTypeAndContainsDate(this, agreementRoleType, date);
    }

    // //////////////////////////////////////

    protected Agreements agreements;

    public void injectAgreements(Agreements agreements) {
        this.agreements = agreements;
    }

    protected AgreementRoles agreementRoles;

    public void injectAgreementRoles(final AgreementRoles agreementRoles) {
        this.agreementRoles = agreementRoles;
    }

    protected AgreementRoleTypes agreementRoleTypes;

    public void injectAgreementRoleTypes(final AgreementRoleTypes agreementRoleTypes) {
        this.agreementRoleTypes = agreementRoleTypes;
    }

    protected AgreementTypes agreementTypes;

    public void injectAgreementTypes(AgreementTypes agreementTypes) {
        this.agreementTypes = agreementTypes;
    }

}
