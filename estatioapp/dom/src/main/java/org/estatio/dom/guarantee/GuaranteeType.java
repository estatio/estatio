package org.estatio.dom.guarantee;

import org.estatio.dom.financial.FinancialAccountType;
import org.incode.module.base.dom.utils.StringUtils;

public enum GuaranteeType {
    BANK_GUARANTEE(FinancialAccountType.BANK_GUARANTEE),
    DEPOSIT(FinancialAccountType.GUARANTEE_DEPOSIT),
    COMPANY_GUARANTEE(null),
    NONE(null),
    UNKNOWN(null);

    private GuaranteeType(FinancialAccountType financialAccountType) {
        this.financialAccountType = financialAccountType;
    }

    private FinancialAccountType financialAccountType;

    public FinancialAccountType getFinancialAccountType() {
        return this.financialAccountType;
    }

    public String title() {
        return StringUtils.enumTitle(this.name());
    }
}
