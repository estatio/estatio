package org.estatio.module.lease.dom.party;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.estatio.module.asset.dom.counts.Count;
import org.estatio.module.budget.dom.budgetitem.BudgetItemValue;
import org.estatio.module.party.dom.Party;

import javax.inject.Inject;

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

    public TenantAdministrationStatus upsert(final AdministrationStatus status, final Party tenant){
        TenantAdministrationStatus tenantStatus = findStatus(tenant);
        if (tenantStatus != null) {
            tenantStatus.setStatus(status);
        } else {
            tenantStatus = create(status, tenant);
        }
        return tenantStatus;
    }

    private TenantAdministrationStatus create(final AdministrationStatus status, final Party tenant) {
        TenantAdministrationStatus tenantStatus = new TenantAdministrationStatus();
        tenantStatus.setTenant(tenant);
        tenantStatus.setStatus(status);
        repositoryService.persistAndFlush(tenantStatus);
        return tenantStatus;
    }

    @Inject
    RepositoryService repositoryService;
}
