package org.estatio.dom.lease;

import org.estatio.dom.agreement.IAgreementRoleCommunicationChannelType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AgreementRoleCommunicationChannelTypeEnum implements IAgreementRoleCommunicationChannelType {
    INVOICE_ADDRESS("Invoice Address"),
    ADMINISTRATION_ADDRESS("Administration Address");

    @Getter
    private String title;

    public boolean equalTo(final IAgreementRoleCommunicationChannelType agreementRoleCommunicationChannelType) {
        return getTitle().equals(agreementRoleCommunicationChannelType.getTitle());
    }

}
