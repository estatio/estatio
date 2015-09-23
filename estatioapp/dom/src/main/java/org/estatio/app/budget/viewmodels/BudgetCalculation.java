package org.estatio.app.budget.viewmodels;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.annotation.ViewModelLayout;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.Distributable;

@ViewModel
@ViewModelLayout()
public class BudgetCalculation implements Distributable {

    public BudgetCalculation(){};

    public BudgetCalculation(final Unit unit, final BigDecimal value, final BigDecimal sourceValue) {
        this.unit = unit;
        this.value = value;
        this.sourceValue = sourceValue;
    }

    //region > unit (property)
    private Unit unit;

    @MemberOrder(sequence = "1")
    public Unit getUnit() {
        return unit;
    }

    public void setUnit(final Unit unit) {
        this.unit = unit;
    }
    //endregion

    //region > value (property)
    private BigDecimal value;

    @MemberOrder(sequence = "2")
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(final BigDecimal value) {
        this.value = value;
    }
    //endregion

    //region > sourceValue (property)
    private BigDecimal sourceValue;

    @MemberOrder(sequence = "3")
    @Column(scale = 6)
    public BigDecimal getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(final BigDecimal sourceValue) {
        this.sourceValue = sourceValue;
    }
    //endregion

}
