package org.estatio.module.coda.contributions;

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
import org.estatio.module.lease.dom.amortisation.AmortisationSchedule;

@Mixin(method="coll")
public class AmortisationSchedule_codaDocuments {

    private final AmortisationSchedule amortisationSchedule;
    public AmortisationSchedule_codaDocuments(final AmortisationSchedule amortisationSchedule) {
        this.amortisationSchedule = amortisationSchedule;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    @Property
    public List<CodaDocument> coll() {
        return codaDocumentLinkRepository.findByAmortisationSchedule(amortisationSchedule)
                .stream()
                .map(l->l.getCodaDocument())
                .collect(Collectors.toList());
    }


    @Inject CodaDocumentLinkRepository codaDocumentLinkRepository;

}
