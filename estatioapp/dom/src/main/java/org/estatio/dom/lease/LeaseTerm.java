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

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.TitleBuffer;

import org.estatio.dom.Chained;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.WithSequence;
import org.estatio.dom.invoice.InvoiceSource;
import org.estatio.dom.lease.invoicing.InvoiceCalculationParameters;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService.CalculationResult;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoiceRunType;
import org.estatio.dom.valuetypes.LocalDateInterval;

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
                members = { "leaseItem", "sequence" }),
        @javax.jdo.annotations.Index(
                name = "LeaseTerm_leaseItem_startDate_IDX",
                members = { "leaseItem", "startDate" })
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByLeaseItemAndSequence", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseTerm "
                        + "WHERE leaseItem == :leaseItem "
                        + "   && sequence == :sequence"),
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
                name = "findByPropertyAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseTerm "
                        + "WHERE leaseItem.type == :leaseItemType "
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
                name = "findByLeaseItemAndStartDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseTerm "
                        + "WHERE leaseItem == :leaseItem "
                        + "   && startDate == :startDate"),
        @javax.jdo.annotations.Query(
                name = "findByStatusAndActiveDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseTerm "
                        + "WHERE status == :status "
                        + "&& startDate <= :date "
                        + "&& (endDate == null || endDate > :date )"),
        @javax.jdo.annotations.Query(
                name = "findByInvalidInterval", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseTerm "
                        + "WHERE startDate > endDate")
})
@Bookmarkable(BookmarkPolicy.AS_CHILD)
@Immutable
public abstract class LeaseTerm
        extends EstatioDomainObject<LeaseTerm>
        implements WithIntervalMutable<LeaseTerm>, Chained<LeaseTerm>, WithSequence, InvoiceSource {

    public LeaseTerm() {
        // TODO: the integration tests fail if this is made DESCending.
        super("leaseItem, sequence, startDate");
    }

    // //////////////////////////////////////

    public String title() {
        TitleBuffer buffer = new TitleBuffer()
                .append(":", getContainer().titleOf(getLeaseItem()))
                .append(":", getInterval().toString("dd-MM-yyyy"));
        return buffer.toString();
    }

    // //////////////////////////////////////

    private LeaseItem leaseItem;

    @javax.jdo.annotations.Persistent
    @javax.jdo.annotations.Column(name = "leaseItemId", allowsNull = "false")
    @Hidden(where = Where.REFERENCES_PARENT)
    @Disabled
    public LeaseItem getLeaseItem() {
        return leaseItem;
    }

    public void setLeaseItem(final LeaseItem leaseItem) {
        this.leaseItem = leaseItem;
    }

    // //////////////////////////////////////

    private BigInteger sequence;

    @Hidden
    @Optional
    public BigInteger getSequence() {
        return sequence;
    }

    public void setSequence(final BigInteger sequence) {
        this.sequence = sequence;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate startDate;

    @Optional
    @Disabled
    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    public void modifyStartDate(final LocalDate startDate) {
        if (ObjectUtils.notEqual(getStartDate(), startDate)) {
            setStartDate(startDate);
            if (getPrevious() != null) {
                getPrevious().align();
            }
        }
    }

    // //////////////////////////////////////

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

    public void modifyEndDate(final LocalDate endDate) {
        if (ObjectUtils.notEqual(getEndDate(), endDate)) {
            setEndDate(endDate);
        }
    }

    // //////////////////////////////////////

    private WithIntervalMutable.Helper<LeaseTerm> changeDates = new WithIntervalMutable.Helper<LeaseTerm>(this);

    WithIntervalMutable.Helper<LeaseTerm> getChangeDates() {
        return changeDates;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    public LeaseTerm changeDates(
            final @Named("Start Date") @Optional LocalDate startDate,
            final @Named("End Date") @Optional LocalDate endDate) {
        getChangeDates().changeDates(startDate, endDate);

        // TODO: need to align the predecessor and successor
        // nb: only if contiguous semantics, eg for Rent, but not for Discount
        return this;
    }

    public String disableChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
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
            final LocalDate startDate,
            final LocalDate endDate) {
        String changeDatesReasonIfAny = getChangeDates().validateChangeDates(startDate, endDate);
        if (changeDatesReasonIfAny != null) {
            return changeDatesReasonIfAny;
        }

        // TODO: now check that the start date is not before the predecessor's
        // start date
        // ...

        // TODO: now check that the end date is not after the successor's end
        // date
        // ...

        return null;
    }

    // //////////////////////////////////////

    @Hidden
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

    private LeaseTermStatus status;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.STATUS_ENUM)
    @Disabled
    public LeaseTermStatus getStatus() {
        return status;
    }

    public void setStatus(final LeaseTermStatus status) {
        this.status = status;
    }

    public void created() {
        setStatus(LeaseTermStatus.NEW);
    }

    // //////////////////////////////////////

    private LeaseTermFrequency frequency;

    @Disabled
    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.LEASE_TERM_FREQUENCY_ENUM)
    public LeaseTermFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(final LeaseTermFrequency frequency) {
        this.frequency = frequency;
    }

    // //////////////////////////////////////

    public BigDecimal getEffectiveValue() {
        return null;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "previousLeaseTermId")
    @javax.jdo.annotations.Persistent(mappedBy = "next")
    private LeaseTerm previous;

    @Named("Previous Term")
    @Hidden(where = Where.ALL_TABLES)
    @Optional
    @Override
    public LeaseTerm getPrevious() {
        return previous;
    }

    public void setPrevious(final LeaseTerm previous) {
        this.previous = previous;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "nextLeaseTermId")
    private LeaseTerm next;

    @Hidden(where = Where.ALL_TABLES)
    @Named("Next Term")
    @Optional
    @Override
    public LeaseTerm getNext() {
        return next;
    }

    public void setNext(final LeaseTerm next) {
        this.next = next;
    }

    // //////////////////////////////////////

    @Persistent(mappedBy = "leaseTerm")
    private SortedSet<InvoiceItemForLease> invoiceItems = new TreeSet<InvoiceItemForLease>();

    @Render(Type.EAGERLY)
    public SortedSet<InvoiceItemForLease> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(final SortedSet<InvoiceItemForLease> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    public Object remove(@Named("Are you sure?") Boolean confirm) {
        LeaseItem item = getLeaseItem();
        if (confirm && doRemove()) {
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

    @Bulk
    @ActionSemantics(Of.IDEMPOTENT)
    public LeaseTerm verify() {
        LocalDateInterval effectiveInterval = getLeaseItem().getEffectiveInterval();
        verifyUntil(ObjectUtils.min(effectiveInterval == null ? null : effectiveInterval.endDateExcluding(), getClockService().now()));
        return this;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    public LeaseTerm verifyUntil(final LocalDate date) {
        LeaseTerm nextTerm = getNext();
        boolean autoCreateTerms = getLeaseItem().getType().autoCreateTerms();
        if (autoCreateTerms) {
            // Remove items after the period
            LocalDateInterval effectiveInterval = getLeaseItem().getEffectiveInterval();
            LocalDate endDateExcluding = effectiveInterval != null ? effectiveInterval.endDateExcluding() : date;
            if (getNext() != null && endDateExcluding != null && getNext().getStartDate().compareTo(endDateExcluding) >= 0) {
                getNext().doRemove();
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
            final @Named("Start date") LocalDate nextStartDate,
            final @Named("End date") @Optional LocalDate nextEndDate) {
        LeaseTerm nextTerm = getNext();
        if (nextTerm != null) {
            return nextTerm;
        }
        nextTerm = terms.newLeaseTerm(getLeaseItem(), this, nextStartDate, nextEndDate);
        nextTerm.initialize();
        align();
        nextTerm.align();
        return nextTerm;
    }

    public String disableCreateNext(
            final LocalDate nextStartDate,
            final LocalDate nextEndDate) {
        return getNext() == null ? null : "Already a next term available";
    }

    public String validateCreateNext(
            final LocalDate nextStartDate,
            final LocalDate nextEndDate) {
        return nextStartDate.isBefore(getStartDate()) ? "Cannot start before this start date" : null;
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
     * Optional hook for subclasses to do additional initialization.
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

    @Bulk
    @ActionSemantics(Of.IDEMPOTENT)
    public LeaseTerm approve() {
        if (!getStatus().isApproved()) {
            setStatus(LeaseTermStatus.APPROVED);
        }
        return this;
    }

    // //////////////////////////////////////

    @Programmatic
    public abstract BigDecimal valueForDate(final LocalDate dueDate);

    // //////////////////////////////////////

    @Prototype
    public String showCalculationResults() {
        return StringUtils.join(calculationResults(
                getLeaseItem().getInvoicingFrequency(),
                getStartDate(),
                getStartDate().plusYears(2)),
                "\t");
    }

    // //////////////////////////////////////

    @Programmatic
    public List<CalculationResult> calculationResults(
            final InvoicingFrequency invoicingFrequency,
            final LocalDate startDueDate,
            final LocalDate nextDueDate
            ) {
        return invoiceCalculationService.calculateDueDateRange(
                this,
                new InvoiceCalculationParameters(
                        InvoiceRunType.NORMAL_RUN,
                        startDueDate,
                        startDueDate,
                        nextDueDate));
    }

    // //////////////////////////////////////

    public String validate() {
        String validatedChangeDatesReason = getChangeDates().validateChangeDates(getStartDate(), getEndDate());
        if (validatedChangeDatesReason != null) {
            return validatedChangeDatesReason;
        }
        // other checks, if any...
        return null;
    }

    // //////////////////////////////////////

    @Override
    public String toString() {
        return getInterval().toString() + " / ";
    }

    // //////////////////////////////////////

    private InvoiceCalculationService invoiceCalculationService;

    public final void injectInvoiceCalculationService(final InvoiceCalculationService invoiceCalculationService) {
        this.invoiceCalculationService = invoiceCalculationService;
    }

    private LeaseTerms terms;

    public final void injectLeaseTerms(final LeaseTerms terms) {
        this.terms = terms;
    }

}
