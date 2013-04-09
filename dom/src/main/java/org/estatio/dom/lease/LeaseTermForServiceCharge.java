package org.estatio.dom.lease;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.MemberOrder;
import org.estatio.dom.utils.MathUtils;
import org.joda.time.LocalDate;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class LeaseTermForServiceCharge extends LeaseTerm {

    // {{ TermFrequency (property)
    //TODO: investigate weather this can be merged with indextionFrequency
    private LeaseTermFrequency termFrequency;
    
    @MemberOrder(sequence = "10")
    public LeaseTermFrequency getTermFrequency() {
        return termFrequency;
    }

    public void setTermFrequency(final LeaseTermFrequency termFrequency) {
        this.termFrequency = termFrequency;
    }
    // }}
    
    // {{ BudgetedValue (property)
    private BigDecimal budgetedValue;

    @MemberOrder(sequence = "11", name = "Service Charges")
    @Column(scale = 4)
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
    @Column(scale = 4)
    public BigDecimal getAuditedValue() {
        return auditedValue;
    }

    public void setAuditedValue(final BigDecimal auditedValue) {
        this.auditedValue = auditedValue;
    }

    // }}

    @Override
    public LeaseTerm approve() {
        super.approve();
        return this;
    }

    public String disableApprove() {
        return getStatus().equals(LeaseItemStatus.APPROVED) ? "Already approved" : null;
    }
    
    @Override
    public LeaseTerm verify() {
        super.verify();
        if (getStatus() == LeaseTermStatus.NEW) {
            if (MathUtils.isNotZeroOrNull(getAuditedValue())) {
                setValue(getAuditedValue());
            } else {
                if (MathUtils.isNotZeroOrNull(getBudgetedValue())) {
                    setValue(getBudgetedValue());
                }
            }
        }
        return this;
    }

    @Override
    public LeaseTerm createOrUpdateNext() {
        LeaseTermForServiceCharge nextTerm = (LeaseTermForServiceCharge) createOrUpdateNext(this.getEndDate() == null ? this.getTermFrequency().nextDate(this.getStartDate()) : this.getEndDate().plusDays(1));
        if (nextTerm != null){
            //Do term sepecific stuff
            nextTerm.setBudgetedValue(MathUtils.isNotZeroOrNull(getAuditedValue()) ? getAuditedValue(): getBudgetedValue());
            nextTerm.setTermFrequency(getTermFrequency());
            return nextTerm;
        }
        return this;
    }
    
    
}
