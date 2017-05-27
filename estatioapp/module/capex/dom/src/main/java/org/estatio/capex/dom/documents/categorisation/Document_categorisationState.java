package org.estatio.capex.dom.documents.categorisation;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.state.DomainObject_currentStateAbstract;
import org.estatio.dom.invoice.DocumentTypeData;

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
