package org.estatio.capex.dom.documents.categorisation;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.dobj.DomainObject_currentStateAbstract;
import org.estatio.dom.invoice.DocumentTypeData;

/**
 * This cannot be inlined (needs to be a mixin) because Document is part of the incode platform and
 * does not know about its categorisation state.
 */
@Mixin(method="prop")
public class Document_categorisationState
        extends DomainObject_currentStateAbstract<
                Document,
        IncomingDocumentCategorisationStateTransition,
        IncomingDocumentCategorisationStateTransitionType,
        IncomingDocumentCategorisationState> {

    public Document_categorisationState(final Document document) {
        super(document, IncomingDocumentCategorisationStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public IncomingDocumentCategorisationState prop() {
        return super.prop();
    }

    public boolean hideProp() {
        return !DocumentTypeData.hasIncomingType(domainObject);
    }

}
