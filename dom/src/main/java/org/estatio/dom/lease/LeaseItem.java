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
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Paged;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioMutableObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.WithSequence;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Leases.InvoiceRunType;
import org.estatio.dom.valuetypes.LocalDateInterval;

/**
 * An item component of an {@link #getLease() owning} {@link Lease}. Each is of
 * a {@link #getType() particular} {@link LeaseItemType}; Estatio currently
 * defines three such: {@link LeaseItemType#RENT (indexable) rent},
 * {@link LeaseItemType#TURNOVER_RENT turnover rent} and
 * {@link LeaseItemType#SERVICE_CHARGE service charge}
 * 
 * <p>
 * Each item gives rise to a succession of {@link LeaseTerm}s, typically
 * generated on a quarterly basis. The lease terms (by implementing
 * <tt>InvoiceSource</tt>) act as the source of <tt>InvoiceItem</tt>s.
 */
@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "LeaseItem_lease_type_sequence_IDX",
                members = { "lease", "type", "sequence" }),
        @javax.jdo.annotations.Index(
                name = "LeaseItem_lease_type_startDate_IDX",
                members = { "lease", "type", "startDate" })
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByLeaseAndTypeAndStartDate",
                language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseItem "
                        + "WHERE lease == :lease "
                        + "   && type == :type "
                        + "   && startDate == :startDate"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseAndTypeAndStartDateAndSequence",
                language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseItem "
                        + "WHERE lease == :lease "
                        + "&& type == :type "
                        + "&& startDate == :startDate "
                        + "&& sequence == :sequence"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseAndTypeAndEndDate",
                language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseItem "
                        + "WHERE lease == :lease "
                        + "   && endDate == :endDate")
})
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public class LeaseItem
        extends EstatioMutableObject<LeaseItem>
        implements WithIntervalMutable<LeaseItem>, WithSequence {

    private static final int PAGE_SIZE = 15;

    public LeaseItem() {
        super("lease, type, sequence desc");
    }

    // //////////////////////////////////////

    private LeaseItemStatus status;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.STATUS_ENUM)
    @Hidden(where = Where.PARENTED_TABLES)
    @Disabled
    public LeaseItemStatus getStatus() {
        return status;
    }

    public void setStatus(final LeaseItemStatus status) {
        this.status = status;
    }

    // //////////////////////////////////////

    private Lease lease;

    @javax.jdo.annotations.Column(name = "leaseId", allowsNull = "false")
    @Hidden(where = Where.PARENTED_TABLES)
    @Title(sequence = "1", append = ":")
    public Lease getLease() {
        return lease;
    }

    public void setLease(final Lease lease) {
        this.lease = lease;
    }

    // //////////////////////////////////////

    private BigInteger sequence;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Hidden
    @Override
    public BigInteger getSequence() {
        return sequence;
    }

    @Override
    public void setSequence(final BigInteger sequence) {
        this.sequence = sequence;
    }

    @Programmatic
    public LeaseTerm findTermWithSequence(final BigInteger sequence) {
        // for (LeaseTerm term : getTerms()) {
        // if (sequence.equals(term.getSequence())) {
        // return term;
        // }
        // }
        // return null;
        // TODO: the code above proved to be very unreliable when using the api.
        // Have to investigate further
        return leaseTerms.findLeaseTermByLeaseItemAndSequence(this, sequence);
    }

    // //////////////////////////////////////

    private LeaseItemType type;

    @javax.jdo.annotations.Persistent(defaultFetchGroup = "true")
    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.TYPE_ENUM)
    @Title(sequence = "2")
    public LeaseItemType getType() {
        return type;
    }

    public void setType(final LeaseItemType type) {
        this.type = type;
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

    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @Hidden(where = Where.PARENTED_TABLES)
    @Optional
    @Disabled
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // //////////////////////////////////////

    private WithIntervalMutable.Helper<LeaseItem> changeDates = new WithIntervalMutable.Helper<LeaseItem>(this);

    WithIntervalMutable.Helper<LeaseItem> getChangeDates() {
        return changeDates;
    }

    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    public LeaseItem changeDates(
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
    @Override
    public LocalDateInterval getEffectiveInterval() {
        return getInterval().overlap(getLease().getEffectiveInterval());
    }

    // //////////////////////////////////////

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(final LocalDate localDate) {
        return getEffectiveInterval().contains(localDate);
    }

    // //////////////////////////////////////

    private InvoicingFrequency invoicingFrequency;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.INVOICING_FREQUENCY_ENUM)
    @Hidden(where = Where.PARENTED_TABLES)
    public InvoicingFrequency getInvoicingFrequency() {
        return invoicingFrequency;
    }

    public void setInvoicingFrequency(final InvoicingFrequency invoicingFrequency) {
        this.invoicingFrequency = invoicingFrequency;
    }

    // //////////////////////////////////////

    private PaymentMethod paymentMethod;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.PAYMENT_METHOD_ENUM)
    @Hidden(where = Where.PARENTED_TABLES)
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // //////////////////////////////////////

    private Charge charge;

    @javax.jdo.annotations.Column(name = "chargeId", allowsNull = "false")
    public Charge getCharge() {
        return charge;
    }

    public void setCharge(final Charge charge) {
        this.charge = charge;
    }

    public List<Charge> choicesCharge() {
        return charges.allCharges();
    }

    // //////////////////////////////////////

    @Disabled
    @Optional
    public BigDecimal getTrialValue() {
        final LeaseTerm currentTerm = currentTerm(getClockService().now());
        return currentTerm != null ? currentTerm.getTrialValue() : null;
    }

    // //////////////////////////////////////

    @Disabled
    @Optional
    public BigDecimal getApprovedValue() {
        LeaseTerm currentTerm = currentTerm(getClockService().now());
        return currentTerm != null ? currentTerm.getApprovedValue() : null;
    }

    // //////////////////////////////////////

    @Programmatic
    public LeaseTerm currentTerm(final LocalDate date) {
        for (LeaseTerm term : getTerms()) {
            if (term.isActiveOn(date)) {
                return term;
            }
        }
        return null;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "leaseItem")
    private SortedSet<LeaseTerm> terms = new TreeSet<LeaseTerm>();

    @Render(Type.EAGERLY)
    @Paged(PAGE_SIZE)
    public SortedSet<LeaseTerm> getTerms() {
        return terms;
    }

    public void setTerms(final SortedSet<LeaseTerm> terms) {
        this.terms = terms;
    }

    @Programmatic
    public LeaseTerm findTerm(final LocalDate startDate) {
        for (LeaseTerm term : getTerms()) {
            if (startDate.equals(term.getStartDate())) {
                return term;
            }
        }
        return null;
    }

    // //////////////////////////////////////

    public LeaseTerm newTerm(
            final @Named("Start date") LocalDate startDate) {
        LeaseTerm lastTerm = null;
        try {
            lastTerm = getTerms().last();
        } catch (NoSuchElementException e) {
            // TODO: is this ok?  if so then let's have a comment here at least.
        }
        LeaseTerm term = leaseTerms.newLeaseTerm(this, lastTerm, startDate);
        term.initialize();
        return term;
    }

    public LocalDate default0NewTerm() {
        LeaseTerm last = null;
        try {
            last = getTerms().last();
        } catch (NoSuchElementException e) {
            return getStartDate();
        }
        if (last.getEndDate() != null) {
            return last.getInterval().endDateExcluding();
        }
        return last.getStartDate().plusYears(1).withMonthOfYear(1).withDayOfMonth(1);
    }

    // //////////////////////////////////////

    public LeaseItem verify() {
        verifyUntil(getClockService().now());
        return this;
    }

    @Programmatic
    public void verifyUntil(final LocalDate date) {
        for (LeaseTerm term : getTerms()) {
            if (term.getPrevious() == null) {
                // since verify is recursive on terms only start on the main
                // term
                term.verifyUntil(date);
            }
        }
    }

    // //////////////////////////////////////

    public LeaseItem calculate(
            final @Named("Period start Date") LocalDate startDate,
            final @Named("Period end date") @Optional LocalDate endDate,
            final @Named("Due date") LocalDate dueDate,
            final @Named("Run Type") InvoiceRunType runType) {
        for (LeaseTerm term : getTerms()) {
            term.calculate(startDate, endDate, dueDate, runType);
        }
        return this;
    }

    // //////////////////////////////////////

    BigDecimal valueForPeriod(
            final InvoicingFrequency frequency,
            final LocalDate periodStartDate,
            final LocalDate dueDate) {
        BigDecimal total = new BigDecimal(0);
        for (LeaseTerm term : getTerms()) {
            total = total.add(term.valueForPeriod(periodStartDate, dueDate, frequency));
        }
        return total;
    }

    // //////////////////////////////////////

    private Charges charges;

    public final void injectCharges(final Charges charges) {
        this.charges = charges;
    }

    private LeaseTerms leaseTerms;

    public final void injectLeaseTerms(final LeaseTerms leaseTerms) {
        this.leaseTerms = leaseTerms;
    }

}
