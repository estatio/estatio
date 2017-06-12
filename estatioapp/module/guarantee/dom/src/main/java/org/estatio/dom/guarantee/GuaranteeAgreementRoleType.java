package org.estatio.dom.guarantee;

import org.estatio.dom.agreement.role.IAgreementRoleType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum GuaranteeAgreementRoleType implements IAgreementRoleType {
    GUARANTEE("Guarantee"), // One to whom a guaranty is made
    GUARANTOR("Guarantor"), //One who makes a guaranty
    BANK("Bank");

    @Getter
    private String title;

}
