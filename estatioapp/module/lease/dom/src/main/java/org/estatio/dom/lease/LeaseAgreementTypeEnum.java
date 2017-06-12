package org.estatio.dom.lease;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.agreement.type.AgreementTypeServiceSupportAbstract;
import org.estatio.dom.agreement.type.IAgreementType;

public enum LeaseAgreementTypeEnum implements IAgreementType {
    LEASE;

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends AgreementTypeServiceSupportAbstract<LeaseAgreementTypeEnum> {
        public SupportService() {
            super(LeaseAgreementTypeEnum.class);
        }
    }

}
