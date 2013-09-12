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

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.WithInterval;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.WithSequence;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Leases.InvoiceRunType;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.estatio.services.clock.ClockService;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "LEASE_INDEX_IDX",
                members = { "lease", "type", "sequence" }),
        @javax.jdo.annotations.Index(
                name = "LEASE_INDEX2_IDX",
                members = { "lease", "type", "startDate" })
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByLeaseAndTypeAndStartDate", 
                language = "JDOQL", 
                value = "SELECT FROM org.estatio.dom.lease.LeaseItem WHERE lease == :lease && type == :type && startDate == :startDate"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseAndTypeAndStartDateAndSequence", 
                language = "JDOQL", 
                value = "SELECT FROM org.estatio.dom.lease.LeaseItem WHERE lease == :lease && type == :type && startDate == :startDate && sequence == :sequence"),
        @javax.jdo.annotations.Query(
                name = "findByLeaseAndTypeAndEndDate", 
                language = "JDOQL", 
                value = "SELECT FROM org.estatio.dom.lease.LeaseItem WHERE lease == :lease && endDate == :endDate")
})
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public class LeaseItem extends EstatioTransactionalObject<LeaseItem, LeaseItemStatus> implements WithIntervalMutable<LeaseItem>, WithSequence {

    public LeaseItem() {
        super("lease, type, sequence desc", LeaseItemStatus.NEW, null);
    }

    // //////////////////////////////////////

    private LeaseItemStatus status;

    // @javax.jdo.annotations.Column(allowsNull="false")
    @Optional
    @Hidden(where = Where.PARENTED_TABLES)
    @Disabled
    @Override
    public LeaseItemStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(final LeaseItemStatus status) {
        this.status = status;
    }

    // //////////////////////////////////////

    private Lease lease;

    @javax.jdo.annotations.Column(name = "LEASE_ID", allowsNull = "false")
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
    public LeaseTerm findTermWithSequence(BigInteger sequence) {
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

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Hidden(where = Where.PARENTED_TABLES)
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
    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // //////////////////////////////////////

    private WithIntervalMutable.Helper<LeaseItem> changeDates = new WithIntervalMutable.Helper<LeaseItem>(this);

    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    public LeaseItem changeDates(
            final @Named("Start Date") @Optional LocalDate startDate,
            final @Named("End Date") @Optional LocalDate endDate) {
        return changeDates.changeDates(startDate, endDate);
    }

    public String disableChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return isLocked() ? "Cannot modify when locked" : null;
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

    @Hidden
    @Override
    public Lease getWithIntervalParent() {
        return getLease();
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
    @Override
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getEffectiveStartDate(), getEffectiveEndDate());
    }

    @Programmatic
    public LocalDate calculatedEndDate() {
        return getEndDate() == null ? getLease().getEndDate() : getEndDate();
    }

    // //////////////////////////////////////

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(LocalDate localDate) {
        return getInterval().contains(localDate);
    }

    // //////////////////////////////////////

    private InvoicingFrequency invoicingFrequency;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Hidden(where = Where.PARENTED_TABLES)
    public InvoicingFrequency getInvoicingFrequency() {
        return invoicingFrequency;
    }

    public void setInvoicingFrequency(final InvoicingFrequency invoicingFrequency) {
        this.invoicingFrequency = invoicingFrequency;
    }

    // //////////////////////////////////////

    private PaymentMethod paymentMethod;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Hidden(where = Where.PARENTED_TABLES)
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // //////////////////////////////////////

    private Charge charge;

    @javax.jdo.annotations.Column(name = "CHARGE_ID", allowsNull = "false")
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
        LeaseTerm currentTerm = currentTerm(getClockService().now());
        if (currentTerm != null)
            return currentTerm.getTrialValue();
        return null;
    }

    // //////////////////////////////////////

    @Disabled
    @Optional
    public BigDecimal getApprovedValue() {
        LeaseTerm currentTerm = currentTerm(getClockService().now());
        if (currentTerm != null)
            return currentTerm.getApprovedValue();
        return null;
    }

    // //////////////////////////////////////

    @Programmatic
    public LeaseTerm currentTerm(LocalDate date) {
        for (LeaseTerm term : getTerms()) {
            if (term.getInterval().contains(date)) {
                return term;
            }
        }
        return null;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "leaseItem")
    private SortedSet<LeaseTerm> terms = new TreeSet<LeaseTerm>();

    @Render(Type.EAGERLY)
    @Paged(15)
    public SortedSet<LeaseTerm> getTerms() {
        return terms;
    }

    public void setTerms(final SortedSet<LeaseTerm> terms) {
        this.terms = terms;
    }

    @Programmatic
    public LeaseTerm findTerm(LocalDate startDate) {
        for (LeaseTerm term : getTerms()) {
            if (startDate.equals(term.getStartDate())) {
                return term;
            }
        }
        return null;
    }

    // //////////////////////////////////////

    public LeaseTerm newTerm() {
        LeaseTerm term = leaseTerms.newLeaseTerm(this);
        return term;
    }

    public String disableNewTerm() {
        return getTerms().size() > 0 ? "Use either 'Verify' or 'Create Next Term' on last term" : null;
    }

    // //////////////////////////////////////

    @Programmatic
    public LeaseTerm createNextTerm(LeaseTerm currentTerm) {
        LeaseTerm term = leaseTerms.newLeaseTerm(this, currentTerm);
        return term;
    }

    // //////////////////////////////////////

    public LeaseItem verify() {
        for (LeaseTerm term : getTerms()) {
            if (term.getPrevious() == null) {
                // since verify is recursive on terms only start on the main
                // term
                term.verify();
            }
        }
        return this;
    }

    // //////////////////////////////////////

    public LeaseItem calculate(@Named("Period Start Date") LocalDate startDate, @Named("Due date") LocalDate dueDate, @Named("Run Type") InvoiceRunType runType) {
        for (LeaseTerm term : getTerms()) {
            term.calculate(startDate, dueDate, runType);
        }
        return this;
    }

    BigDecimal valueForPeriod(InvoicingFrequency frequency, LocalDate periodStartDate, LocalDate dueDate) {
        BigDecimal total = new BigDecimal(0);
        for (LeaseTerm term : getTerms()) {
            total = total.add(term.valueForPeriod(frequency, periodStartDate, dueDate));
        }
        return total;
    }

    // //////////////////////////////////////

    private Charges charges;

    public final void injectCharges(Charges charges) {
        this.charges = charges;
    }

    private LeaseTerms leaseTerms;

    public final void injectLeaseTerms(LeaseTerms leaseTerms) {
        this.leaseTerms = leaseTerms;
    }

}
