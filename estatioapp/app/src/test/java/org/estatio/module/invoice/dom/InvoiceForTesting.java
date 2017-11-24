package org.estatio.module.invoice.dom;

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
