package org.estatio.module.lease.dom.party;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(nature = NatureOfService.DOMAIN,
        repositoryFor = ContinuationPlanEntry.class,
        objectType = "party.ContinuationPlanEntryRepository")
public class ContinuationPlanEntryRepository {

    public ContinuationPlanEntry findUnique(final ContinuationPlan continuationPlan, final LocalDate date){
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        ContinuationPlanEntry.class,
                        "findUnique",
                        "continuationPlan", continuationPlan,
                        "date", date)
        );
    }

    public ContinuationPlanEntry upsert(final ContinuationPlan plan, final LocalDate date, final BigDecimal percentage){
        final ContinuationPlanEntry unique = findUnique(plan, date);
        if (unique !=null){
            unique.setPercentage(percentage);
            return unique;
        } else {
            return create(plan, date, percentage);
        }
    }

    private ContinuationPlanEntry create(final ContinuationPlan plan, final LocalDate date, final BigDecimal percentage){
        ContinuationPlanEntry entry = new ContinuationPlanEntry();
        entry.setContinuationPlan(plan);
        entry.setDate(date);
        entry.setPercentage(percentage);
        serviceRegistry2.injectServicesInto(entry);
        repositoryService.persistAndFlush(entry);
        return entry;
    }

    public List<ContinuationPlanEntry> listAll(){
        return repositoryService.allInstances(ContinuationPlanEntry.class);
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

}
