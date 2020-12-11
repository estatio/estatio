package org.estatio.module.coda.contributions.codadocument;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.coda.dom.codadocument.CodaDocument;
import org.estatio.module.coda.dom.codadocument.CodaDocumentLinkRepository;

@Mixin
public class CodaDocument_delete {

    private final CodaDocument codaDocument;

    public CodaDocument_delete(CodaDocument codaDocument) {
        this.codaDocument = codaDocument;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void $$() {
        Lists.newArrayList(codaDocument.getLines()).forEach(dl->{
            codaDocumentLinkRepository.findAmortisationScheduleLinkByDocumentLine(dl)
                    .forEach(l->{
                        repositoryService.removeAndFlush(l);
                    });
            codaDocumentLinkRepository.findAmortisationEntryLinkByDocumentLine(dl)
                    .forEach(l->{
                        repositoryService.removeAndFlush(l);
                    });
            codaDocumentLinkRepository.findInvoiceLinkByDocumentLine(dl)
                    .forEach(l->{
                        repositoryService.removeAndFlush(l);
                    });
        });
        repositoryService.removeAndFlush(codaDocument);
    }

    public String disable$$(){
        if (codaDocument.getPostedAt()!=null) return "This document is posted";
        return null;
    }

    @Inject RepositoryService repositoryService;

    @Inject CodaDocumentLinkRepository codaDocumentLinkRepository;

}
