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

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.invoice.dom.Invoice;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaDocLink.class,
        objectType = "codalink.InvoiceCodaDocLinkRepository"
)
public class CodaDocLinkRepository {

    @Programmatic
    public CodaDocLink findOrCreate(
            final String cmpCode, final String docCode, final String docNum,
            final Invoice invoice) {
        return findByCmpCodeAndDocCodeAndDocNum(cmpCode, docCode, docNum)
                .orElse(create(cmpCode, docCode,docNum, invoice));
    }

    private CodaDocLink create(
            final String cmpCode, final String docCode, final String docNum,
            final Invoice invoice) {
        return repositoryService.persistAndFlush(
                new CodaDocLink(cmpCode, docCode, docNum, invoice, clockService.nowAsDateTime()));
    }

    @Programmatic
    public Optional<CodaDocLink> findByCmpCodeAndDocCodeAndDocNum(
            final String cmpCode,
            final String docCode,
            final String docNum
    ) {
        return Optional.ofNullable(
                repositoryService.uniqueMatch(
                    new QueryDefault<>(
                            CodaDocLink.class,
                            "findByCmpCodeAndDocCodeAndDocNum",
                            "cmpCode", cmpCode,
                            "docCode", docCode,
                            "docNum", docNum))
            );
    }

    @Programmatic
    public List<CodaDocLink> findByInvoice(final Invoice invoice) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocLink.class,
                        "findByInvoice",
                        "invoice", invoice
                )
        );
    }

    @Programmatic
    public Optional<CodaDocLink> findMostRecentBy(final IncomingInvoice incomingInvoice) {
        return findByInvoice(incomingInvoice).stream().findFirst();
    }

    @Programmatic
    public List<CodaDocLink> listAll() {
        return repositoryService.allInstances(CodaDocLink.class);
    }


    @Inject
    RepositoryService repositoryService;

    @Inject
    ClockService clockService;

}
