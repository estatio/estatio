package org.estatio.capex.dom.documents.categorisation;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.dobj.DomainObject_nextTaskRoleAssignedToAbstract;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method="prop")
public class Document_nextCategorisationTaskRoleAssignedTo
        extends DomainObject_nextTaskRoleAssignedToAbstract<
                        Document,
                        IncomingDocumentCategorisationStateTransition,
                        IncomingDocumentCategorisationStateTransitionType,
                        IncomingDocumentCategorisationState> {


    public Document_nextCategorisationTaskRoleAssignedTo(final Document document) {
        super(document, IncomingDocumentCategorisationStateTransition.class);
    }

    public boolean hideProp() {
        return !DocumentTypeData.hasIncomingType(domainObject)  || super.hideProp();
    }

}
