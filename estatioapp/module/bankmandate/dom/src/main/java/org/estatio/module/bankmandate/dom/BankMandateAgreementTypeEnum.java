package org.estatio.module.bankmandate.dom;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.agreement.dom.type.AgreementTypeServiceSupportAbstract;
import org.estatio.module.agreement.dom.type.IAgreementType;

public enum BankMandateAgreementTypeEnum implements IAgreementType {
    MANDATE;

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends AgreementTypeServiceSupportAbstract<BankMandateAgreementTypeEnum> {
        public SupportService() {
            super(BankMandateAgreementTypeEnum.class);
        }
    }

}
