package org.estatio.module.coda.dom.codadocument;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.lease.dom.amortisation.AmortisationEntry;
import org.estatio.module.lease.dom.amortisation.AmortisationSchedule;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "codadocument.CodaDocumentLinkRepository"
)
public class CodaDocumentLinkRepository {

    @Programmatic
    public List<AmortisationScheduleCodaDocumentLink> listAllScheduleLinks() {
        return repositoryService.allInstances(AmortisationScheduleCodaDocumentLink.class);
    }

    @Programmatic
    public List<AmortisationEntryCodaDocumentLink> listAllEntryLinks() {
        return repositoryService.allInstances(AmortisationEntryCodaDocumentLink.class);
    }

    @Programmatic
    public List<OutgoingInvoiceCodaDocumentLink> listAllOutgoingInvoiceLinks() {
        return repositoryService.allInstances(OutgoingInvoiceCodaDocumentLink.class);
    }

    @Programmatic
    public List<AmortisationScheduleCodaDocumentLink> findByAmortisationSchedule(final AmortisationSchedule amortisationSchedule) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        AmortisationScheduleCodaDocumentLink.class,
                        "findByAmortisationSchedule",
                        "amortisationSchedule", amortisationSchedule));
    }

    @Programmatic
    public AmortisationScheduleCodaDocumentLink findUnique(final AmortisationSchedule amortisationSchedule, final CodaDocument codaDocument){
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        AmortisationScheduleCodaDocumentLink.class,
                        "findUnique",
                        "codaDocument", codaDocument,
                        "amortisationSchedule", amortisationSchedule));
    }

    @Programmatic
    public AmortisationScheduleCodaDocumentLink findOrCreate(final AmortisationSchedule amortisationSchedule, final CodaDocument codaDocument){
        AmortisationScheduleCodaDocumentLink result = findUnique(amortisationSchedule, codaDocument);
        if (result ==null) {
            result = create(amortisationSchedule, codaDocument);
        }
        return result;
    }

    private AmortisationScheduleCodaDocumentLink create(final AmortisationSchedule schedule, final CodaDocument document){
        AmortisationScheduleCodaDocumentLink link = new AmortisationScheduleCodaDocumentLink(schedule, document);
        repositoryService.persistAndFlush(link);
        return link;
    }


    @Programmatic
    public AmortisationEntryCodaDocumentLink findUnique(final AmortisationEntry amortisationEntry, final CodaDocument codaDocument){
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        AmortisationEntryCodaDocumentLink.class,
                        "findUnique",
                        "codaDocument", codaDocument,
                        "amortisationEntry", amortisationEntry));
    }

    @Programmatic
    public AmortisationEntryCodaDocumentLink findOrCreate(final AmortisationEntry amortisationEntry, final CodaDocument codaDocument){
        AmortisationEntryCodaDocumentLink result = findUnique(amortisationEntry, codaDocument);
        if (result ==null) {
            result = create(amortisationEntry, codaDocument);
        }
        return result;
    }

    private AmortisationEntryCodaDocumentLink create(final AmortisationEntry amortisationEntry, final CodaDocument document){
        AmortisationEntryCodaDocumentLink link = new AmortisationEntryCodaDocumentLink(amortisationEntry, document);
        repositoryService.persistAndFlush(link);
        return link;
    }

    @Programmatic
    public List<OutgoingInvoiceCodaDocumentLink> findByInvoice(final InvoiceForLease invoice) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        OutgoingInvoiceCodaDocumentLink.class,
                        "findByInvoice",
                        "invoice", invoice));
    }

    @Programmatic
    public OutgoingInvoiceCodaDocumentLink findUnique(final InvoiceForLease invoice, final CodaDocument codaDocument){
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        OutgoingInvoiceCodaDocumentLink.class,
                        "findUnique",
                        "codaDocument", codaDocument,
                        "invoice", invoice));
    }

    @Programmatic
    public OutgoingInvoiceCodaDocumentLink findOrCreate(final InvoiceForLease invoice, final CodaDocument codaDocument){
        OutgoingInvoiceCodaDocumentLink result = findUnique(invoice, codaDocument);
        if (result ==null) {
            result = create(invoice, codaDocument);
        }
        return result;
    }

    private OutgoingInvoiceCodaDocumentLink create(final InvoiceForLease invoice, final CodaDocument document){
        OutgoingInvoiceCodaDocumentLink link = new OutgoingInvoiceCodaDocumentLink(invoice, document);
        repositoryService.persistAndFlush(link);
        return link;
    }

    @Inject
    RepositoryService repositoryService;

}
