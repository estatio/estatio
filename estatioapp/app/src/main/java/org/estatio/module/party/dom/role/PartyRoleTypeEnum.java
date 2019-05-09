package org.estatio.module.party.dom.role;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

public enum PartyRoleTypeEnum implements IPartyRoleType {

    OFFICE_ADMINISTRATOR,
    INCOMING_INVOICE_MANAGER,
    COUNTRY_DIRECTOR,
    TREASURER,
    CORPORATE_ADMINISTRATOR,
    CORPORATE_MANAGER,
    ORDER_MANAGER,
    PREFERRED_MANAGER,
    PREFERRED_DIRECTOR,
    ADVISOR,
    TECHNICIAN;


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
