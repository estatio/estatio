package org.estatio.module.coda.contributions.codadocument;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.coda.dom.codadocument.CodaDocument;
import org.estatio.module.coda.dom.codadocument.CodaDocumentLinkRepository;
import org.estatio.module.lease.dom.amortisation.AmortisationEntry;

@Mixin(method="coll")
public class CodaDocument_amortisationEntry {

    private final CodaDocument codaDocument;
    public CodaDocument_amortisationEntry(final CodaDocument codaDocument) {
        this.codaDocument = codaDocument;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    @Property
    public List<AmortisationEntry> coll() {
        return codaDocumentLinkRepository.findEntryLinkByDocument(codaDocument)
                .stream()
                .map(l->l.getAmortisationEntry())
                .collect(Collectors.toList());
    }

    @Inject CodaDocumentLinkRepository codaDocumentLinkRepository;

}
