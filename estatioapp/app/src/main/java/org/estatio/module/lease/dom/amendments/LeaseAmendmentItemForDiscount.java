package org.estatio.module.lease.dom.amendments;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;
import javax.validation.constraints.Digits;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.SemanticsOf;

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
        return getLeaseAmendment().getState()==LeaseAmendmentState.PROPOSED ? null : warning;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @Override
    public LeaseAmendmentItemType getType(){
        return LeaseAmendmentItemType.DISCOUNT;
    }
}
