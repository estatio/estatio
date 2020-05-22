package org.estatio.module.lease.dom.amendments;

import java.math.BigDecimal;
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
import org.apache.isis.applib.annotation.RestrictTo;
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
                .withName("Lease amendment")
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

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public LeaseAmendment changeState(final LeaseAmendmentState state){
        setState(state);
        return this;
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

    @Programmatic
    public void remove(){
        repositoryService.removeAndFlush(this);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public LeaseAmendment apply(){
        leaseAmendmentService.apply(this, false);
        return this;
    }

    public String disableApply(){
        return getState()!=LeaseAmendmentState.SIGNED ? "Only signed amendments can be applied" : null;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public LeaseAmendment createLeasePreview(){
        leaseAmendmentService.getLeasePreviewFor(this);
        return this;
    }

    public String disableCreateLeasePreview(){
        return getLeasePreview()!=null ? "There is already a lease preview. We support 1 preview at the moment." : null;
    }

    @Programmatic
    public LocalDate getEffectiveStartDate(){
        final Optional<LocalDate> min = Lists.newArrayList(getItems()).stream()
                .map(ai -> ai.getStartDate())
                .min(LocalDate::compareTo);
        if (min.isPresent()) {
            return min.get();
        } else {
            // SHOULD BE IMPOSSIBLE
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
            // SHOULD BE IMPOSSIBLE
            return null;
        }
    }

    @Programmatic
    public LocalDateInterval getEffectiveInterval(){
        return LocalDateInterval.including(getEffectiveStartDate(), getEffectiveEndDate());
    }

    @Programmatic
    public List<LeaseAmendmentItem> findItemsOfType(final LeaseAmendmentItemType type){
        return Lists.newArrayList(getItems()).stream().filter(lai->lai.getType()==type).collect(Collectors.toList());
    }

    @Programmatic
    public LeaseAmendment upsertItem(final BigDecimal discountPercentage, final List<LeaseItemType> discountAppliesTo, final LocalDate discountStartDate, final LocalDate discountEndDate) {
        leaseAmendmentItemRepository.upsert(this, discountPercentage, discountAppliesTo, discountStartDate, discountEndDate);
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

    @Inject
    RepositoryService repositoryService;

    @Inject
    LeaseAmendmentService leaseAmendmentService;

    @Inject LeaseAmendmentItemRepository leaseAmendmentItemRepository;

}
