package org.estatio.module.lease.dom.party;

import java.util.List;

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

    public TenantAdministrationStatus findUnique(final Party tenant, final AdministrationStatus status){
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        TenantAdministrationStatus.class,
                        "findByTenantAndStatus",
                        "tenant", tenant,
                        "status", status));
    }

    public TenantAdministrationStatus upsertOrCreateNext(
            final AdministrationStatus status,
            final Party tenant,
            final LocalDate judicialRedressDate) {
        TenantAdministrationStatus tenantStatus = findUnique(tenant, status);
        if (tenantStatus != null) {
            tenantStatus.setStatus(status);
            tenantStatus.setJudicialRedressDate(judicialRedressDate);
            return tenantStatus;
        } else {
            return create(status,tenant,judicialRedressDate, latestForParty(tenant));
        }
    }

    public TenantAdministrationStatus latestForParty(final Party tenant){
        return findByTenant(tenant).stream().filter(s->s.getNext()==null).findFirst().orElse(null);
    }

    public List<TenantAdministrationStatus> findByTenant(final Party tenant){
        return repositoryService.allMatches(
                new QueryDefault<>(
                        TenantAdministrationStatus.class,
                        "findByTenant",
                        "tenant", tenant));
    }

    private TenantAdministrationStatus create(final AdministrationStatus status, final Party tenant, final LocalDate judicialRedressDate, final TenantAdministrationStatus previous) {
        TenantAdministrationStatus tenantStatus = new TenantAdministrationStatus();
        tenantStatus.setTenant(tenant);
        tenantStatus.setStatus(status);
        tenantStatus.setJudicialRedressDate(judicialRedressDate);
        tenantStatus.setPrevious(previous);
        if (previous!=null){
            previous.setNext(tenantStatus);
            tenantStatus.setComments(previous.getComments());
        }
        repositoryService.persistAndFlush(tenantStatus);
        return tenantStatus;
    }

    public List<TenantAdministrationStatus> listAll(){
        return repositoryService.allInstances(TenantAdministrationStatus.class);
    }

    @Inject
    RepositoryService repositoryService;

}
