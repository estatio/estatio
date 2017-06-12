package org.estatio.dom.party;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.party.role.IPartyRoleType;
import org.estatio.dom.party.role.PartyRoleTypeServiceSupportAbstract;

public enum PartyRoleTypeEnum implements IPartyRoleType {

    MAIL_ROOM,
    COUNTRY_DIRECTOR,
    COUNTRY_ADMINISTRATOR,
    TREASURER;


    @Override
    public String getKey() {
        return this.name();
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends PartyRoleTypeServiceSupportAbstract<PartyRoleTypeEnum> {
        public SupportService() {
            super(PartyRoleTypeEnum.class);
        }
    }

}
