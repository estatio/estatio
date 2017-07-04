package org.estatio.capex.dom.documents.categorisation;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.dobj.DomainObject_checkStateAbstract;
import org.estatio.dom.invoice.DocumentTypeData;

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
