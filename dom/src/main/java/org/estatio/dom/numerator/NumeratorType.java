package org.estatio.dom.numerator;

import org.estatio.dom.utils.StringUtils;

public enum NumeratorType {
    INVOICE_NUMBER, 
    COLLECTION_NUMBER;

    public String title() {
        return StringUtils.enumTitle(this.toString());
    }

    public String description() {
        return StringUtils.enumTitle(this.toString());
    }
}
