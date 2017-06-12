package org.estatio.dom.lease;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.party.role.IPartyRoleType;
import org.estatio.dom.party.role.PartyRoleTypeServiceSupportAbstract;

public enum LeaseRoleTypeEnum implements IPartyRoleType {
    LANDLORD,
    TENANT;

    @Override
    public String getKey() {
        return this.name();
    }


    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends PartyRoleTypeServiceSupportAbstract<LeaseRoleTypeEnum> {
        public SupportService() {
            super(LeaseRoleTypeEnum.class);
        }
    }

}
