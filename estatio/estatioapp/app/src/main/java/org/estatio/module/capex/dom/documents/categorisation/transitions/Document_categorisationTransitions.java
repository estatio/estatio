package org.estatio.module.capex.dom.documents.categorisation.transitions;

import java.util.List;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.task.dom.dobj.DomainObject_transitionsAbstract;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.module.invoice.dom.DocumentTypeData;

/**
 * This cannot be inlined (needs to be a mixin) because Document is part of the incode platform and
 * does not know about its categorisation state.
 */
@Mixin(method = "coll")
public class Document_categorisationTransitions
        extends DomainObject_transitionsAbstract<
                                    Document,
                                    IncomingDocumentCategorisationStateTransition,
                                    IncomingDocumentCategorisationStateTransitionType,
                                    IncomingDocumentCategorisationState> {


    public Document_categorisationTransitions(final Document document) {
        super(document, IncomingDocumentCategorisationStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public List<IncomingDocumentCategorisationStateTransition> coll() {
        return super.coll();
    }


    public boolean hideColl() {
        return !DocumentTypeData.hasIncomingType(domainObject);
    }

}
