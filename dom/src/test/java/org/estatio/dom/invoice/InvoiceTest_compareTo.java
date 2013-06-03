package org.estatio.dom.invoice;

import java.util.List;

import org.estatio.dom.ComparableContractTest_compareTo;


public class InvoiceTest_compareTo extends ComparableContractTest_compareTo<Invoice> {

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<Invoice>> orderedTuples() {
        return listOf(
                listOf(
                        newInvoice(null),
                        newInvoice("0000123"),
                        newInvoice("0000123"),
                        newInvoice("0000124")
                        )
                );
    }

    private Invoice newInvoice(String number) {
        final Invoice inv = new Invoice();
        inv.setInvoiceNumber(number);
        return inv;
    }

}
