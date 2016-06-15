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
package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.app.security.EstatioRole;
import org.estatio.dom.Chained;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.WithSequence;
import org.estatio.dom.apptenancy.WithApplicationTenancyPropertyLocal;
import org.estatio.dom.invoice.InvoiceSource;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService.CalculationResult;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.utils.TitleBuilder;
import org.estatio.dom.valuetypes.LocalDateInterval;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME,
        column = "discriminator")
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "LeaseTerm_leaseItem_sequence_IDX",
                members = {"leaseItem", "sequence"}),
        @javax.jdo.annotations.Index(
                name = "LeaseTerm_leaseItem_startDate_IDX",
                members = {"leaseItem", "startDate"})
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByLeaseItem", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseTerm "
                        + "WHERE leaseItem == :leaseItem"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseItemAndSequence", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseTerm "
                        + "WHERE leaseItem == :leaseItem "
                        + "   && sequence == :sequence"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseItemAndStartDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseTerm "
                        + "WHERE leaseItem == :leaseItem "
                        + "   && startDate == :startDate"),
        @javax.jdo.annotations.Query(
                name = "findByPropertyAndTypeAndStartDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseTerm "
                        + "WHERE leaseItem.type == :leaseItemType "
                        + "   && startDate == :startDate "
                        + "   && leaseItem.lease.occupancies.contains(lu) "
                        + "   && (lu.unit.property == :property) "
                        + "VARIABLES org.estatio.dom.lease.Occupancy lu"),
        @javax.jdo.annotations.Query(
                name = "findStartDatesByPropertyAndType", language = "JDOQL",
                value = "SELECT DISTINCT startDate "
                        + "FROM org.estatio.dom.lease.LeaseTerm "
                        + "WHERE leaseItem.type == :leaseItemType "
                        + "   && leaseItem.lease.occupancies.contains(lu) "
                        + "   && (lu.unit.property == :property) "
                        + "VARIABLES org.estatio.dom.lease.Occupancy lu "
                        + "ORDER BY startDate"),
        @javax.jdo.annotations.Query(
                name = "findByStatusAndActiveDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseTerm "
                        + "WHERE status == :status "
                        + "&& startDate <= :date "
                        + "&& (endDate == null || endDate > :date )")
})
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
@DomainObject(editing = Editing.DISABLED)
public abstract class LeaseTerm
        extends EstatioDomainObject<LeaseTerm>
        implements WithIntervalMutable<LeaseTerm>, Chained<LeaseTerm>, WithSequence, InvoiceSource, WithApplicationTenancyPropertyLocal {

    public LeaseTerm() {
        // TODO: the integration tests fail if this is made DESCending.
        super("leaseItem, sequence, startDate");
    }

    // //////////////////////////////////////

    public String title() {
        return TitleBuilder.start()
                .withParent(getLeaseItem())
                .withName(getInterval())
                .toString();
    }

    // //////////////////////////////////////

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getLeaseItem().getApplicationTenancy();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    @javax.jdo.annotations.Column(name = "leaseItemId", allowsNull = "false")
    @Property(hidden = Where.REFERENCES_PARENT, editing = Editing.DISABLED)
    @Getter @Setter
    private LeaseItem leaseItem;

    // //////////////////////////////////////

    @Property(hidden = Where.EVERYWHERE, optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private BigInteger sequence;

    // //////////////////////////////////////

    @Property(editing = Editing.DISABLED)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate startDate;

    public void modifyStartDate(final LocalDate newStartDate) {
        if (ObjectUtils.notEqual(getStartDate(), newStartDate)) {
            setStartDate(newStartDate);
            if (getPrevious() != null) {
                getPrevious().align();
            }
        }
    }

    // //////////////////////////////////////


    @Property(optionality = Optionality.OPTIONAL, editing = Editing.DISABLED)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate endDate;

    public void modifyEndDate(final LocalDate newEndDate) {
        if (ObjectUtils.notEqual(getEndDate(), newEndDate)) {
            setEndDate(newEndDate);
        }
    }

    protected boolean allowOpenEndDate(){
        return getLeaseItem().getType().allowOpenEndDate();
    }

    // //////////////////////////////////////

    private WithIntervalMutable.Helper<LeaseTerm> changeDates = new WithIntervalMutable.Helper<>(this);

    WithIntervalMutable.Helper<LeaseTerm> getChangeDates() {
        return changeDates;
    }

    @Override
    @Action(semantics = SemanticsOf.IDEMPOTENT, hidden = Where.EVERYWHERE)
    public LeaseTerm changeDates(
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        modifyStartDate(startDate);
        modifyEndDate(endDate);
        return this;
    }

    public String disableChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        if (getStatus().isApproved()){
            return "Already approved";
        }
        if (valueType() == LeaseTermValueType.FIXED) {
            if (!getInvoiceItems().isEmpty()) {
                return "Cannot change dates because this lease term has invoices and is fixed";
            }
        }
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
            final LocalDate newStartDate,
            final LocalDate newEndDate) {
        String changeDatesReasonIfAny = getChangeDates().validateChangeDates(newStartDate, newEndDate);
        if (changeDatesReasonIfAny != null) {
            return changeDatesReasonIfAny;
        }
        if (getPrevious() != null && newStartDate.isBefore(getPrevious().getStartDate())) {
            return "New start date can't be before start date of previous term";
        }
        if (getNext() != null && ObjectUtils.notEqual(newEndDate, getEndDate())) {
            return "The end date of this term is set by the start date of the next term";
        }
        return null;
    }

    // //////////////////////////////////////

    @Property(hidden = Where.EVERYWHERE)
    @Override
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Programmatic
    public LocalDateInterval getEffectiveInterval() {
        return getInterval().overlap(getLeaseItem().getEffectiveInterval());
    }

    // //////////////////////////////////////

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    @Programmatic
    public boolean isActiveOn(final LocalDate date) {
        LocalDateInterval effectiveInterval = getEffectiveInterval();
        if (date == null || effectiveInterval == null || !effectiveInterval.isValid()) {
            return false;
        }
        return effectiveInterval.contains(date);
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.STATUS_ENUM)
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private LeaseTermStatus status;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.LEASE_TERM_FREQUENCY_ENUM)
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private LeaseTermFrequency frequency;

    // //////////////////////////////////////

    public BigDecimal getEffectiveValue() {
        return null;
    }

    // //////////////////////////////////////


    @Property(hidden = Where.ALL_TABLES, optionality = Optionality.OPTIONAL)
    @PropertyLayout(named = "Previous Term")
    @javax.jdo.annotations.Column(name = "previousLeaseTermId")
    @javax.jdo.annotations.Persistent(mappedBy = "next")
    @Getter @Setter
    private LeaseTerm previous;

    // //////////////////////////////////////

    @Property(hidden = Where.ALL_TABLES, optionality = Optionality.OPTIONAL)
    @PropertyLayout(named = "Next Term")
    @javax.jdo.annotations.Column(name = "nextLeaseTermId")
    @Getter @Setter
    private LeaseTerm next;

    // //////////////////////////////////////

    @Persistent(mappedBy = "leaseTerm")
    @CollectionLayout(render = RenderType.EAGERLY)
    @Getter @Setter
    private SortedSet<InvoiceItemForLease> invoiceItems = new TreeSet<>();

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Object remove() {
        LeaseItem item = getLeaseItem();
        if (doRemove()) {
            return item;
        }
        return this;
    }

    @Programmatic
    public boolean doRemove() {
        boolean success = true;
        if (getNext() != null) {
            success = getNext().doRemove();
        }
        success = getInvoiceItems().size() == 0;
        if (success) {
            if (getPrevious() != null) {
                getPrevious().setNext(null);
            }
            this.setPrevious(null);
            getContainer().remove(this);
            getContainer().flush();
        }
        return success;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.IDEMPOTENT, invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
    public LeaseTerm verify() {
        LocalDateInterval effectiveInterval = getLeaseItem().getEffectiveInterval();
        verifyUntil(ObjectUtils.min(effectiveInterval == null ? null : effectiveInterval.endDateExcluding(), getClockService().now()));
        return this;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public LeaseTerm verifyUntil(final LocalDate date) {
        LeaseTerm nextTerm = getNext();
        boolean autoCreateTerms = getLeaseItem().getType().autoCreateTerms();
        if (autoCreateTerms) {
            // Remove items after the period
            LocalDateInterval effectiveInterval = getLeaseItem().getEffectiveInterval();
            LocalDate endDateExcluding = effectiveInterval != null ? effectiveInterval.endDateExcluding() : date;
            if (getNext() != null && endDateExcluding != null && getNext().getStartDate().compareTo(endDateExcluding) >= 0) {
                getNext().doRemove();
                return this;
            }
        }
        align();
        if (autoCreateTerms) {
            // convenience code to automatically create terms but not for terms
            // who have a start date after today
            LocalDateInterval effectiveInterval = getLeaseItem().getEffectiveInterval();
            LocalDate minDate = ObjectUtils.min(effectiveInterval == null ? null : effectiveInterval.endDateExcluding(), date);
            LocalDate nextStartDate = nextStartDate();
            if (nextTerm == null && nextStartDate.compareTo(minDate) < 0) {
                LocalDate nextstartDate = default0CreateNext(null, null);
                LocalDate nextEndDate = default1CreateNext(null, null);
                nextTerm = createNext(nextstartDate, nextEndDate);
            }
        }
        if (nextTerm != null) {
            nextTerm.verifyUntil(date);
        }
        return this;
    }

    protected LocalDate nextStartDate() {
        LocalDate nextStartDate = getInterval().endDateExcluding();
        if (nextStartDate == null) {
            return getFrequency().nextDate(getStartDate());
        }
        return nextStartDate;
    }

    // //////////////////////////////////////

    public LeaseTerm createNext(
            final LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        LeaseTerm nextTerm = getNext();
        if (nextTerm != null) {
            return nextTerm;
        }
        nextTerm = leaseTermRepository.newLeaseTerm(getLeaseItem(), this, startDate, endDate);
        return nextTerm;
    }

    public boolean hideCreateNext(
            final LocalDate nextStartDate,
            final LocalDate nextEndDate) {
        return !getLeaseItem().getType().autoCreateTerms();
    }

    public String disableCreateNext(
            final LocalDate nextStartDate,
            final LocalDate nextEndDate) {
        return getNext() == null ? null : "Already a next term available";
    }

    public String validateCreateNext(
            final LocalDate nextStartDate,
            final LocalDate nextEndDate) {
        return leaseTermRepository.validateNewLeaseTerm(getLeaseItem(), this, nextStartDate, nextEndDate);
    }

    public LocalDate default0CreateNext(
            final LocalDate nextStartDate,
            final LocalDate nextEndDate) {
        return nextStartDate();
    }

    public LocalDate default1CreateNext(
            final LocalDate nextStartDate,
            final LocalDate nextEndDate) {
        LocalDate endDate = nextStartDate() != null ? getFrequency().nextDate(nextStartDate()) : null;
        return new LocalDateInterval(endDate, null).endDateFromStartDate();
    }

    // //////////////////////////////////////

    @Programmatic
    protected final void initialize() {
        setStatus(LeaseTermStatus.NEW);
        LeaseTerm previousTerm = getPrevious();
        BigInteger sequence = BigInteger.ONE;
        if (previousTerm != null) {
            sequence = previousTerm.getSequence().add(BigInteger.ONE);
            setFrequency(previousTerm.getFrequency());
        }
        setSequence(sequence);
        doInitialize();
    }

    /**
     * Optional hook for subclasses to do additional initialization.
     */
    @Programmatic
    protected void doInitialize() {
    }

    @Programmatic
    protected final void align() {
        // Get the end date from the next start date
        if (getNext() != null) {
            LocalDate endDate = getNext().getInterval().endDateFromStartDate();
            if (ObjectUtils.notEqual(getEndDate(), endDate)) {
                modifyEndDate(endDate);
            }
        }
        doAlign();
    }

    /**
     * Optional hook for subclasses to do additional alignment.
     */
    @Programmatic
    protected void doAlign() {
    }

    // //////////////////////////////////////

    /**
     * Be default all values are annual amounts.
     */
    @Programmatic
    public LeaseTermValueType valueType() {
        return LeaseTermValueType.ANNUAL;
    }

    // //////////////////////////////////////

    @Programmatic
    public void copyValuesTo(final LeaseTerm target) {
        target.setStartDate(getStartDate());
        target.setEndDate(getEndDate());
        target.setStatus(getStatus());
        target.setFrequency(getFrequency());
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.IDEMPOTENT, invokeOn = InvokeOn.OBJECT_AND_COLLECTION)
    public LeaseTerm approve() {
        if (!getStatus().isApproved()) {
            setStatus(LeaseTermStatus.APPROVED);
        }
        return this;
    }

    public String disableApprove() {
        return getStatus().equals(LeaseTermStatus.APPROVED) ? "Already approved" : null;
    }

    @MemberOrder(name = "status", sequence = "1")
    public LeaseTerm changeStatus(final LeaseTermStatus newStatus) {
        setStatus(newStatus);
        return this;
    }

    public boolean hideChangeStatus(final LeaseTermStatus newStatus) {
        return !EstatioRole.ADMINISTRATOR.isApplicableFor(getUser());
    }

    // //////////////////////////////////////

    @Programmatic
    public abstract BigDecimal valueForDate(final LocalDate dueDate);

    // //////////////////////////////////////

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public String showCalculationResults() {
        return StringUtils.join(calculationResults(
                getEffectiveInterval(),
                getStartDate().plusYears(2)),
                "\t");
    }

    // //////////////////////////////////////

    @Programmatic
    public List<CalculationResult> calculationResults(
            final LocalDateInterval interval,
            final LocalDate dueDate
    ) {
        return invoiceCalculationService.calculateDateRange(this, interval, dueDate);
    }

    // //////////////////////////////////////

    @Override
    public String toString() {
        return getInterval().toString() + " / ";
    }

    // //////////////////////////////////////

    @Inject
    private InvoiceCalculationService invoiceCalculationService;

    @Inject
    public LeaseTermRepository leaseTermRepository;

}
