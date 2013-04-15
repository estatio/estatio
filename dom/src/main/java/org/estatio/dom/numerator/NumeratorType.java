package org.estatio.dom.numerator;

import org.estatio.dom.utils.StringUtils;

public enum NumeratorType {
    INVOICE_NUMBER("Invoice Number"), 
    COLLECTION_NUMBER("Collection Number");

    private String description;

    private NumeratorType(String description) {
        this.description = description;
    }

    public String title() {
        return StringUtils.enumTitle(this.toString());
    }

    public String description() {
        return description;
    }
}
