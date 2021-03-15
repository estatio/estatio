package org.estatio.module.guarantee.dom;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.agreement.dom.role.AgreementRoleTypeServiceSupportAbstract;
import org.estatio.module.agreement.dom.role.IAgreementRoleType;
import org.estatio.module.agreement.dom.type.IAgreementType;

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

    @Override public String getKey() {
        return name();
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends AgreementRoleTypeServiceSupportAbstract<GuaranteeAgreementRoleTypeEnum> {
        public SupportService() {
            super(GuaranteeAgreementRoleTypeEnum.class);
        }
    }

}
