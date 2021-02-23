package org.estatio.module.lease.dom.party;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.party.dom.Party;

@DomainService(nature = NatureOfService.DOMAIN,
        repositoryFor = TenantAdministrationStatus.class,
        objectType = "party.TenantAdministrationStatusRepository")
public class TenantAdministrationStatusRepository {


    public TenantAdministrationStatus upsert(final AdministrationStatus status, final Party tenant){
        // TODO:
        return null;
    }

    public TenantAdministrationStatus findStatus(final Party tenant){
        // TODO:
        return null;
    }


}
