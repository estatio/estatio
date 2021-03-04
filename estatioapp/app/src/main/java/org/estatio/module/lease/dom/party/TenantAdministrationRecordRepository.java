package org.estatio.module.lease.dom.party;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.party.dom.Party;

@DomainService(nature = NatureOfService.DOMAIN,
        repositoryFor = TenantAdministrationRecord.class,
        objectType = "party.TenantAdministrationRecordRepository")
public class TenantAdministrationRecordRepository {

    public TenantAdministrationRecord findUnique(final Party tenant, final AdministrationStatus status){
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        TenantAdministrationRecord.class,
                        "findByTenantAndStatus",
                        "tenant", tenant,
                        "status", status));
    }

    public TenantAdministrationRecord upsertOrCreateNext(
            final AdministrationStatus status,
            final Party tenant,
            final LocalDate judicialRedressDate) {
        TenantAdministrationRecord tenantStatus = findUnique(tenant, status);
        if (tenantStatus != null) {
            tenantStatus.setJudicialRedressDate(judicialRedressDate);
            return tenantStatus;
        } else {
            return create(status, tenant, judicialRedressDate, latestForParty(tenant));
        }
    }

    public TenantAdministrationRecord latestForParty(final Party tenant){
        return findByTenant(tenant).stream().filter(s->s.getNext()==null).findFirst().orElse(null);
    }

    public List<TenantAdministrationRecord> findByTenant(final Party tenant){
        return repositoryService.allMatches(
                new QueryDefault<>(
                        TenantAdministrationRecord.class,
                        "findByTenant",
                        "tenant", tenant));
    }

    private TenantAdministrationRecord create(final AdministrationStatus status, final Party tenant, final LocalDate judicialRedressDate, final TenantAdministrationRecord previous) {
        TenantAdministrationRecord tenantStatus = new TenantAdministrationRecord();
        tenantStatus.setTenant(tenant);
        tenantStatus.setStatus(status);
        tenantStatus.setJudicialRedressDate(judicialRedressDate);
        tenantStatus.setPrevious(previous);
        if (previous!=null){
            previous.setNext(tenantStatus);
            tenantStatus.setComments(previous.getComments());
        }
        serviceRegistry2.injectServicesInto(tenantStatus);
        repositoryService.persistAndFlush(tenantStatus);
        return tenantStatus;
    }

    public List<TenantAdministrationRecord> listAll(){
        return repositoryService.allInstances(TenantAdministrationRecord.class);
    }

    @Inject
    RepositoryService repositoryService;

    @Inject ServiceRegistry2 serviceRegistry2;
}
