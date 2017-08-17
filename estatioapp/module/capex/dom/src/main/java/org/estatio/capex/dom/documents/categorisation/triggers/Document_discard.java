package org.estatio.capex.dom.documents.categorisation.triggers;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.dom.invoice.DocumentTypeData;

/**
 * This cannot be inlined (needs to be a mixin) because Document is part of the incode platform and
 * does not know about its categorisation state machine
 */
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

        trigger(comment, null);

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


}
