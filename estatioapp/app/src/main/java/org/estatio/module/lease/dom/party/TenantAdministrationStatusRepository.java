package org.estatio.module.lease.dom.party;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.party.dom.Party;

@DomainService(nature = NatureOfService.DOMAIN,
        repositoryFor = TenantAdministrationStatus.class,
        objectType = "party.TenantAdministrationStatusRepository")
public class TenantAdministrationStatusRepository {

    public TenantAdministrationStatus findStatus(final Party tenant){
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        TenantAdministrationStatus.class,
                        "findStatus",
                        "tenant", tenant));
    }

    public TenantAdministrationStatus upsert(final AdministrationStatus status, final Party tenant, @Nullable final LocalDate judicialRedressDate){
        TenantAdministrationStatus tenantStatus = findStatus(tenant);
        if (tenantStatus != null) {
            tenantStatus.setStatus(status);
            tenantStatus.setJudicialRedressDate(judicialRedressDate);
        } else {
            tenantStatus = create(status, tenant, judicialRedressDate);
        }
        return tenantStatus;
    }

    private TenantAdministrationStatus create(final AdministrationStatus status, final Party tenant, final LocalDate judicialRedressDate) {
        TenantAdministrationStatus tenantStatus = new TenantAdministrationStatus();
        tenantStatus.setTenant(tenant);
        tenantStatus.setStatus(status);
        tenantStatus.setJudicialRedressDate(judicialRedressDate);
        repositoryService.persistAndFlush(tenantStatus);
        return tenantStatus;
    }

    public List<TenantAdministrationStatus> listAll(){
        return repositoryService.allInstances(TenantAdministrationStatus.class);
    }

    @Inject
    RepositoryService repositoryService;
}
