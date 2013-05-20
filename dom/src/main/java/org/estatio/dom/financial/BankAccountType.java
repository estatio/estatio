package org.estatio.dom.financial;

import org.estatio.dom.utils.StringUtils;

public enum BankAccountType {

    DEPOSIT, 
    CHECKING;

    public String title() {
        return StringUtils.enumTitle(this.name());
    }
}
