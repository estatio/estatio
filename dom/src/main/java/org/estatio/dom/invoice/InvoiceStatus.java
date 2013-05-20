package org.estatio.dom.invoice;

import org.estatio.dom.utils.StringUtils;

public enum InvoiceStatus {

    NEW, 
    APPROVED, 
    COLLECTED, 
    INVOICED;

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

}
