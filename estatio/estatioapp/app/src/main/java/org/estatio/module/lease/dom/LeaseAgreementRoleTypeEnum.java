package org.estatio.module.lease.dom;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.agreement.dom.role.AgreementRoleTypeServiceSupportAbstract;
import org.estatio.module.agreement.dom.role.IAgreementRoleType;
import org.estatio.module.agreement.dom.type.IAgreementType;

import static org.estatio.module.lease.dom.LeaseAgreementTypeEnum.LEASE;

public enum LeaseAgreementRoleTypeEnum implements IAgreementRoleType {

    LANDLORD,
    TENANT,
    TENANTS_ASSOCIATION,
    MANAGER;

    @Override
    public IAgreementType getAppliesTo() {
        return LEASE;
    }

    @Override public String getKey() {
        return name();
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends AgreementRoleTypeServiceSupportAbstract<LeaseAgreementRoleTypeEnum> {
        public SupportService() {
            super(LeaseAgreementRoleTypeEnum.class);
        }
    }

}
