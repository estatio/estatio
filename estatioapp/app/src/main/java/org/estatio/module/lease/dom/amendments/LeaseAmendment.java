package org.estatio.module.lease.dom.amendments;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItemType;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        schema = "dbo" // Isis' ObjectSpecId inferred from @Discriminator
)
// identityType=IdentityType.DATASTORE inherited from superclass
@Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
// no @DatastoreIdentity nor @Version, since inherited from supertype
@Discriminator("org.estatio.module.lease.dom.amendments.LeaseAmendment")
@Queries({
        @Query(
                name = "findByLease", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.amendments.LeaseAmendment "
                        + "WHERE lease == :lease "),
        @Query(
                name = "findByType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.amendments.LeaseAmendment "
                        + "WHERE leaseAmendmentType == :leaseAmendmentType"),
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.amendments.LeaseAmendment "
                        + "WHERE lease == :lease && "
                        + "leaseAmendmentType == :leaseAmendmentType"),
        @Query(
                name = "findByLeasePreview", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.amendments.LeaseAmendment "
                        + "WHERE leasePreview == :leasePreview "),
        @Query(
                name = "findByState", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.lease.dom.amendments.LeaseAmendment "
                        + "WHERE state == :state ")
})
@Unique(name = "LeaseAmendment_lease_leaseAmendmentType_UNQ", members = {"lease", "leaseAmendmentType"})
@DomainObject(editing = Editing.DISABLED)
public class LeaseAmendment extends Agreement {

    public LeaseAmendment() {
        super(LeaseAgreementRoleTypeEnum.LANDLORD, LeaseAgreementRoleTypeEnum.TENANT);
    }

    @Override
    public String title() {
        return TitleBuilder.start()
                .withName("Amendment")
                .withName(getName())
                .withReference(getReference())
                .toString();
    }

    @Column(name = "leaseId", allowsNull = "false")
    @Getter @Setter
    private Lease lease;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LeaseAmendmentType leaseAmendmentType;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LeaseAmendmentState state;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate dateSigned;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate dateApplied;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public LeaseAmendment sign(final LocalDate dateSigned){
        setState(LeaseAmendmentState.SIGNED);
        setDateSigned(dateSigned);
        findItemsOfType(LeaseAmendmentItemType.DISCOUNT).forEach(lai->{
            LeaseAmendmentItemForDiscount castedItem = (LeaseAmendmentItemForDiscount) lai;
            castedItem.setAmortisationEndDate(leaseAmendmentService.getAmortisationEndDateFor(castedItem));
        });
        return this;
    }

    public boolean hideSign(){
        return getState()!=LeaseAmendmentState.PROPOSED;
    }

    public LocalDate default0Sign(){
        return clockService.now();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public LeaseAmendment changeDateSigned(final LocalDate dateSigned){
        setDateSigned(dateSigned);
        return this;
    }

    public LocalDate default0ChangeDateSigned(){
        return getDateSigned();
    }

    public String disableChangeDateSigned(){
        if (getState()!=LeaseAmendmentState.SIGNED) return "Only on signed amendments the date signed can be changed";
        return null;
    }

    @Column(name = "leasePreviewId", allowsNull = "true")
    @Getter @Setter
    private Lease leasePreview;

    @Getter @Setter
    @Persistent(mappedBy = "leaseAmendment", dependentElement = "true")
    private SortedSet<LeaseAmendmentItem> items = new TreeSet<>();

    @Override
    @ActionLayout(hidden = Where.EVERYWHERE)
    public Agreement changePrevious(final Agreement previousAgreement) {
        return null;
    }

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return lease.getApplicationTenancy();
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Lease remove(){
        Lease result = getLease();
        if (getLeasePreview()!=null){
            getLeasePreview().remove("Removing amendment");
        }
        repositoryService.removeAndFlush(this);
        return result;
    }

    public String disableRemove(){
        if (amendmentDataIsImmutable()) return "The amendment is immutable";
        return null;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public LeaseAmendment apply(){
        final Optional<LeaseAmendmentItem> optionalDiscountItem = Lists.newArrayList(getItems()).stream()
                .filter(i -> i.getType() == LeaseAmendmentItemType.DISCOUNT).findFirst();
        // make sure a most recent lease preview is created in order to update the calculated values on discount item
        // NOTE: this preview will be deleted right after this when applying the lease
        if (optionalDiscountItem.isPresent()){
            createOrRenewLeasePreview();
        }
        leaseAmendmentService.apply(this, false);
        setDateApplied(clockService.now());
        return this;
    }

    public boolean hideApply(){
        return !Arrays.asList(LeaseAmendmentState.SIGNED, LeaseAmendmentState.APPLY).contains(getState());
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public LeaseAmendment createOrRenewLeasePreview(){
        if (getLeasePreview()!=null) getLeasePreview().remove("Replacing preview");
        leaseAmendmentService.getLeasePreviewFor(this);
        return this;
    }

    public String disableCreateOrRenewLeasePreview(){
        if (getState()==LeaseAmendmentState.APPLIED) return "This amendment is applied";
        return null;
    }

    @Programmatic
    public LocalDate getEffectiveStartDate(){
        final Optional<LocalDate> min = Lists.newArrayList(getItems()).stream()
                .map(ai -> ai.getStartDate())
                .min(LocalDate::compareTo);
        if (min.isPresent()) {
            return min.get();
        } else {
            // only when there are no items
            return null;
        }
    }

    @Programmatic
    public LocalDate getEffectiveEndDate(){
        final Optional<LocalDate> max = Lists.newArrayList(getItems()).stream()
                .map(ai -> ai.getEndDate())
                .max(LocalDate::compareTo);
        if (max.isPresent()) {
            return max.get();
        } else {
            // only when there are no items
            return null;
        }
    }

    @Programmatic
    public LocalDateInterval getEffectiveInterval(){
        return LocalDateInterval.including(getEffectiveStartDate(), getEffectiveEndDate());
    }

    @Programmatic
    public List<LeaseAmendmentItem> findItemsOfType(final LeaseAmendmentItemType type){
        return Lists.newArrayList(getItems()).stream().filter(lai->lai.getType()==type).sorted(Comparator.comparing(LeaseAmendmentItem::getStartDate)).collect(Collectors.toList());
    }

    @Programmatic
    public LeaseAmendment upsertItem(
            final BigDecimal discountPercentage,
            final BigDecimal manualDiscountAmount,
            final List<LeaseItemType> discountAppliesTo,
            final LocalDate discountStartDate,
            final LocalDate discountEndDate) {
        leaseAmendmentItemRepository.upsert(this, discountPercentage, manualDiscountAmount, discountAppliesTo, discountStartDate, discountEndDate);
        return this;
    }

    @Programmatic
    public LeaseAmendment upsertItem(
            final InvoicingFrequency invoicingFrequencyOnLease,
            final InvoicingFrequency newInvoicingFrequency,
            final List<LeaseItemType> frequencyChangeAppliesTo,
            final LocalDate frequencyChangeStartDate,
            final LocalDate frequencyChangeEndDate) {
        leaseAmendmentItemRepository.upsert(this, invoicingFrequencyOnLease, newInvoicingFrequency,frequencyChangeAppliesTo, frequencyChangeStartDate, frequencyChangeEndDate);
        return null;
    }

    @Override
    public String disableChangeDates(){
        final String warning = String.format("Amendment in state of %s cannot be changed", getState());
        return amendmentDataIsImmutable() ? warning : null;
    }

    @Programmatic
    public boolean amendmentDataIsImmutable() {
        return getState()!=LeaseAmendmentState.PROPOSED;
    }
    @Inject
    RepositoryService repositoryService;

    @Inject
    LeaseAmendmentService leaseAmendmentService;

    @Inject
    LeaseAmendmentItemRepository leaseAmendmentItemRepository;
}
