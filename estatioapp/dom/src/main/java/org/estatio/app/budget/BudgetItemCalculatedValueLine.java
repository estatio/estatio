package org.estatio.app.budget;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.lease.LeaseTermForServiceCharge;

@DomainObject(
        nature = Nature.VIEW_MODEL
)
public class BudgetItemCalculatedValueLine {

    public BudgetItemCalculatedValueLine() {}

    public BudgetItemCalculatedValueLine(
            final BigDecimal calculatedValue,
            final LeaseTermForServiceCharge leaseTermForServiceCharge,
            final String status,
            final Unit unit,
            final BigDecimal keyValue

    ){
        this.calculatedValue=calculatedValue;
        this.leaseTermForServiceCharge=leaseTermForServiceCharge;
        this.status=status;
        this.unit=unit;
        this.keyValue=keyValue;
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

    //region > leaseTermForServiceCharge (property)
    private LeaseTermForServiceCharge leaseTermForServiceCharge;

    @MemberOrder(sequence = "2")
    public LeaseTermForServiceCharge getLeaseTermForServiceCharge() {
        return leaseTermForServiceCharge;
    }

    public void setLeaseTermForServiceCharge(final LeaseTermForServiceCharge leaseTermForServiceCharge) {
        this.leaseTermForServiceCharge = leaseTermForServiceCharge;
    }
    //endregion

    //region > status (property)
    private String status;

    @MemberOrder(sequence = "3")
    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }
    //endregion

    //region > unit (property)
    private Unit unit;

    @MemberOrder(sequence = "4")
    public Unit getUnit() {
        return unit;
    }

    public void setUnit(final Unit unit) {
        this.unit = unit;
    }
    //endregion

    //region > keyValue (property)
    private BigDecimal keyValue;

    @MemberOrder(sequence = "5")
    public BigDecimal getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(final BigDecimal keyValue) {
        this.keyValue = keyValue;
    }
    //endregion



}
