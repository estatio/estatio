package org.estatio.module.capex.dom.documents.categorisation.transitions;

import java.util.List;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.module.capex.dom.dobj.DomainObject_viewTransitionsAbstract;
import org.estatio.module.invoice.dom.DocumentTypeData;

/**
 * This cannot be inlined (needs to be a mixin) because Document is part of the incode platform and
 * does not know about its categorisation state.
 */
@Mixin(method = "act")
public class Document_viewCategorisationTransitions
        extends DomainObject_viewTransitionsAbstract<
                            Document,
                            IncomingDocumentCategorisationStateTransition,
                            IncomingDocumentCategorisationStateTransitionType,
                            IncomingDocumentCategorisationState> {


    public Document_viewCategorisationTransitions(final Document document) {
        super(document, IncomingDocumentCategorisationStateTransition.class);
    }

    // necessary because Isis' metamodel unable to infer return type from generic method
    @Override
    public List<IncomingDocumentCategorisationStateTransition> act() {
        return super.act();
    }


    public boolean hideAct() {
        return !DocumentTypeData.hasIncomingType(domainObject);
    }

}
