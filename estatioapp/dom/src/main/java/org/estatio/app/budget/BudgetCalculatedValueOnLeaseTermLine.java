package org.estatio.app.budget;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;

import org.estatio.dom.lease.LeaseTerm;

@DomainObject(
        nature = Nature.VIEW_MODEL
)
public class BudgetCalculatedValueOnLeaseTermLine {

    public BudgetCalculatedValueOnLeaseTermLine() {}

    public BudgetCalculatedValueOnLeaseTermLine(
            final BigDecimal calculatedValue,
            final LeaseTerm leaseTerm

    ){
        this.calculatedValue=calculatedValue;
        this.leaseTerm=leaseTerm;
    }

    //region > calculatedValue (property)
    private BigDecimal calculatedValue;

    @MemberOrder(sequence = "1")
    public BigDecimal getCalculatedValue() {
        return calculatedValue;
    }

    public void setCalculatedValue(final BigDecimal calculatedValue) {
        this.calculatedValue = calculatedValue;
    }
    //endregion


    //region > leaseTerm (property)
    private LeaseTerm leaseTerm;

    @MemberOrder(sequence = "2")
    public LeaseTerm getLeaseTerm() {
        return leaseTerm;
    }

    public void setLeaseTerm(final LeaseTerm leaseTerm) {
        this.leaseTerm = leaseTerm;
    }
    //endregion

}
