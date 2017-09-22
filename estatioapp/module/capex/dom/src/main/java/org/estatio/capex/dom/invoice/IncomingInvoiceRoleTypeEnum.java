package org.estatio.capex.dom.invoice;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.party.role.IPartyRoleType;
import org.estatio.dom.party.role.PartyRoleTypeServiceSupportAbstract;

public enum IncomingInvoiceRoleTypeEnum implements IPartyRoleType {

    ECP,
    SUPPLIER;

    @Override
    public String getKey() {
        return this.name();
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends PartyRoleTypeServiceSupportAbstract<IncomingInvoiceRoleTypeEnum> {
        public SupportService() {
            super(IncomingInvoiceRoleTypeEnum.class);
        }
    }

}
