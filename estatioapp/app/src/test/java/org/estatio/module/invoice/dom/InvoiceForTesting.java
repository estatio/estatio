package org.estatio.module.invoice.dom;

import javax.jdo.annotations.DiscriminatorStrategy;

import org.apache.isis.applib.annotation.DomainObject;

@DomainObject(
        objectType = "org.estatio.module.invoice.dom.InvoiceForTesting"
)
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.VALUE_MAP,
        column = "discriminator",
        value = "org.estatio.module.invoice.dom.InvoiceForTesting"
)
public class InvoiceForTesting extends Invoice<InvoiceForTesting> {

    public InvoiceForTesting() {
        super("");
    }

    @Override
    protected String reasonDisabledDueToState(final Object viewContext) {
        return null;
    }

    @Override
    public String reasonDisabledFinanceDetailsDueToState(final Object viewContext) {
        return null;
    }

}
