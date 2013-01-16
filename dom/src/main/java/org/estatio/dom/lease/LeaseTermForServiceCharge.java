package org.estatio.dom.lease;

import java.math.BigDecimal;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.MemberOrder;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
//@Discriminator("LTRI")
public class LeaseTermForServiceCharge extends LeaseTerm {

    // {{ BudgetedValue (property)
    private BigDecimal budgetedValue;

    @MemberOrder(sequence = "11", name = "Service Charges")
    public BigDecimal getBudgetedValue() {
        return budgetedValue;
    }

    public void setBudgetedValue(final BigDecimal budgetedValue) {
        this.budgetedValue = budgetedValue;
    }

    // }}

    // {{ AuditedValue (property)
    private BigDecimal auditedValue;

    @MemberOrder(sequence = "12", name = "Service Charges")
    public BigDecimal getAuditedValue() {
        return auditedValue;
    }

    public void setAuditedValue(final BigDecimal auditedValue) {
        this.auditedValue = auditedValue;
    }
    // }}

}
