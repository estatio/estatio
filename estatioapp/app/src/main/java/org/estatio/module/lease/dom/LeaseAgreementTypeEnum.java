package org.estatio.module.lease.dom;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.agreement.dom.type.AgreementTypeServiceSupportAbstract;
import org.estatio.module.agreement.dom.type.IAgreementType;

public enum LeaseAgreementTypeEnum implements IAgreementType {
    LEASE;

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends AgreementTypeServiceSupportAbstract<LeaseAgreementTypeEnum> {
        public SupportService() {
            super(LeaseAgreementTypeEnum.class);
        }
    }

}
