package org.estatio.module.capex.dom.codalink;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.invoice.dom.Invoice;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = InvoiceCodaDocLink.class,
        objectType = "codalink.InvoiceCodaDocLinkRepository"
)
public class InvoiceCodaDocLinkRepository {

    @Programmatic
    public InvoiceCodaDocLink findOrCreate(
            final String cmpCode, final String docCode, final String docNum,
            final Invoice invoice) {
        return findByCmpCodeAndDocCodeAndDocNum(cmpCode, docCode, docNum)
                .orElse(create(cmpCode, docCode,docNum, invoice));
    }

    private InvoiceCodaDocLink create(
            final String cmpCode, final String docCode, final String docNum,
            final Invoice invoice) {
        return repositoryService.persistAndFlush(
                new InvoiceCodaDocLink(cmpCode, docCode, docNum, invoice, clockService.nowAsDateTime()));
    }

    @Programmatic
    public Optional<InvoiceCodaDocLink> findByCmpCodeAndDocCodeAndDocNum(
            final String cmpCode,
            final String docCode,
            final String docNum
    ) {
        return Optional.ofNullable(
                repositoryService.uniqueMatch(
                    new QueryDefault<>(
                            InvoiceCodaDocLink.class,
                            "findByCmpCodeAndDocCodeAndDocNum",
                            "cmpCode", cmpCode,
                            "docCode", docCode,
                            "docNum", docNum))
            );
    }

    @Programmatic
    public List<InvoiceCodaDocLink> findByInvoice(final Invoice invoice) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        InvoiceCodaDocLink.class,
                        "findByInvoice",
                        "invoice", invoice
                )
        );
    }

    @Programmatic
    public List<InvoiceCodaDocLink> listAll() {
        return repositoryService.allInstances(InvoiceCodaDocLink.class);
    }


    @Inject
    RepositoryService repositoryService;

    @Inject
    ClockService clockService;

}
