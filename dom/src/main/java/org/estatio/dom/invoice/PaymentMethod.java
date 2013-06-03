package org.estatio.dom.invoice;

import org.estatio.dom.utils.StringUtils;

public enum PaymentMethod {

    DIRECT_DEBIT,
    BILLING_ACCOUNT,
    BANK_TRANSFER,
    CASH;

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

}
