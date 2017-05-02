package org.estatio.capex.dom.invoice.rule;

import org.estatio.capex.dom.invoice.IncomingInvoice;

enum IncomingInvoiceRule implements IncomingInvoiceRulesEngine {
    when_new_invoice_for_project_assign {
        @Override public boolean apply(final IncomingInvoice invoice) {

            return false;
        }
    };
}

interface IncomingInvoiceRulesEngine {
    boolean apply(IncomingInvoice invoice);
}