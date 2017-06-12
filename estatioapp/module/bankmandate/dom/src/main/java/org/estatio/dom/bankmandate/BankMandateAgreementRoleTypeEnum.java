package org.estatio.dom.bankmandate;

import org.estatio.dom.agreement.role.IAgreementRoleType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BankMandateAgreementRoleTypeEnum implements IAgreementRoleType {
    DEBTOR("Debtor"),
    CREDITOR("Creditor"),
    OWNER("Owner");

    @Getter
    private String title;

    public static class Meta {
        public final static int MAX_LEN = 30;

        private Meta() {
        }
    }
}
