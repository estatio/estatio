package org.estatio.dom.lease;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.agreement.commchantype.AgreementRoleCommunicationChannelTypeServiceSupportAbstract;
import org.estatio.dom.agreement.commchantype.IAgreementRoleCommunicationChannelType;
import org.estatio.dom.agreement.type.IAgreementType;

public enum AgreementRoleCommunicationChannelTypeEnum implements IAgreementRoleCommunicationChannelType {
    INVOICE_ADDRESS,
    ADMINISTRATION_ADDRESS;

    @Override
    public IAgreementType getAppliesTo() {
        return LeaseAgreementTypeEnum.LEASE;
    }

    public boolean equalTo(final IAgreementRoleCommunicationChannelType agreementRoleCommunicationChannelType) {
        return getTitle().equals(agreementRoleCommunicationChannelType.getTitle());
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService
            extends AgreementRoleCommunicationChannelTypeServiceSupportAbstract<AgreementRoleCommunicationChannelTypeEnum> {
        public SupportService() {
            super(AgreementRoleCommunicationChannelTypeEnum.class);
        }
    }

}
