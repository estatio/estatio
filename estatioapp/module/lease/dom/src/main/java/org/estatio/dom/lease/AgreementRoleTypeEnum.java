package org.estatio.dom.lease;

import org.estatio.dom.agreement.IAgreementRoleType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AgreementRoleTypeEnum implements IAgreementRoleType {
    LANDLORD("Landlord"),
    TENANT("Tenant"),
    TENANTS_ASSOCIATION("Tenants association"),
    MANAGER("Manager");

    @Getter
    private String title;

    public static class Meta {
        public final static int MAX_LEN = 30;

        private Meta() {
        }
    }
}
