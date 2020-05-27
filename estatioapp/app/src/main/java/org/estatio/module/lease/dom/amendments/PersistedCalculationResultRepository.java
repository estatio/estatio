package org.estatio.module.lease.dom.amendments;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = PersistedCalculationResult.class
)
public class PersistedCalculationResultRepository {

    @Programmatic
    public List<PersistedCalculationResult> listAll() {
        return repositoryService.allInstances(PersistedCalculationResult.class);
    }

    @Programmatic
    public List<PersistedCalculationResult> findByLeaseTerm(final LeaseTerm leaseTerm) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        PersistedCalculationResult.class,
                        "findByLeaseTerm",
                        "leaseTerm", leaseTerm));
    }

    @Programmatic
    public PersistedCalculationResult create(final InvoiceCalculationService.CalculationResult calculationResult, final LeaseTerm leaseTerm) {
        final PersistedCalculationResult persistedCalculationResult = new PersistedCalculationResult(calculationResult, leaseTerm);
        serviceRegistry2.injectServicesInto(persistedCalculationResult);
        repositoryService.persistAndFlush(persistedCalculationResult);
        return persistedCalculationResult;
    }

    @Programmatic
    public void deleteIfAnyAndRecreate(
            final List<InvoiceCalculationService.CalculationResult> results,
            final LeaseTerm leaseTerm) {
        for (PersistedCalculationResult oldResultOnTerm : findByLeaseTerm(leaseTerm)){
            repositoryService.removeAndFlush(oldResultOnTerm);
        }
        for (InvoiceCalculationService.CalculationResult calculationResult : results){
            create(calculationResult, leaseTerm);
        }
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

}
