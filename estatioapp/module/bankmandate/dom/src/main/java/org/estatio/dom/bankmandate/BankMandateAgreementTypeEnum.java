package org.estatio.dom.bankmandate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.agreement.type.AgreementTypeServiceSupportAbstract;
import org.estatio.dom.agreement.type.IAgreementType;

public enum BankMandateAgreementTypeEnum implements IAgreementType {
    MANDATE;

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends AgreementTypeServiceSupportAbstract<BankMandateAgreementTypeEnum> {
        public SupportService() {
            super(BankMandateAgreementTypeEnum.class);
        }
    }

}
