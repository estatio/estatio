package org.estatio.dom.invoice;

import org.estatio.dom.WithIntervalContractTest_getInterval;

public class InvoiceItemTest_getInterval extends WithIntervalContractTest_getInterval<InvoiceItem> {

    protected InvoiceItem newWithInterval() {
        return new InvoiceItemForTesting();
    }
}
