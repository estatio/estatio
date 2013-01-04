package org.estatio.dom.invoice;

import javax.jdo.annotations.PersistenceCapable;


import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;
import org.estatio.dom.tax.Tax;

@PersistenceCapable
public class Charge extends AbstractDomainObject {

    // {{ Reference (property)
    private String reference;

    @Title
    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }
    // }}

    // {{ Code (property)
    private String code;

    @MemberOrder(sequence = "1")
    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
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

    @MemberOrder(sequence = "2")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
    // }}

}
