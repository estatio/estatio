package org.estatio.module.lease.dom.party;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(nature = NatureOfService.DOMAIN,
        repositoryFor = ContinuationPlan.class,
        objectType = "party.ContinuationPlanRepository")
public class ContinuationPlanRepository {

    public ContinuationPlan findUnique(final TenantAdministrationRecord tenantAdministrationRecord){
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        ContinuationPlan.class,
                        "findUnique",
                        "tenantAdministrationRecord", tenantAdministrationRecord)
        );
    }

    public ContinuationPlan findOrCreate(final TenantAdministrationRecord record, final LocalDate judgmentDate){
        final ContinuationPlan unique = findUnique(record);
        if (unique !=null) return unique;
        return create(record, judgmentDate);
    }

    private ContinuationPlan create(final TenantAdministrationRecord record, final LocalDate judgmentDate){
        ContinuationPlan plan = new ContinuationPlan();
        plan.setTenantAdministrationRecord(record);
        plan.setJudgmentDate(judgmentDate);
        serviceRegistry2.injectServicesInto(plan);
        repositoryService.persistAndFlush(plan);
        return plan;
    }

    public List<ContinuationPlan> listAll(){
        return repositoryService.allInstances(ContinuationPlan.class);
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

}
