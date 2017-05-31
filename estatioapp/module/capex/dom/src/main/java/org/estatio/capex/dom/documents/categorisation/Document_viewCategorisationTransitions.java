package org.estatio.capex.dom.documents.categorisation;

import java.util.List;

import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.state.DomainObject_viewTransitionsAbstract;
import org.estatio.dom.invoice.DocumentTypeData;

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
