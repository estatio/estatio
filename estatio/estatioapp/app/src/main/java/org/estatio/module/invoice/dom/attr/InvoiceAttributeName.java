package org.estatio.module.invoice.dom.attr;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum InvoiceAttributeName {
    PRELIMINARY_LETTER_DESCRIPTION("preliminaryLetterDescription"),
    PRELIMINARY_LETTER_COMMENT("preliminaryLetterComment"),
    INVOICE_DESCRIPTION("description"),
    INVOICE_COMMENT("invoiceComment"),
    ;

    @Getter
    private String fragmentName;

}
