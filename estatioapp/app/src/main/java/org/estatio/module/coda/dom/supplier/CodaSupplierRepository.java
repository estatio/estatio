package org.estatio.module.coda.dom.supplier;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.value.TimeStamp;

import org.estatio.module.party.dom.Organisation;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaSupplier.class
)
public class CodaSupplierRepository {

    @Programmatic
    public List<CodaSupplier> listAll() {
        return repositoryService.allInstances(CodaSupplier.class);
    }

    @Programmatic
    public CodaSupplier findByReference(
            final String reference
    ) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        CodaSupplier.class,
                        "findByReference",
                        "reference", reference));
    }

    @Programmatic
    public TimeStamp findHighWaterMark() {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        TimeStamp.class,
                        "findHighWaterMark"));
    }

    @Programmatic
    public CodaSupplier createSupplier(
            final String reference,
            final String shortName,
            final TimeStamp modifyDate,
            final Organisation supplier) {
        final CodaSupplier codaSupplier = new CodaSupplier();
        serviceRegistry2.injectServicesInto(codaSupplier);
        codaSupplier.setReference(reference);
        codaSupplier.setShortName(shortName);
        codaSupplier.setModifyDate(modifyDate);
        codaSupplier.setOrganisation(supplier);
        repositoryService.persist(codaSupplier);
        return codaSupplier;
    }

    @Programmatic
    public CodaSupplier upsertSupplier(
            final String reference,
            final String shortName,
            final TimeStamp modifyDate,
            final Organisation supplier) {
        CodaSupplier codaSupplier = findByReference(reference);
        if (codaSupplier == null) {
            codaSupplier = createSupplier(reference, shortName, modifyDate, supplier);
        } else {
            codaSupplier.setShortName(shortName);
            codaSupplier.setModifyDate(modifyDate);
        }
        return codaSupplier;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;

    @javax.inject.Inject
    ServiceRegistry2 serviceRegistry2;
}
