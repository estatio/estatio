package org.estatio.module.coda.contributions.codadocument;

import java.util.Arrays;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.module.coda.dom.CodaDocumentType;
import org.estatio.module.coda.dom.codadocument.CodaDocument;

import lombok.RequiredArgsConstructor;

@Mixin(method = "act")
@RequiredArgsConstructor
public class CodaDocument_post {

    private final CodaDocument codaDocument;

    @Action(publishing = Publishing.ENABLED, semantics = SemanticsOf.IDEMPOTENT)
    public CodaDocument act() {
        if (codaDocument.getPostedAt()==null) codaDocument.updatePostedAtAndAttachedScheduleEntryIfAny(clockService.nowAsLocalDateTime());
        return codaDocument;
    }

    // extra temp requirement until Camel can handle everything ...
    public boolean hideAct(){
        return !(
                codaDocument.getRole() == CodaDocument.CodaDocumentRole.PROPOSAL
             && Arrays.asList(
                        CodaDocumentType.INITIAL_COVID_AMORTISATION,
                        CodaDocumentType.RECURRING_COVID_AMORTISATION)
                    .contains(codaDocument.getDocumentType())
             && codaDocument.getAtPath().startsWith("/ITA"));
    }

    @Inject ClockService clockService;

}
