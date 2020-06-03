package org.estatio.module.lease.dom.amendments;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;
import javax.validation.constraints.Digits;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseTerm;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo" // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("amendments.LeaseAmendmentItemForDiscount")
public class LeaseAmendmentItemForDiscount extends LeaseAmendmentItem {

    @Column(allowsNull = "false", scale = 2)
    @Getter @Setter
    private BigDecimal discountPercentage;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public LeaseAmendmentItemForDiscount changeDiscountPercentage(@Digits(integer = 3, fraction = 2) final BigDecimal newPercentage){
        setDiscountPercentage(newPercentage);
        return this;
    }

    public BigDecimal default0ChangeDiscountPercentage(){
        return getDiscountPercentage();
    }

    public String disableChangeDiscountPercentage(){
        final String warning = String.format("Amendment in state of %s cannot be changed", getLeaseAmendment().getState());
        return getLeaseAmendment().amendmentDataIsImmutable() ? warning : null;
    }

    @Column(allowsNull = "true", scale = 2)
    @Getter @Setter
    private BigDecimal calculatedDiscountAmount;

    @Column(allowsNull = "true", scale = 2)
    @Getter @Setter
    private BigDecimal totalValueForDateBeforeDiscount;

    @Programmatic
    public BigDecimal calculateDiscountAmountUsingLeasePreview(){
        final Lease leasePreview = getLeaseAmendment().getLeasePreview();
        if (leasePreview==null) return null;
        BigDecimal result = new BigDecimal("0.00");
        final List<LeaseItem> leaseItemsInvolved = Lists.newArrayList(leasePreview.getItems()).stream()
                .filter(li -> li.getLeaseAmendmentItem()==this)
                .collect(Collectors.toList());
        for (LeaseItem leaseItem : leaseItemsInvolved){
            for (LeaseTerm term : leaseItem.getTerms()){
                for (PersistedCalculationResult calcResult : persistedCalculationResultRepository.findByLeaseTerm(term)){
                    result = result.add(calcResult.getValue());
                }
            }
        }
        return result;
    }

    @Programmatic
    public List<LeaseItem> leaseItemsToIncludeForDiscount(final Lease lease){
        final List<LeaseItem> itemsToIncludeForDiscount = Lists.newArrayList(lease.getItems()).stream()
                .filter(li -> LeaseAmendmentItem
                        .applicableToFromString(this.getApplicableTo())
                        .contains(li.getType()))
                .filter(li->li.getEffectiveInterval().overlaps(this.getInterval()))
                .collect(Collectors.toList());
        return itemsToIncludeForDiscount;
    }

    @Programmatic
    public BigDecimal calculateValueForDateBeforeDiscountUsingLeasePreview(){
        final Lease leasePreview = getLeaseAmendment().getLeasePreview();
        if (leasePreview==null) return null;
        return leaseItemsToIncludeForDiscount(leasePreview).stream()
                .map(li -> li.valueForDate(getStartDate().minusDays(1)))
                .filter(x->x!=null)
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @Override
    public LeaseAmendmentItemType getType(){
        return LeaseAmendmentItemType.DISCOUNT;
    }

    @Inject
    PersistedCalculationResultRepository persistedCalculationResultRepository;
}
