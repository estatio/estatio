package org.estatio.capex.dom.documents.categorisation.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.dobj.DomainObject_nextTaskTransitionTypeAbstract;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.dom.invoice.DocumentTypeData;

/**
 * This cannot be inlined (needs to be a mixin) because Document is part of the incode platform and
 * does not know about its categorisation state.
 */
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
