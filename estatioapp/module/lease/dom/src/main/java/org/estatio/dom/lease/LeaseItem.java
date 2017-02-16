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
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

import org.apache.commons.lang3.ObjectUtils;
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
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.base.dom.with.WithIntervalMutable;
import org.incode.module.base.dom.with.WithSequence;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;
import org.estatio.dom.apptenancy.WithApplicationTenancyPathPersisted;
import org.estatio.dom.apptenancy.WithApplicationTenancyPropertyLocal;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.invoice.InvoicingInterval;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService.CalculationResult;
import org.estatio.dom.tax.Tax;

import lombok.Getter;
import lombok.Setter;

/**
 * An item component of an {@link #getLease() owning} {@link Lease}. Each is of
 * a {@link #getType() particular} {@link LeaseItemType}; Estatio currently
 * defines three such: {@link LeaseItemType#RENT (indexable) rent},
 * {@link LeaseItemType#TURNOVER_RENT turnover rent} and
 * {@link LeaseItemType#SERVICE_CHARGE service charge}
 * <p/>
 * <p/>
 * Each item gives rise to a succession of {@link LeaseTerm}s, typically
 * generated on a quarterly basis. The lease terms (by implementing
 * <tt>InvoiceSource</tt>) act as the source of <tt>InvoiceItem</tt>s.
 */
@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"     // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Indices({
        @Index(
                name = "LeaseItem_lease_type_sequence_IDX",
                members = {"lease", "type", "sequence"}),
        @Index(
                name = "LeaseItem_lease_type_startDate_IDX",
                members = {"lease", "type", "startDate"})
})
@Queries({
        @Query(
                name = "findByLeaseAndTypeAndStartDateAndSequence",
                language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseItem "
                        + "WHERE lease == :lease "
                        + "&& type == :type "
                        + "&& startDate == :startDate "
                        + "&& sequence == :sequence"),
        @Query(
                name = "findByLeaseAndType",
                language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseItem "
                        + "WHERE lease == :lease "
                        + "&& type == :type "
                        + "ORDER BY sequence "),
        @Query(
                name = "findByLeaseAndTypeAndCharge",
                language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseItem "
                        + "WHERE lease == :lease "
                        + "&& type == :type "
                        + "&& charge == :charge "
                        + "ORDER BY sequence "),
        @Query(
                name = "findByLeaseAndTypeAndChargeAndStartDate",
                language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.LeaseItem "
                        + "WHERE lease == :lease "
                        + "&& type == :type "
                        + "&& charge == :charge "
                        + "&& startDate == :startDate "
                        + "ORDER BY sequence ")

})
@Unique(name = "LeaseItem_lease_type_charge_startDate_sequence_UNQ", members = {"lease", "type", "charge", "startDate", "sequence"})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.dom.lease.LeaseItem"
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
public class LeaseItem
        extends UdoDomainObject2<LeaseItem>
        implements WithIntervalMutable<LeaseItem>, WithSequence, WithApplicationTenancyPropertyLocal, WithApplicationTenancyPathPersisted {

    private static final int PAGE_SIZE = 15;

    public LeaseItem() {
        super("lease, type, charge, startDate, sequence");
    }

    public LeaseItem(final Lease lease, final InvoicingFrequency invoicingFrequency) {
        this();
        this.lease = lease;
        this.invoicingFrequency = invoicingFrequency;
    }

    @Programmatic
    public SortedSet<LocalDate> dueDatesInRange(LocalDate startDueDate, LocalDate nextDueDate) {
        final SortedSet<LocalDate> dates = Sets.newTreeSet();
        List<InvoicingInterval> invoiceIntervals = getInvoicingFrequency().intervalsInDueDateRange(
                startDueDate, nextDueDate);
        for (InvoicingInterval interval : invoiceIntervals) {
            dates.add(interval.dueDate());
        }
        return dates;
    }

    // //////////////////////////////////////

    @Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String applicationTenancyPath;

    @PropertyLayout(
            named = "Application Level",
            hidden = Where.PARENTED_TABLES,
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(getApplicationTenancyPath());
    }

    // //////////////////////////////////////

    @Column(allowsNull = "false", length = LeaseItemStatus.Meta.MAX_LEN)
    @Getter @Setter
    private LeaseItemStatus status;

    // //////////////////////////////////////

    @Action(domainEvent = LeaseItem.SuspendEvent.class)
    public LeaseItem suspend(final String reason) {
        setStatus(LeaseItemStatus.SUSPENDED);
        return this;
    }

    public boolean hideSuspend() {
        return getStatus().equals(LeaseItemStatus.SUSPENDED);
    }

    @Action(domainEvent = LeaseItem.ResumeEvent.class)
    public LeaseItem resume(final String reason) {
        doResume();
        return this;
    }

    public boolean hideResume() {
        return !getStatus().equals(LeaseItemStatus.SUSPENDED);
    }

    @Programmatic
    public void doResume() {
        this.setStatus(LeaseItemStatus.UNKOWN);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Object remove() {
        Lease tmpLease = getLease();
        if (doRemove()) {
            return tmpLease;
        }
        return this;
    }

    @Programmatic
    public boolean doRemove() {
        boolean canDelete = true;
        if (!getTerms().isEmpty()) {
            canDelete = getTerms().first().doRemove();
        }
        if (canDelete) {
            final Sets.SetView<LeaseItemSource> itemSources = Sets.union(
                    Sets.newHashSet(leaseItemSourceRepository.findByItem(this)),
                    Sets.newHashSet(leaseItemSourceRepository.findBySourceItem(this)));

            for (LeaseItemSource leaseItemSource : itemSources){
                leaseItemSource.remove();
            }
            remove(this);
//            getContainer().remove(this);
            getContainer().flush();
        }
        return canDelete;
    }

    // //////////////////////////////////////

    public String title(){
        return TitleBuilder.start()
                .withParent(getLease())
                .withName(getType())
                .withName(getCharge())
                .toString();
    }



    @Column(name = "leaseId", allowsNull = "false")
    @Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private Lease lease;

    // //////////////////////////////////////

    @Property(hidden = Where.ALL_TABLES)
    @PropertyLayout(describedAs = "When left empty the tax of the charge will be used")
    @Column(name = "taxId", allowsNull = "true")
    @Getter @Setter
    private Tax tax;

    @Programmatic
    public Tax getEffectiveTax() {
        return getTax() == null && getCharge() != null ? getCharge().getTax() : getTax();
    }

    // //////////////////////////////////////

    @Column(allowsNull = "false")
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private BigInteger sequence;

    @Programmatic
    public LeaseTerm findTermWithSequence(final BigInteger sequence) {
        return leaseTermRepository.findByLeaseItemAndSequence(this, sequence);
    }

    // //////////////////////////////////////

    @Persistent(defaultFetchGroup = "true")
    @Column(allowsNull = "false", length = LeaseItemType.Meta.MAX_LEN)
    @Getter @Setter
    private LeaseItemType type;

    // //////////////////////////////////////

    @Persistent
    @Getter @Setter
    private LocalDate startDate;

    @Property(optionality = Optionality.OPTIONAL)
    @Persistent
    @Getter @Setter
    private LocalDate endDate;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LeaseConstants.AgreementRoleType invoicedBy;

    // //////////////////////////////////////

    private WithIntervalMutable.Helper<LeaseItem> changeDates = new WithIntervalMutable.Helper<>(this);

    WithIntervalMutable.Helper<LeaseItem> getChangeDates() {
        return changeDates;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @Override
    public LeaseItem changeDates(
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
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

    public LeaseItem copy(
            final LocalDate startDate,
            final InvoicingFrequency invoicingFrequency,
            final PaymentMethod paymentMethod,
            final Charge charge
    ) {
        final LeaseItem newItem = getLease().newItem(
                this.getType(), LeaseConstants.AgreementRoleType.LANDLORD, charge, invoicingFrequency, paymentMethod, startDate);
        this.copyTerms(startDate, newItem);
        this.changeDates(getStartDate(), newItem.getInterval().endDateFromStartDate());
        return newItem;
    }

    public LocalDate default0Copy() {
        return getStartDate();
    }

    public InvoicingFrequency default1Copy() {
        return getInvoicingFrequency();
    }

    public PaymentMethod default2Copy() {
        return getPaymentMethod();
    }

    public Charge default3Copy() {
        return getCharge();
    }

    public List<Charge> choices3Copy() {
        return chargeRepository.chargesForCountry(this.getApplicationTenancy());
    }

    public String validateCopy(
            final LocalDate startDate,
            final InvoicingFrequency invoicingFrequency,
            final PaymentMethod paymentMethod,
            final Charge charge
    ) {
        if (!choices3Copy().contains(charge)) {
            return "Charge (with app tenancy '%s') is not valid for this lease item";
        }
        return null;
    }


    // //////////////////////////////////////

    public LeaseItem terminate(
            final LocalDate endDate) {
        this.changeDates(getStartDate(), endDate);
        return this;
    }

    public LocalDate default0Terminate() {
        return getLease().getInterval().endDateExcluding();
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

    @Column(allowsNull = "false", length = InvoicingFrequency.Meta.MAX_LEN)
    @Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private InvoicingFrequency invoicingFrequency;

    @Action(domainEvent = ChangeInvoicingFrequencyEvent.class)
    public LeaseItem changeInvoicingFrequency(final InvoicingFrequency invoicingFrequency) {
        setInvoicingFrequency(invoicingFrequency);
        return this;
    }

    public InvoicingFrequency default0ChangeInvoicingFrequency() {
        return getInvoicingFrequency();
    }

    // //////////////////////////////////////

    @Column(allowsNull = "false", length = PaymentMethod.Meta.MAX_LEN)
    @Getter @Setter
    private PaymentMethod paymentMethod;

    // //////////////////////////////////////

    @Column(name = "chargeId", allowsNull = "false")
    @Getter @Setter
    private Charge charge;

    public List<Charge> choicesCharge() {
        return chargeRepository.allCharges();
    }

    // //////////////////////////////////////

    public LeaseItem changeCharge(final Charge charge) {
        setCharge(charge);
        return this;
    }

    public Charge default0ChangeCharge() {
        return getCharge();
    }

    public List<Charge> choices0ChangeCharge() {
        return chargeRepository.chargesForCountry(getApplicationTenancyPath());
    }


    // //////////////////////////////////////

    public LeaseItem changePaymentMethod(
            final PaymentMethod paymentMethod,
            final String reason) {
        setPaymentMethod(paymentMethod);
        return this;
    }

    public PaymentMethod default0ChangePaymentMethod(
            final PaymentMethod paymentMethod,
            final String reason
    ) {
        return getPaymentMethod();
    }

    // //////////////////////////////////////

    public LeaseItem overrideTax(
            final Tax tax,
            final String reason) {
        setTax(tax);
        return this;
    }

    public Tax default0OverrideTax(
            final Tax tax,
            final String reason) {
        return getTax();
    }

    public boolean hideOverrideTax() {
        return getTax() != null;
    }

    // //////////////////////////////////////

    public LeaseItem cancelOverrideTax(
            final String reason) {
        setTax(null);
        return this;
    }

    public boolean hideCancelOverrideTax() {
        return getTax() == null;
    }

    // //////////////////////////////////////

    @Property(hidden = Where.PARENTED_TABLES, optionality = Optionality.OPTIONAL)
    @Persistent
    @Getter @Setter
    private LocalDate nextDueDate;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL, hidden = Where.ALL_TABLES)
    @Persistent
    @Getter @Setter
    private LocalDate epochDate;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    public BigDecimal getValue() {
        return valueForDate(getClockService().now());
    }

    @Programmatic
    public BigDecimal valueForDate(final LocalDate date) {
        final LeaseTerm currentTerm = currentTerm(date);
        return currentTerm != null ? currentTerm.valueForDate(date) : BigDecimal.ZERO;
    }

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

    @Persistent(mappedBy = "leaseItem")
    @CollectionLayout(render = RenderType.EAGERLY, paged = PAGE_SIZE)
    @Getter @Setter
    private SortedSet<LeaseTerm> terms = new TreeSet<>();

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
            final LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        return leaseTermRepository.newLeaseTerm(this, lastInChain(), startDate, endDate);
    }

    private LeaseTerm lastInChain() {
        if (getType().autoCreateTerms() && !getTerms().isEmpty()) {
            return getTerms().last();
        }
        return null;
    }

    public String validateNewTerm(
            final LocalDate startDate,
            final LocalDate endDate
    ) {
        return leaseTermRepository.validateNewLeaseTerm(this, lastInChain(), startDate, endDate);
    }

    public LocalDate default0NewTerm(
            final LocalDate startDate,
            final LocalDate endDate) {
        if (getTerms().size() == 0) {
            return getStartDate();
        }
        LeaseTerm last = getTerms().last();
        return last.default0CreateNext(null, null);
    }

    public LocalDate default1NewTerm(
            final LocalDate startDate,
            final LocalDate endDate) {
        if (getTerms().size() == 0) {
            return null;
        }
        return getTerms().last().default1CreateNext(null, null);
    }

    @MemberOrder(sequence = "1")
    public List<LeaseItemSource> getSourceItems() {
        return leaseItemSourceRepository.findByItem(this);
    }

    public boolean hideSourceItems() {
        return !getType().useSource();
    }

    @MemberOrder(sequence = "2", name = "sourceItems")
    public LeaseItem newSourceItem(
            final LeaseItem sourceItem
    ){
        leaseItemSourceRepository.newSource(this, sourceItem);
        return this;
    }

    public boolean hideNewSourceItem() {
        return !getType().useSource();
    }

    public List<LeaseItem> choices0NewSourceItem(final LeaseItem leaseItem){
        List<LeaseItem> choices = new ArrayList<>();
        for (LeaseItem item : getLease().getItems()) {
            // items should not be linked to themselves and be linked only once
            if (!item.equals(this) && leaseItemNotLinked(item)){
                choices.add(item);
            }

        }
        return choices;
    }

    @Programmatic
    private boolean leaseItemNotLinked(final LeaseItem item) {
        for (LeaseItemSource src : getSourceItems()){
            if (src.getSourceItem().equals(item)) {return false;}
        }
        return true;
    }

    @Programmatic
    public LeaseItemSource findOrCreateSourceItem(final LeaseItem sourceItem){
        return leaseItemSourceRepository.findOrCreateSource(this, sourceItem);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public LeaseItem verify() {
        verifyUntil(ObjectUtils.min(getEffectiveInterval().endDateExcluding(), getClockService().now()));
        return this;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public LeaseItem verifyUntil(final LocalDate date) {
        for(LeaseTerm term : getTerms().stream().filter(t->t.getPrevious()==null).collect(Collectors.toList())) {
            // only verify the first terms of a chain or the standalones:
            term.verifyUntil(date);
        }
        return this;
    }

    // //////////////////////////////////////

    @Programmatic
    public void copyTerms(final LocalDate startDate, final LeaseItem newItem) {
        LeaseTerm lastTerm = null;
        for (LeaseTerm term : getTerms()) {
            if (term.getInterval().contains(startDate)) {
                LeaseTerm newTerm;
                if (lastTerm == null) {
                    newTerm = newItem.newTerm(term.getStartDate(), null);
                } else {
                    newTerm = lastTerm.createNext(term.getStartDate(), term.getEndDate());
                }
                term.copyValuesTo(newTerm);
                lastTerm = newTerm;
            }
        }
    }

    // //////////////////////////////////////

    @Programmatic
    public List<CalculationResult> calculationResults(
            final LocalDateInterval interval,
            final LocalDate dueDate
            ) {
        List<CalculationResult> results = new ArrayList<>();
        for (LeaseTerm term : getTerms()) {
            results.addAll(term.calculationResults(interval, dueDate));
        }
        return results;
    }

    // //////////////////////////////////////

    public static class Predicates {
        private Predicates() {
        }

        public static Predicate<LeaseItem> ofType(final LeaseItemType t) {
            return new Predicate<LeaseItem>() {
                @Override
                public boolean apply(LeaseItem input) {
                    return input.getType() == t;
                }
            };
        }
    }

    // //////////////////////////////////////

    public static class SuspendEvent extends ActionDomainEvent<LeaseItem> {
        private static final long serialVersionUID = 1L;
    }

    // //////////////////////////////////////

    public static class ResumeEvent extends ActionDomainEvent<LeaseItem> {
        private static final long serialVersionUID = 1L;
    }

    // //////////////////////////////////////

    public static class ChangeInvoicingFrequencyEvent extends ActionDomainEvent<LeaseItem> {
        private static final long serialVersionUID = 1L;
    }

    // //////////////////////////////////////

    @Inject
    private ChargeRepository chargeRepository;

    @Inject
    LeaseTermRepository leaseTermRepository;

    @Inject
    EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

    @Inject LeaseItemSourceRepository leaseItemSourceRepository;

}
