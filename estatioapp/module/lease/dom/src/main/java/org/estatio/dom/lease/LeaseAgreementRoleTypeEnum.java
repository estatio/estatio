package org.estatio.dom.lease;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.agreement.role.AgreementRoleTypeServiceSupportAbstract;
import org.estatio.dom.agreement.role.IAgreementRoleType;
import org.estatio.dom.agreement.type.IAgreementType;

import static org.estatio.dom.lease.LeaseAgreementTypeEnum.LEASE;

public enum LeaseAgreementRoleTypeEnum implements IAgreementRoleType {

    LANDLORD,
    TENANT,
    TENANTS_ASSOCIATION,
    MANAGER;

    @Override
    public IAgreementType getAppliesTo() {
        return LEASE;
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends AgreementRoleTypeServiceSupportAbstract<LeaseAgreementRoleTypeEnum> {
        public SupportService() {
            super(LeaseAgreementRoleTypeEnum.class);
        }
    }

}
