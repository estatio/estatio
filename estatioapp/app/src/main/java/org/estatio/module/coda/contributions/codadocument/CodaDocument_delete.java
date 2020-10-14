package org.estatio.module.coda.contributions.codadocument;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.coda.dom.codadocument.CodaDocument;
import org.estatio.module.coda.dom.codadocument.CodaDocumentLinkRepository;
import org.estatio.module.lease.dom.amortisation.AmortisationSchedule;

@Mixin
public class CodaDocument_delete {

    private final CodaDocument codaDocument;

    public CodaDocument_delete(CodaDocument codaDocument) {
        this.codaDocument = codaDocument;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public AmortisationSchedule $$() {
        final AmortisationSchedule schedule = codaDocumentLinkRepository
                .findByAmortisationScheduleLinkByDocument(codaDocument).stream()
                .map(l -> l.getAmortisationSchedule())
                .findFirst().orElse(null);
        codaDocumentLinkRepository.findByAmortisationScheduleLinkByDocument(codaDocument)
                .forEach(l->{
                    repositoryService.removeAndFlush(l);
                });
        codaDocumentLinkRepository.findEntryLinkByDocument(codaDocument)
                .forEach(l->{
                    repositoryService.removeAndFlush(l);
                });
        codaDocumentLinkRepository.findInvoiceLinkByDocument(codaDocument)
                .forEach(l->{
                    repositoryService.removeAndFlush(l);
                });
        repositoryService.removeAndFlush(codaDocument);
        return schedule;
    }

    public String disable$$(){
        if (codaDocument.getPostedAt()!=null) return "This document is posted";
        return null;
    }

    @Inject RepositoryService repositoryService;

    @Inject CodaDocumentLinkRepository codaDocumentLinkRepository;

}
