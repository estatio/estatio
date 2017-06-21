package org.estatio.capex.dom.documents.categorisation;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.task.DomainObject_nextTaskTransitionTypeAbstract;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method="prop")
public class Document_nextCategorisationTaskTransitionType
        extends DomainObject_nextTaskTransitionTypeAbstract<
                        Document,
                        IncomingDocumentCategorisationStateTransition,
                        IncomingDocumentCategorisationStateTransitionType,
                        IncomingDocumentCategorisationState> {


    public Document_nextCategorisationTaskTransitionType(final Document document) {
        super(document, IncomingDocumentCategorisationStateTransition.class);
    }

    public boolean hideProp() {
        return !DocumentTypeData.hasIncomingType(domainObject)  || super.hideProp();
    }

}
