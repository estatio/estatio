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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Bulk;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.Chained;
import org.estatio.dom.EstatioMutableObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.WithSequence;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.lease.Leases.InvoiceRunType;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoiceItemsForLease;
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
                name = "findByLeaseItemAndStartDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseTerm "
                        + "WHERE leaseItem == :leaseItem "
                        + "   && startDate == :startDate")
})
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public abstract class LeaseTerm
        extends EstatioMutableObject<LeaseTerm>
        implements WithIntervalMutable<LeaseTerm>, Chained<LeaseTerm>, WithSequence {

    public LeaseTerm() {
        // TODO: the integration tests fail if this is made DESCending.
        super("leaseItem, sequence, startDate");
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LeaseItem leaseItem;

    @javax.jdo.annotations.Column(name = "leaseItemId", allowsNull = "false")
    @Hidden(where = Where.REFERENCES_PARENT)
    @Disabled
    @Title(sequence = "1", append = ":")
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

    @Title(sequence = "2", append = "-")
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
        LocalDate currentStartDate = getStartDate();
        if (startDate != null && !startDate.equals(currentStartDate)) {
            setStartDate(startDate);
        }
        if (getPrevious() != null) {
            getPrevious().modifyEndDate(getInterval().endDateFromStartDate());
        }
    }

    public void clearStartDate() {
        LocalDate currentStartDate = getStartDate();
        if (currentStartDate == null) {
            return;
        }
        setStartDate(null);
        // TODO: shouldn't there be some logic reciprocal to that in
        // modifyStartDate ?
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @Title(sequence = "3")
    @Disabled
    @Optional
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    public void modifyEndDate(final LocalDate endDate) {
        LocalDate currentEndDate = getEndDate();
        if (endDate == null && currentEndDate == null || endDate.equals(currentEndDate)) {
            return;
        }
        setEndDate(endDate);
    }

    public void clearEndDate() {
        LocalDate currentEndDate = getEndDate();
        if (currentEndDate == null) {
            return;
        }
        setEndDate(null);
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

    @Programmatic
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
    public boolean isActiveOn(final LocalDate localDate) {
        LocalDateInterval effectiveInterval = getEffectiveInterval();
        if (effectiveInterval == null) {
            return false;
        }
        return effectiveInterval.contains(localDate);
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

    public void modifyPrevious(final LeaseTerm previous) {
        LeaseTerm currentPrevious = getPrevious();
        if (previous == null || previous.equals(currentPrevious)) {
            return;
        }
        clearPrevious();
        previous.setNext(this);
        setPrevious(previous);
    }

    public void clearPrevious() {
        LeaseTerm currentPrevious = getPrevious();
        if (currentPrevious == null) {
            return;
        }
        currentPrevious.setNext(null);
        setPrevious(null);
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

    public void modifyNext(final LeaseTerm next) {
        LeaseTerm currentNext = getNext();
        if (next == null || next.equals(currentNext)) {
            return;
        }
        if (currentNext != null) {
            currentNext.clearPrevious();
        }
        next.modifyPrevious(this);
    }

    public void clearNext() {
        LeaseTerm currentNext = getNext();
        if (currentNext == null) {
            return;
        }
        currentNext.clearPrevious();
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

    public void addToInvoiceItems(final InvoiceItemForLease invoiceItem) {
        if (invoiceItem == null || getInvoiceItems().contains(invoiceItem)) {
            return;
        }
        invoiceItem.clearLeaseTerm();
        invoiceItem.setLeaseTerm(this);
        getInvoiceItems().add(invoiceItem);
    }

    public void removeFromInvoiceItems(final InvoiceItemForLease invoiceItem) {
        if (invoiceItem == null || !getInvoiceItems().contains(invoiceItem)) {
            return;
        }
        invoiceItem.setLeaseTerm(null);
        getInvoiceItems().remove(invoiceItem);
    }

    // //////////////////////////////////////

    @Programmatic
    @Prototype
    public void remove() {
        if (getNext() != null) {
            getNext().remove();
        }
        if (this.getInvoiceItems().size() == 0) {
            this.modifyPrevious(null);
            getContainer().remove(this);
        }
    }

    @Programmatic
    public void removeUnapprovedInvoiceItemsForDate(final LocalDate startDate, final LocalDate dueDate) {
        for (InvoiceItemForLease invoiceItem : getInvoiceItems()) {
            Invoice invoice = invoiceItem.getInvoice();
            if ((invoice == null || invoice.getStatus().equals(InvoiceStatus.NEW)) &&
                    startDate.equals(invoiceItem.getStartDate()) &&
                    dueDate.equals(invoiceItem.getDueDate())) {
                invoiceItem.setInvoice(null);
                invoiceItem.clearLeaseTerm();
                getContainer().flush();
                remove(invoiceItem);
            }
        }
    }

    @Programmatic
    public InvoiceItemForLease findOrCreateUnapprovedInvoiceItemFor(
            final LocalDateInterval invoiceInterval,
            final LocalDate dueDate) {
        InvoiceItemForLease ii = findUnapprovedInvoiceItemFor(invoiceInterval, dueDate);
        if (ii == null) {
            ii = invoiceItemsForLease.newInvoiceItem(this, invoiceInterval, dueDate);
        }
        return ii;
    }

    @Programmatic
    public InvoiceItemForLease findUnapprovedInvoiceItemFor(
            final LocalDateInterval invoiceInterval,
            final LocalDate dueDate) {

        List<InvoiceItemForLease> invoiceItems =
                invoiceItemsForLease.findByLeaseTermAndIntervalAndDueDateAndStatus(
                        this, invoiceInterval, dueDate, InvoiceStatus.NEW);
        if (invoiceItems.size() > 0) {
            // TODO: what should we do when we find more then one. Throw an
            // error?
            return invoiceItems.get(0);
        }
        return null;
    }

    @Programmatic
    public BigDecimal invoicedValueFor(final LocalDate startDate) {
        BigDecimal invoicedValue = new BigDecimal(0);
        for (InvoiceItemForLease invoiceItem : getInvoiceItems()) {
            Invoice invoice = invoiceItem.getInvoice();
            if (invoice == null || invoice.getStatus() == InvoiceStatus.NEW ||
                    invoiceItem.getStartDate() == null ||
                    invoiceItem.getStartDate().compareTo(startDate) != 0) {
                continue;
            }
            invoicedValue = invoicedValue.add(invoiceItem.getNetAmount());
        }
        return invoicedValue;
    }

    // //////////////////////////////////////

    @Programmatic
    public LeaseTerm calculate(
            final @Named("Period Start Date") LocalDate startDate,
            final @Named("Due Date") LocalDate dueDate) {
        return calculate(startDate, startDate, dueDate, InvoiceRunType.NORMAL_RUN);
    }

    public LeaseTerm calculate(
            final @Named("Period start Date") LocalDate startDate,
            final @Named("Period end Date") @Optional LocalDate endDate,
            final @Named("Due Date") LocalDate dueDate,
            final @Named("Run Type") InvoiceRunType runType) {
        if (!getLeaseItem().getStatus().equals(LeaseItemStatus.SUSPENDED)) {
            invoiceCalculationService.calculateAndInvoice(
                    this, startDate, endDate, dueDate, getLeaseItem().getInvoicingFrequency(), runType);
        }
        return this;
    }

    // //////////////////////////////////////

    @Bulk
    public LeaseTerm verify() {
        verifyUntil(getClockService().now());
        return this;
    }

    @Programmatic
    public void verifyUntil(final LocalDate date) {
        update();
        // convenience code to automatically create terms but not for terms who
        // have a start date after today
        LeaseTerm nextTerm = getNext();
        LocalDate nextStartDate = getNextStartDate();
        if (nextTerm == null && nextStartDate.compareTo(date) <= 0) {
            nextTerm = createNext(getNextStartDate());
        }
        if (nextTerm != null) {
            nextTerm.verifyUntil(date);
        }
    }

    private LocalDate getNextStartDate() {
        LocalDate nextStartDate = getInterval().endDateExcluding();
        if (nextStartDate == null) {
            return getFrequency().nextDate(getStartDate());
        }
        return nextStartDate;
    }

    // //////////////////////////////////////

    public LeaseTerm createNext(
            final @Named("Start date") LocalDate nextStartDate) {
        LeaseTerm nextTerm = getNext();
        if (nextTerm != null) {
            return nextTerm;
        }
        nextTerm = terms.newLeaseTerm(getLeaseItem(), this, nextStartDate);
        nextTerm.initialize();
        nextTerm.modifyStartDate(nextStartDate);
        nextTerm.update();
        return nextTerm;
    }

    // //////////////////////////////////////

    protected void initialize() {
        setStatus(LeaseTermStatus.NEW);
        LeaseTerm previousTerm = getPrevious();
        BigInteger sequence = BigInteger.ONE;
        if (previousTerm != null) {
            sequence = previousTerm.getSequence().add(BigInteger.ONE);
            setFrequency(previousTerm.getFrequency());
        }
        setSequence(sequence);
    }

    @Programmatic
    protected void update() {
        // Get the end date from the next start date
        if (getEndDate() == null && getNext() != null) {
            modifyEndDate(getNext().getInterval().endDateFromStartDate());
        }

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
    public BigDecimal valueForDate(final LocalDate dueDate) {
        return getEffectiveValue();
    }

    @Programmatic
    BigDecimal valueForPeriod(
            final LocalDate periodStartDate,
            final LocalDate dueDate,
            final InvoicingFrequency frequency) {
        return invoiceCalculationService.calculateSumForAllPeriods(this, periodStartDate, dueDate, frequency);
    }

    // //////////////////////////////////////

    @Override
    public String toString() {
        return getInterval().toString() + " / ";
    }

    // //////////////////////////////////////

    private InvoiceItemsForLease invoiceItemsForLease;

    public final void injectInvoiceItemsForLease(final InvoiceItemsForLease invoiceItemsForLease) {
        this.invoiceItemsForLease = invoiceItemsForLease;
    }

    private InvoiceCalculationService invoiceCalculationService;

    public final void injectInvoiceCalculationService(final InvoiceCalculationService invoiceCalculationService) {
        this.invoiceCalculationService = invoiceCalculationService;
    }

    private LeaseTerms terms;

    public final void injectLeaseTerms(final LeaseTerms terms) {
        this.terms = terms;
    }

}
