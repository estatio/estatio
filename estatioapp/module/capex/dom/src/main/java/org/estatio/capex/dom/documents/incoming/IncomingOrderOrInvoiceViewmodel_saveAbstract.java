package org.estatio.capex.dom.documents.incoming;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.documents.HasDocumentAbstract;
import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.capex.dom.triggers.DomainObject_triggerAbstract;

import lombok.Getter;

public abstract class IncomingOrderOrInvoiceViewmodel_saveAbstract<
        T,
        VM extends IncomingOrderOrInvoiceViewModel<T>
        > extends DomainObject_triggerAbstract<
        Document,
        IncomingDocumentCategorisationStateTransition,
        IncomingDocumentCategorisationStateTransitionType,
        IncomingDocumentCategorisationState
        > {

    @Getter
    protected final VM viewmodel;

    public IncomingOrderOrInvoiceViewmodel_saveAbstract(final VM viewModel) {
        super(viewModel.getDocument(), IncomingDocumentCategorisationStateTransitionType.CLASSIFY_AS_INVOICE_OR_ORDER);
        this.viewmodel = viewModel;
    }


    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    public Object act(
            @Nullable final String comment,
            final boolean goToNext){
        final T domainObject = doCreate();

        triggerStateTransition(comment);

        this.viewmodel.setDomainObject(domainObject);
        return goToNext && nextDocument()!=null ? factory.createFor(nextDocument()) : domainObject;
    }

    public boolean default1Act(){
        return true;
    }

    public boolean hideAct() {
        return cannotTriggerStateTransition();
    }

    public String disableAct(){
        return getViewmodel().minimalRequiredDataToComplete();
    }


    protected abstract T doCreate();

    private Document nextDocument(){
        List<Document> incomingDocuments = incomingDocumentRepository.findUnclassifiedIncomingOrders();
        return incomingDocuments.size() > 0 ? incomingDocuments.get(0) : null;
    }

    @Inject
    IncomingDocumentRepository incomingDocumentRepository;

    @Inject
    protected HasDocumentAbstract.Factory factory;

    @Inject
    protected PaperclipRepository paperclipRepository;

    @Inject
    protected OrderRepository orderRepository;

    @Inject
    protected ClockService clockService;

}
