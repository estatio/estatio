package org.estatio.capex.dom.documents.categorisation.document;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class Document_discard
        extends Document_triggerAbstract {

    private final Document document;

    public Document_discard(final Document document) {
        super(document, IncomingDocumentCategorisationStateTransitionType.DISCARD);
        this.document = document;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE
    )
    @ActionLayout(cssClassFa = "trash-o")
    public Document act(
            @Nullable final String comment) {

        trigger(comment);

        return document;
    }

    public boolean hideAct() {
        if(cannotTransition()) {
            return true;
        }
        final Document document = getDomainObject();
        return !DocumentTypeData.hasIncomingType(document);
    }

    public String disableAct() {
        return reasonGuardNotSatisified();
    }

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    OrderRepository orderRepository;

    @Inject
    ClockService clockService;

}
