package org.estatio.dom.bankmandate;

import org.estatio.dom.agreement.type.IAgreementType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BankMandateAgreementTypeEnum implements IAgreementType {
    MANDATE("Mandate");

    @Getter
    private String title;
}
