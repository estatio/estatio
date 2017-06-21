package org.estatio.capex.dom.documents.categorisation;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.task.DomainObject_nextTaskPersonAssignedToAbstract;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method="prop")
public class Document_nextCategorisationTaskPersonAssignedTo
        extends DomainObject_nextTaskPersonAssignedToAbstract<
                        Document,
                        IncomingDocumentCategorisationStateTransition,
                        IncomingDocumentCategorisationStateTransitionType,
                        IncomingDocumentCategorisationState> {


    public Document_nextCategorisationTaskPersonAssignedTo(final Document document) {
        super(document, IncomingDocumentCategorisationStateTransition.class);
    }

    public boolean hideProp() {
        return !DocumentTypeData.hasIncomingType(domainObject) || super.hideProp();
    }

}
