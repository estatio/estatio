package org.estatio.module.lease.dom.party;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.services.clock.ClockService;
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
        TenantAdministrationRecord tenantAdministrationRecord = findUnique(tenant, status);
        final TenantAdministrationRecord latestRecord = latestForParty(tenant);
        if (tenantAdministrationRecord != null) {
            if (tenantAdministrationRecord.equals(latestRecord)) {
                tenantAdministrationRecord.setJudicialRedressDate(judicialRedressDate);
                return tenantAdministrationRecord;
            }
            // status already visited; do nothing
            return null;
        }
        else {
            return latestRecord!=null && latestRecord.getStatus().equals(AdministrationStatus.LIQUIDATION) ? null : create(status, tenant, judicialRedressDate, latestRecord);
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

    private TenantAdministrationRecord create(
            final AdministrationStatus status,
            final Party tenant,
            final LocalDate judicialRedressDate,
            final TenantAdministrationRecord previous) {
        TenantAdministrationRecord tenantAdministrationRecord = new TenantAdministrationRecord();
        serviceRegistry2.injectServicesInto(tenantAdministrationRecord);
        tenantAdministrationRecord.setTenant(tenant);
        tenantAdministrationRecord.setStatus(status);
        tenantAdministrationRecord.setJudicialRedressDate(judicialRedressDate);
        tenantAdministrationRecord.setStatusChangedDate(clockService.now());
        tenantAdministrationRecord.setPrevious(previous);
        if (previous!=null){
            previous.setNext(tenantAdministrationRecord);
            tenantAdministrationRecord.setComments(previous.getComments());
            for (TenantAdministrationLeaseDetails ld : previous.getLeaseDetails()){
                tenantAdministrationRecord.addLeaseDetails(ld.getLease(), ld.getDeclaredAmountOfClaim(), ld.getDebtAdmitted(), ld.getAdmittedAmountOfClaim(), ld.getLeaseContinued());
            }
        }
        repositoryService.persistAndFlush(tenantAdministrationRecord);
        return tenantAdministrationRecord;
    }

    public List<TenantAdministrationRecord> listAll(){
        return repositoryService.allInstances(TenantAdministrationRecord.class);
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ClockService clockService;

    @Inject ServiceRegistry2 serviceRegistry2;
}
