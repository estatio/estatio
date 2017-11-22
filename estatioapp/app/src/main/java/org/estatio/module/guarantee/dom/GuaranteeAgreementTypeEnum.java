package org.estatio.module.guarantee.dom;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.agreement.dom.type.AgreementTypeServiceSupportAbstract;
import org.estatio.module.agreement.dom.type.IAgreementType;

public enum GuaranteeAgreementTypeEnum implements IAgreementType {
    GUARANTEE;

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends AgreementTypeServiceSupportAbstract<GuaranteeAgreementTypeEnum> {
        public SupportService() {
            super(GuaranteeAgreementTypeEnum.class);
        }
    }

}
