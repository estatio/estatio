package org.estatio.dom.lease;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;

import org.estatio.dom.PowerType;
import org.estatio.dom.utils.StringUtils;


public enum LeaseItemType implements PowerType<LeaseTerm>{

    RENT(LeaseTermForIndexableRent.class), 
    TURNOVER_RENT(LeaseTermForTurnoverRent.class),
    SERVICE_CHARGE(LeaseTermForServiceCharge.class);
    //DISCOUNT("Discount", LeaseTerm.class);

    private final Class<? extends LeaseTerm> clss;

    private LeaseItemType(Class<? extends LeaseTerm> clss) {
        this.clss = clss;
    }

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    // //////////////////////////////////////
    
    public LeaseTerm create(DomainObjectContainer container){ 
        try {
            LeaseTerm term = container.newTransientInstance(clss);
            return term;
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }
}
