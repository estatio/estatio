package org.estatio.dom.invoice;

public class InvoiceForTesting extends Invoice<InvoiceForTesting> {

    public InvoiceForTesting() {
        super("");
    }

    @Override
    protected String reasonDisabledDueToState(final Object viewContext) {
        return null;
    }
}
