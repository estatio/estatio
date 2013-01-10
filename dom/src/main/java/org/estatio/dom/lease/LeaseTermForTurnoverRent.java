package org.estatio.dom.lease;

import java.math.BigDecimal;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.ObjectType;
import org.estatio.dom.index.Index;
import org.apache.isis.applib.annotation.MemberOrder;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
//@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@Discriminator("LTRT")
//required since subtypes are rolling-up
@ObjectType("LTRT")
public class LeaseTermForTurnoverRent extends LeaseTerm {

    // {{ BudetValue (property)
    private BigDecimal budgetValue;

    @MemberOrder(sequence = "11", name="Turnover Rent")
    public BigDecimal getBudetValue() {
        return budgetValue;
    }

    public void setBudetValue(final BigDecimal budgetValue) {
        this.budgetValue = budgetValue;
    }

    // }}

    // {{ AuditedValue (property)
    private BigDecimal auditedValue;

    @MemberOrder(sequence = "12", name="Turnover Rent")
    public BigDecimal getAuditedValue() {
        return auditedValue;
    }

    public void setAuditedValue(final BigDecimal auditedValue) {
        this.auditedValue = auditedValue;
    }
    // }}

}
