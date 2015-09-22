package org.estatio.dom.budgeting.schedule.viewmodels;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.annotation.ViewModelLayout;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.budgeting.Distributable;

/**
 * Created by jodo on 20/09/15.
 */
@ViewModel
@ViewModelLayout()
public class ValueAssignedToUnitLine implements Distributable {

    public ValueAssignedToUnitLine(){};

    public ValueAssignedToUnitLine(final Unit unit, final BigDecimal value, final BigDecimal sourceValue) {
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
    public BigDecimal getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(final BigDecimal sourceValue) {
        this.sourceValue = sourceValue;
    }
    //endregion

}
