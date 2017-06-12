package org.estatio.dom.lease;

import org.estatio.dom.agreement.role.IAgreementRoleType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum LeaseAgreementRoleTypeEnum implements IAgreementRoleType {
    LANDLORD("Landlord"),
    TENANT("Tenant"),
    TENANTS_ASSOCIATION("Tenants association"),
    MANAGER("Manager");

    @Getter
    private String title;

}
