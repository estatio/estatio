package org.estatio.module.lease.dom.etlutilobjects;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.lease.dom.Lease;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = LeaseEvaluationDateEtlObject.class
)
public class LeaseEvaluationDateEtlObjectRepository {

    @Programmatic
    public List<LeaseEvaluationDateEtlObject> listAll() {
        return repositoryService.allInstances(LeaseEvaluationDateEtlObject.class);
    }

    @Programmatic
    public LeaseEvaluationDateEtlObject findUnique(final Lease lease, final LocalDate leaseEvaluationDate, final LocalDate startDate) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        LeaseEvaluationDateEtlObject.class,
                        "findUnique",
                        "lease", lease,
                        "leaseEvaluationDate", leaseEvaluationDate,
                        "startDate", startDate));
    }

    @Programmatic
    public List<LeaseEvaluationDateEtlObject> findByLease(final Lease lease) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        LeaseEvaluationDateEtlObject.class,
                        "findByLease",
                        "lease", lease));
    }

    @Programmatic
    public LeaseEvaluationDateEtlObject create(
            final Lease lease,
            final LocalDate leaseEvaluationDate,
            final LocalDate startDate){
        final LeaseEvaluationDateEtlObject newObject = new LeaseEvaluationDateEtlObject(lease, leaseEvaluationDate, startDate);
        repositoryService.persistAndFlush(newObject);
        return newObject;
    }

    @Inject
    RepositoryService repositoryService;

}
