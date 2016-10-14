package org.estatio.dom.lease.invoicing;

import org.incode.module.base.dom.utils.StringUtils;


public enum InvoiceRunType {
    NORMAL_RUN,
    RETRO_RUN;
    
    public String title() {
        return StringUtils.enumTitle(this.name());
    }
}