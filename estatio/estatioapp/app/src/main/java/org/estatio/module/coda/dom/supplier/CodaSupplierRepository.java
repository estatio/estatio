package org.estatio.module.coda.dom.supplier;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.party.dom.Organisation;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaSupplier.class,
        objectType = "coda.CodaSupplierRepository"
)
public class CodaSupplierRepository {

    @Programmatic
    public List<CodaSupplier> listAll() {
        return repositoryService.allInstances(CodaSupplier.class);
    }

    @Programmatic
    public CodaSupplier findByReference(
            final String reference) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        CodaSupplier.class,
                        "findByReference",
                        "reference", reference));
    }

    @Programmatic
    public CodaSupplier create(
            final String reference,
            final String name,
            final String shortName,
            final Organisation organisation) {
        return repositoryService.persist(new CodaSupplier(reference, name, shortName, organisation));
    }

    /**
     * Similar to {@link #upsert(String, String, String, Organisation)}, but will NOT update any fields for a
     * {@link CodaSupplier} that already exists.
     */
    @Programmatic
    public CodaSupplier findOrCreate(
            final String reference,
            final String name,
            final String shortName,
            final Organisation supplier) {
        CodaSupplier codaSupplier = findByReference(reference);
        if (codaSupplier == null) {
            codaSupplier = create(reference, name, shortName, supplier);
        }
        return codaSupplier;
    }

    /**
     * Similar to {@link #create(String, String, String, Organisation)}, but will update any non-key fields if the
     * {@link CodaSupplier} already exists.
     */
    @Programmatic
    public CodaSupplier upsert(
            final String reference,
            final String name,
            final String shortName,
            final Organisation supplier) {
        CodaSupplier codaSupplier = findByReference(reference);
        if (codaSupplier == null) {
            codaSupplier = create(reference, name, shortName, supplier);
        } else {
            codaSupplier.setName(name);
            codaSupplier.setShortName(shortName);
            codaSupplier.setOrganisation(supplier);
        }
        return codaSupplier;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;
}
