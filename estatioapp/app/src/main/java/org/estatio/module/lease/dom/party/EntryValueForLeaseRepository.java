package org.estatio.module.lease.dom.party;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(nature = NatureOfService.DOMAIN,
        repositoryFor = EntryValueForLease.class,
        objectType = "party.EntryValueForLeaseRepository")
public class EntryValueForLeaseRepository {

    public EntryValueForLease findUnique(final ContinuationPlanEntry continuationPlanEntry, final TenantAdministrationLeaseDetails leaseDetails){
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        EntryValueForLease.class,
                        "findUnique",
                        "continuationPlanEntry", continuationPlanEntry,
                        "leaseDetails", leaseDetails)
        );
    }

    public List<EntryValueForLease> findByLeaseDetails(final TenantAdministrationLeaseDetails leaseDetails) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        EntryValueForLease.class,
                        "findByLeaseDetails",
                        "leaseDetails", leaseDetails)
        );
    }

    public EntryValueForLease upsert(final ContinuationPlanEntry continuationPlanEntry,
            final TenantAdministrationLeaseDetails leaseDetails,
            final BigDecimal amount){
        final EntryValueForLease unique = findUnique(continuationPlanEntry, leaseDetails);
        if (unique !=null){
            unique.setAmount(amount);
            return unique;
        } else {
            return create(continuationPlanEntry, leaseDetails, amount);
        }
    }

    private EntryValueForLease create(
            final ContinuationPlanEntry continuationPlanEntry,
            final TenantAdministrationLeaseDetails leaseDetails,
            final BigDecimal amount){
        EntryValueForLease valueForLease = new EntryValueForLease();
        valueForLease.setContinuationPlanEntry(continuationPlanEntry);
        valueForLease.setLeaseDetails(leaseDetails);
        valueForLease.setAmount(amount);
        repositoryService.persistAndFlush(valueForLease);
        return valueForLease;
    }

    public List<EntryValueForLease> listAll(){
        return repositoryService.allInstances(EntryValueForLease.class);
    }

    @Inject
    RepositoryService repositoryService;
}
