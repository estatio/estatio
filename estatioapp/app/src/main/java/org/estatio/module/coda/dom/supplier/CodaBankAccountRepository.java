package org.estatio.module.coda.dom.supplier;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaBankAccount.class,
        objectType = "coda.CodaBankAccountRepository"
)
public class CodaBankAccountRepository {

    @Programmatic
    public List<CodaBankAccount> listAll() {
        return repositoryService.allInstances(CodaBankAccount.class);
    }

    @Programmatic
    public CodaBankAccount findBySupplierAndIban(
            final CodaSupplier codaSupplier,
            final String iban) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        CodaBankAccount.class,
                        "findBySupplierAndIban",
                        "supplier", codaSupplier,
                        "iban", iban));
    }

    @Programmatic
    public List<CodaBankAccount> findBySupplier(
            final CodaSupplier codaSupplier) {
        return repositoryService.allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        CodaBankAccount.class,
                        "findBySupplier",
                        "supplier", codaSupplier));
    }

    /**
     * Typically expect only one, but there are no constraints to ensure that an iban is unique across suppliers.
     */
    @Programmatic
    public List<CodaBankAccount> findByIban(
            final String iban) {
        return repositoryService.allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        CodaBankAccount.class,
                        "findByIban",
                        "iban", iban));
    }

    @Programmatic
    public CodaBankAccount create(
            final CodaSupplier codaSupplier,
            final String iban,
            final String bic) {
        return repositoryService.persist(new CodaBankAccount(codaSupplier, iban, bic));
    }

    @Programmatic
    public CodaSupplier delete(
            final CodaSupplier codaSupplier,
            final String iban) {
        final CodaBankAccount bankAccount = findBySupplierAndIban(codaSupplier, iban);
        if(bankAccount != null) {
            repositoryService.removeAndFlush(bankAccount);
        }
        return codaSupplier;
    }

    /**
     * Similar to {@link #upsert(CodaSupplier, String, String)}, but will NOT update any fields for
     * a {@link CodaBankAccount} that already exists.
     */
    @Programmatic
    public CodaBankAccount findOrCreate(
            final CodaSupplier codaSupplier,
            final String iban,
            final String bic) {
        CodaBankAccount bankAccount = findBySupplierAndIban(codaSupplier, iban);
        if (bankAccount == null) {
            bankAccount = create(codaSupplier, iban, bic);
        }
        return bankAccount;
    }

    /**
     * Same as {@link #findOrCreate(CodaSupplier, String, String)}, but will update any non-key fields
     * if the {@link CodaBankAccount} already exists.
     */
    @Programmatic
    public CodaBankAccount upsert(
            final CodaSupplier codaSupplier,
            final String iban,
            final String bic) {
        CodaBankAccount bankAccount = findBySupplierAndIban(codaSupplier, iban);
        if (bankAccount == null) {
            bankAccount = create(codaSupplier, iban, bic);
        } else {
            bankAccount.setBic(bic);
        }
        return bankAccount;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;
}
