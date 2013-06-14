package org.estatio.dom.lease;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.Mask;
import org.apache.isis.applib.annotation.MemberOrder;

public class LeaseTermForTesting extends LeaseTerm {

    @Override
    @MemberOrder(sequence = "4")
    @Mask("")
    public BigDecimal getApprovedValue() {
        return value;
    }

    @Override
    @MemberOrder(sequence = "5")
    @Mask("")
    public BigDecimal getTrialValue() {
        return value;
    }

    // //////////////////////////////////////

    private BigDecimal value;

    public void setValue(final BigDecimal value) {
        this.value = value;
    }

}
