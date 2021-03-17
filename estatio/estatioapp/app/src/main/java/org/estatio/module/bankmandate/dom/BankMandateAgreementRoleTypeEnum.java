package org.estatio.module.bankmandate.dom;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.agreement.dom.role.AgreementRoleTypeServiceSupportAbstract;
import org.estatio.module.agreement.dom.role.IAgreementRoleType;
import org.estatio.module.agreement.dom.type.IAgreementType;

public enum BankMandateAgreementRoleTypeEnum implements IAgreementRoleType {
    DEBTOR,
    CREDITOR,
    OWNER;

    @Override
    public IAgreementType getAppliesTo() {
        return BankMandateAgreementTypeEnum.MANDATE;
    }

    @Override public String getKey() {
        return name();
    }
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends
            AgreementRoleTypeServiceSupportAbstract<BankMandateAgreementRoleTypeEnum> {
        public SupportService() {
            super(BankMandateAgreementRoleTypeEnum.class);
        }
    }


}
