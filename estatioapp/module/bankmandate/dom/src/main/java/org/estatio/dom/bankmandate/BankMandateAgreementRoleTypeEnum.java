package org.estatio.dom.bankmandate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.agreement.role.AgreementRoleTypeServiceSupportAbstract;
import org.estatio.dom.agreement.role.IAgreementRoleType;
import org.estatio.dom.agreement.type.IAgreementType;

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
