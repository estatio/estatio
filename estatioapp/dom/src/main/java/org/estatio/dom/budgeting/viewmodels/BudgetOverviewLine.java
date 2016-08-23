package org.estatio.dom.budgeting.viewmodels;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Occupancy;

import lombok.Getter;
import lombok.Setter;

@DomainObject(nature = Nature.VIEW_MODEL)
public class BudgetOverviewLine implements Comparable<BudgetOverviewLine> {

    public BudgetOverviewLine(){}

    public BudgetOverviewLine(final Occupancy occupancy, final Unit unit, final Charge charge, final BigDecimal amount) {

        this.unit = unit;
        this.charge = charge;
        this.amount = amount;
        this.occupancy = occupancy;
        if (occupancy != null) {
            this.lease = occupancy.getLease();
        }

    }

    @Getter @Setter
    private Occupancy occupancy;

    @Getter @Setter
    private Lease lease;

    @Getter @Setter
    private Unit unit;

    @Getter @Setter
    private Charge charge;

    @Getter @Setter
    private BigDecimal amount;


    @Override
    public int compareTo(BudgetOverviewLine o) {

        int result = 0;

        if (this.getOccupancy()!=null && o.getOccupancy()!=null) {
            result = this.getUnit().compareTo(o.getUnit());
        } else {
            if (this.getOccupancy()!=null){
                result = -1;
            } else {
                if (o.getOccupancy()!=null){
                    result = 1;
                }
            }

        }

        if (result==0){
            result = this.getUnit().compareTo(o.getUnit());
        }

        if (result==0){
            result = this.getCharge().compareTo(o.getCharge());
        }

        return result;
    }
}
