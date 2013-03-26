package org.estatio.dom.numerator;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.estatio.dom.invoice.Invoice;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;

@PersistenceCapable(/*serializeRead = "true"*/)
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class NumeratorForInvoiceNumber extends Numerator {

    public String iconName() {
        return "Numerator";
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @Hidden
    public boolean assign(Invoice invoice){
        if (invoice.getInvoiceNumber() != null) {
            return false;
        } 
        invoice.setInvoiceNumber(String.format("INV-%05d", increment()));
        return true;
    }
    
    // {{ Dummy (property)
    private String dummy;

    @MemberOrder(sequence = "1")
    public String getDummy() {
        return dummy;
    }

    public void setDummy(final String dummy) {
        this.dummy = dummy;
    }
    // }}


}
