package org.estatio.dom.invoice;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;

import org.estatio.dom.ComparableContractTest_compareTo;


public class InvoiceItemTest_compareTo extends ComparableContractTest_compareTo<InvoiceItem> {

    private Invoice inv;
    private Invoice inv2;

    @Before
    public void setUpParentInvoices() throws Exception {
        inv = new Invoice();
        inv.setInvoiceNumber("000001");
        
        inv2 = new Invoice();
        inv2.setInvoiceNumber("000002");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected List<List<InvoiceItem>> orderedTuples() {
        return listOf(
                listOf(
                        newInvoiceItem(null, null, null, null),
                        newInvoiceItem(inv, null, null, null),
                        newInvoiceItem(inv, null, null, null),
                        newInvoiceItem(inv2, null, null, null)
                        ),
                listOf(
                        newInvoiceItem(inv, null, null, null),
                        newInvoiceItem(inv, new LocalDate(2012,3,1), null, null),
                        newInvoiceItem(inv, new LocalDate(2012,3,1), null, null),
                        newInvoiceItem(inv, new LocalDate(2012,3,2), null, null)
                        ),
                listOf(
                        newInvoiceItem(inv, new LocalDate(2012,3,1), null, null),
                        newInvoiceItem(inv, new LocalDate(2012,3,1), new LocalDate(2012,4,1), null),
                        newInvoiceItem(inv, new LocalDate(2012,3,1), new LocalDate(2012,4,1), null),
                        newInvoiceItem(inv, new LocalDate(2012,3,1), new LocalDate(2012,4,2), null)
                        ),
                listOf(
                        newInvoiceItem(inv, new LocalDate(2012,3,1), new LocalDate(2012,4,1), null),
                        newInvoiceItem(inv, new LocalDate(2012,3,1), new LocalDate(2012,4,1), "ABC"),
                        newInvoiceItem(inv, new LocalDate(2012,3,1), new LocalDate(2012,4,1), "ABC"),
                        newInvoiceItem(inv, new LocalDate(2012,3,1), new LocalDate(2012,4,1), "DEF")
                        )
                );
    }

    private InvoiceItem newInvoiceItem(
            Invoice invoice,
            LocalDate startDate,
            LocalDate dueDate,
            String description) {
        final InvoiceItem ib = new InvoiceItem(){
            @Override
            public void attachToInvoice() {
            }
        };
        ib.setInvoice(invoice);
        ib.setStartDate(startDate);
        ib.setDueDate(dueDate);
        ib.setDescription(description);
        return ib;
    }

}
