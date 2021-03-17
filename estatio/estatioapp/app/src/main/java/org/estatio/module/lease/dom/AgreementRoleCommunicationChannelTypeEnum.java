package org.estatio.module.lease.dom;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.agreement.dom.commchantype.AgreementRoleCommunicationChannelTypeServiceSupportAbstract;
import org.estatio.module.agreement.dom.commchantype.IAgreementRoleCommunicationChannelType;
import org.estatio.module.agreement.dom.type.IAgreementType;

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
