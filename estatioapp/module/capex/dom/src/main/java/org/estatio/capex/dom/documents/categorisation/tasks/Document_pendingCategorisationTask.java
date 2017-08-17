package org.estatio.capex.dom.documents.categorisation.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.dobj.DomainObject_pendingTaskAbstract;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.dom.invoice.DocumentTypeData;

/**
 * This cannot be inlined (needs to be a mixin) because Document is part of the incode platform and
 * does not know about its categorisation state.
 */
@Mixin(method="prop")
public class Document_pendingCategorisationTask
        extends DomainObject_pendingTaskAbstract<
                    Document,
        IncomingDocumentCategorisationStateTransition,
        IncomingDocumentCategorisationStateTransitionType,
        IncomingDocumentCategorisationState> {


    public Document_pendingCategorisationTask(final Document document) {
        super(document, IncomingDocumentCategorisationStateTransition.class);
    }

    public boolean hideProp() {
        return !DocumentTypeData.hasIncomingType(domainObject);
    }

}
