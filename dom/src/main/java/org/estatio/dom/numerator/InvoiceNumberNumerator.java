package org.estatio.dom.numerator;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.estatio.dom.invoice.Invoice;

@PersistenceCapable(/*serializeRead = "true"*/)
@Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
public class InvoiceNumberNumerator extends Numerator {

    public boolean assign(Invoice invoice){
        if (invoice.getInvoiceNumber() != null) {
            return false;
        } 
        invoice.setInvoiceNumber(String.format("%s-%05d", "TEST", increment()));
        return true;
    }
}
