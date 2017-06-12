package org.estatio.dom.guarantee;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.agreement.role.AgreementRoleTypeServiceSupportAbstract;
import org.estatio.dom.agreement.role.IAgreementRoleType;
import org.estatio.dom.agreement.type.IAgreementType;

public enum GuaranteeAgreementRoleTypeEnum implements IAgreementRoleType {
    /**
     * One to whom a guarantee is made
     */
    GUARANTEE,
    /**
     * One who makes a guarantee
     */
    GUARANTOR,
    BANK;

    @Override
    public IAgreementType getAppliesTo() {
        return GuaranteeAgreementTypeEnum.GUARANTEE;
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends AgreementRoleTypeServiceSupportAbstract<GuaranteeAgreementRoleTypeEnum> {
        public SupportService() {
            super(GuaranteeAgreementRoleTypeEnum.class);
        }
    }

}
