package org.estatio.capex.dom.documents.categorisation.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.dobj.DomainObject_checkStateAbstract;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.dom.invoice.DocumentTypeData;

/**
 * This cannot be inlined (needs to be a mixin) because Document is part of the incode platform and
 * does not know about its categorisation state.
 */
@Mixin(method="act")
public class Document_checkCategorisationState
        extends DomainObject_checkStateAbstract<
                            Document,
        IncomingDocumentCategorisationStateTransition,
        IncomingDocumentCategorisationStateTransitionType,
        IncomingDocumentCategorisationState> {


    public Document_checkCategorisationState(final Document document) {
        super(document, IncomingDocumentCategorisationStateTransition.class);
    }

    public boolean hideAct() {
        return !DocumentTypeData.hasIncomingType(domainObject);
    }

}
