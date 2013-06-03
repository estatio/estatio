package org.estatio.dom.invoice;

import com.google.common.collect.Ordering;

import org.estatio.dom.financial.FinancialAccountType;
import org.estatio.dom.utils.StringUtils;

public enum InvoiceStatus {

    NEW, 
    APPROVED, 
    COLLECTED, 
    INVOICED;

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    public static final Ordering<FinancialAccountType> ORDERING_BY_TYPE = 
            Ordering.<FinancialAccountType>natural().nullsFirst();

}
