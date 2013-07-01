package org.estatio.dom.lease;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.Mask;
import org.apache.isis.applib.annotation.MemberOrder;

public class LeaseTermForTesting extends LeaseTerm {

    @MemberOrder(sequence = "4")
    @Mask("")
    @Override
    public BigDecimal getApprovedValue() {
        return value;
    }

    @MemberOrder(sequence = "5")
    @Mask("")
    @Override
    public BigDecimal getTrialValue() {
        return value;
    }

    // //////////////////////////////////////

    private BigDecimal value;

    public void setValue(final BigDecimal value) {
        this.value = value;
    }

}
