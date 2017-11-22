package org.estatio.module.capex.dom.documents.categorisation.tasks;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.capex.dom.dobj.DomainObject_tasksAbstract;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.module.invoice.dom.DocumentTypeData;

/**
 * This cannot be inlined (needs to be a mixin) because Document is part of the incode platform and
 * does not know about its categorisation state.
 */
@Mixin(method = "coll")
public class Document_categorisationTasks
        extends DomainObject_tasksAbstract<
                    Document,
        IncomingDocumentCategorisationStateTransition,
        IncomingDocumentCategorisationStateTransitionType,
        IncomingDocumentCategorisationState> {


    public Document_categorisationTasks(final Document document) {
        super(document, IncomingDocumentCategorisationStateTransition.class);
    }

    public boolean hideColl() {
        return !DocumentTypeData.hasIncomingType(domainObject);
    }

}
