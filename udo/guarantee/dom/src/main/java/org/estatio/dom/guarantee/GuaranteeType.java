package org.estatio.dom.guarantee;

import org.incode.module.base.dom.utils.StringUtils;

import org.estatio.dom.financial.FinancialAccountType;

public enum GuaranteeType {
    BANK_GUARANTEE(FinancialAccountType.BANK_GUARANTEE, false),
    DEPOSIT(FinancialAccountType.GUARANTEE_DEPOSIT, false),
    COMPANY_GUARANTEE(null, true),
    NONE(null, true),
    UNKNOWN(null, true);

    private FinancialAccountType financialAccountType;
    private boolean mutable;

    private GuaranteeType(FinancialAccountType financialAccountType, final boolean mutable) {
        this.financialAccountType = financialAccountType;
        this.mutable = mutable;
    }

    public FinancialAccountType getFinancialAccountType() {
        return this.financialAccountType;
    }

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    public boolean isMutable() {
        return mutable;
    }
}
