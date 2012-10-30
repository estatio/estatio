package com.eurocommercialproperties.estatio.dom.invoice;

import java.math.BigDecimal;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;

import com.eurocommercialproperties.estatio.dom.lease.LeaseTerm;
import com.eurocommercialproperties.estatio.dom.tax.Tax;

@PersistenceCapable
public class InvoiceItem extends AbstractDomainObject {

    // {{ Invoice (property)
    private Invoice invoice;

    @Disabled
    @MemberOrder(sequence = "1")
    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(final Invoice invoice) {
        this.invoice = invoice;
    }

    // }}

    // {{ Quantity (property)
    private BigDecimal quantity;

    @MemberOrder(sequence = "1")
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(final BigDecimal quantity) {
        this.quantity = quantity;
    }

    // }}

    // {{ Amount (property)
    private BigDecimal amount;

    @MemberOrder(sequence = "1")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    // }}

    // {{ Tax (property)
    private Tax tax;

    @MemberOrder(sequence = "1")
    public Tax getTax() {
        return tax;
    }

    public void setTax(final Tax tax) {
        this.tax = tax;
    }

    // }}

    // {{ Description (property)
    private String description;

    @MemberOrder(sequence = "1")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // }}

    // {{ LeaseTerm (property)
    private LeaseTerm leaseTerm;

    @Optional
    @Disabled
    @MemberOrder(sequence = "1")
    public LeaseTerm getLeaseTerm() {
        return leaseTerm;
    }

    public void setLeaseTerm(final LeaseTerm leaseTerm) {
        this.leaseTerm = leaseTerm;
    }
    // }}

}
