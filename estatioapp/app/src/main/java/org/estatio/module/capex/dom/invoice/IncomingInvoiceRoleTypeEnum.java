package org.estatio.module.capex.dom.invoice;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeServiceSupportAbstract;

public enum IncomingInvoiceRoleTypeEnum implements IPartyRoleType {

    ECP,
    SUPPLIER,
    ECP_MGT_COMPANY;

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
