package org.estatio.dom.invoice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum InvoiceAttributeName {
    PRELIMINARY_LETTER_DESCRIPTION("preliminaryLetterDescription"),
    PRELIMINARY_LETTER_COMMENT("preliminaryLetterDescription"),
    INVOICE_DESCRIPTION("description");

    @Getter
    private String fragmentName;

}
