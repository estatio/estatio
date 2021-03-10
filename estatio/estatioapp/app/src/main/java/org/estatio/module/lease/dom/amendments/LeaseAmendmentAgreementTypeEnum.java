package org.estatio.module.lease.dom.amendments;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.agreement.dom.type.AgreementTypeServiceSupportAbstract;
import org.estatio.module.agreement.dom.type.IAgreementType;

public enum LeaseAmendmentAgreementTypeEnum implements IAgreementType {
    LEASE_AMENDMENT;

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends AgreementTypeServiceSupportAbstract<LeaseAmendmentAgreementTypeEnum> {
        public SupportService() {
            super(LeaseAmendmentAgreementTypeEnum.class);
        }
    }

}
