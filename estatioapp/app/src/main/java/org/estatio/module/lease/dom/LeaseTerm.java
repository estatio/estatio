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
package org.estatio.module.lease.dom;

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
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.VersionStrategy;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
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

import org.incode.module.base.dom.Chained;
import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.base.dom.with.WithIntervalMutable;
import org.incode.module.base.dom.with.WithSequence;

import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyPropertyLocal;
import org.estatio.module.invoice.dom.InvoiceSource;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationService;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationService.CalculationResult;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"     // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.VALUE_MAP,
        column = "discriminator",
        value = "org.estatio.dom.lease.LeaseTerm"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Uniques({
        @Unique(
                name = "LeaseTerm_leaseItem_startDate_sequence_UNQ",
                members = {"leaseItem", "startDate", "sequence"}) })
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
                        + "FROM org.estatio.module.lease.dom.LeaseTerm "
                        + "WHERE leaseItem == :leaseItem"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseItemAndSequence", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.LeaseTerm "
                        + "WHERE leaseItem == :leaseItem "
                        + "   && sequence == :sequence"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseItemAndStartDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.LeaseTerm "
                        + "WHERE leaseItem == :leaseItem "
                        + "   && startDate == :startDate"),
        @javax.jdo.annotations.Query(
                name = "findByPropertyAndTypeAndStartDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.LeaseTerm "
                        + "WHERE leaseItem.type == :leaseItemType "
                        + "   && startDate == :startDate "
                        + "   && leaseItem.lease.occupancies.contains(lu) "
                        + "   && (lu.unit.property == :property) "
                        + "VARIABLES org.estatio.module.lease.dom.occupancy.Occupancy lu"),
        @javax.jdo.annotations.Query(
                name = "findByPropertyAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.LeaseTerm "
                        + "WHERE leaseItem.type == :leaseItemType "
                        + "   && leaseItem.lease.occupancies.contains(lu) "
                        + "   && (lu.unit.property == :property) "
                        + "VARIABLES org.estatio.module.lease.dom.occupancy.Occupancy lu"),
        @javax.jdo.annotations.Query(
                name = "findStartDatesByPropertyAndType", language = "JDOQL",
                value = "SELECT DISTINCT startDate "
                        + "FROM org.estatio.module.lease.dom.LeaseTerm "
                        + "WHERE leaseItem.type == :leaseItemType "
                        + "   && leaseItem.lease.occupancies.contains(lu) "
                        + "   && (lu.unit.property == :property) "
                        + "VARIABLES org.estatio.module.lease.dom.occupancy.Occupancy lu "
                        + "ORDER BY startDate"),
        @javax.jdo.annotations.Query(
                name = "findByStatusAndActiveDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.LeaseTerm "
                        + "WHERE status == :status "
                        + "&& startDate <= :date "
                        + "&& (endDate == null || endDate > :date )")
})
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
@DomainObject()
public abstract class LeaseTerm
        extends UdoDomainObject2<LeaseTerm>
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
            hidden = Where.PARENTED_TABLES,
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getLeaseItem().getApplicationTenancy();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    @javax.jdo.annotations.Column(name = "leaseItemId", allowsNull = "false")
    @Property(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private LeaseItem leaseItem;

    // //////////////////////////////////////

    @Property(hidden = Where.EVERYWHERE, optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private BigInteger sequence;

    // //////////////////////////////////////

    @Property()
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


    @Property(optionality = Optionality.OPTIONAL)
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
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public LeaseTerm changeDates(
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        modifyStartDate(startDate);
        modifyEndDate(endDate);
        return this;
    }

    public String disableChangeDates() {
        if (getStatus().isApproved()){
            return "Cannot change dates when term is approved";
        }
        if (!getInvoiceItems().isEmpty()){
            if (valueType() == LeaseTermValueType.FIXED) {
                return "Cannot change dates when used in invoice and value type is fixed";
            }
            if(!EstatioRole.ADMINISTRATOR.isApplicableFor(getUser())){
                return "Cannot change dates when used in invoice and user does not have administor role";
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

    @javax.jdo.annotations.Column(allowsNull = "false", length = LeaseTermStatus.Meta.MAX_LEN)
    @Getter @Setter
    private LeaseTermStatus status;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = LeaseTermFrequency.Meta.MAX_LEN)
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

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public LeaseTerm split(
            final LocalDate splitDate) {

        final LeaseTerm currentPrevous = this.getPrevious();
        final LeaseTerm currentNext = this.getNext();

        //decouple current next
        this.setNext(null);

        //create a new term
        final LocalDate endDate = this.getEndDate();
        final LeaseTerm newTerm = leaseTermRepository.newLeaseTerm(getLeaseItem(), this, splitDate, endDate);
        newTerm.setNext(currentNext);
        this.copyValuesTo(newTerm);
        newTerm.setStartDate(splitDate); // copy values overwrites these so need to set them again
        newTerm.setEndDate(endDate); // copy values overwrites these so need to set them again

        //fixup current
        this.setEndDate(LocalDateInterval.endDateFromStartDate(splitDate));

        return newTerm;
    }

    public LocalDate default0Split(){
        return this.nextStartDate();
    }

    public String validateSplit(
            final LocalDate startDate) {
        if (!startDate.isAfter(this.getStartDate())){
            return "Start date must be after term start date";
        }
        if (this.getEndDate() != null && !startDate.isBefore(this.getEndDate())){
            return "Start date must be before term end date";
        }
        return null;

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

    public boolean hideCreateNext() {
        return !getLeaseItem().getType().autoCreateTerms();
    }

    public String disableCreateNext() {
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

    public boolean hideChangeStatus() {
        return !EstatioRole.ADMINISTRATOR.isApplicableFor(getUser());
    }

    // //////////////////////////////////////

    @Programmatic
    public abstract BigDecimal valueForDate(final LocalDate dueDate);

    // //////////////////////////////////////

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public String showCalculationResults() {
        return StringUtils.join(calculationResults(
                getEffectiveInterval()
                ),
                "\t");
    }

    // //////////////////////////////////////

    @Programmatic
    public List<CalculationResult> calculationResults(
            final LocalDateInterval interval
    ) {
        return invoiceCalculationService.calculateDateRange(this, interval);
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
