package org.estatio.dom.guarantee;

import org.estatio.dom.agreement.type.IAgreementType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum GuaranteeAgreementTypeEnum implements IAgreementType {
    GUARANTEE("Guarantee");

    @Getter
    private String title;
}
