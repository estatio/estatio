package org.estatio.capex.dom.documents.categorisation.triggers;

import javax.inject.Inject;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.BuyerFinder;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.triggers.DomainObject_triggerAbstract;

public abstract class Document_triggerAbstract
        extends DomainObject_triggerAbstract<
                                            Document,
                                            IncomingDocumentCategorisationStateTransition,
                                            IncomingDocumentCategorisationStateTransitionType,
                                            IncomingDocumentCategorisationState> {

    public static abstract class ActionDomainEvent<MIXIN> extends DomainObject_triggerAbstract.ActionDomainEvent<MIXIN> {
        @Override
        public Class<?> getStateTransitionClass() {
            return IncomingDocumentCategorisationStateTransition.class;
        }
    }

    public Document_triggerAbstract(final Document document, IncomingDocumentCategorisationStateTransitionType transitionType) {
        super(document, IncomingDocumentCategorisationStateTransition.class, transitionType);
    }

    @Inject
    BuyerFinder buyerFinder;
}
