package org.estatio.dom.financial;

import com.google.common.collect.Ordering;

import org.estatio.dom.utils.StringUtils;

public enum BankAccountType {

    DEPOSIT, 
    CHECKING;

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    public static Ordering<BankAccountType> ORDERING_BY_TYPE = 
            Ordering.<BankAccountType> natural().nullsFirst();

}
