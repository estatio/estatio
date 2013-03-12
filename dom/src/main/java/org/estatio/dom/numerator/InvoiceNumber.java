package org.estatio.dom.numerator;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;

import org.estatio.dom.invoice.Invoice;

//@PersistenceCapable(serializeRead = "true")
@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
public class InvoiceNumber extends Numerator {

    public void assign(Invoice invoice){
        if (invoice.getInvoiceNumber() == null) {
            invoice.setInvoiceNumber(String.format("%s-%05d", "TEST", increment()));
        } else {
            //TODO: it's not fun doing unit tests without a container
            //getContainer().warnUser("Unable to assign a number to an invoice multiple times");
        }
    }
}
