package org.estatio.capex.dom.documents.order;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.documents.HasDocumentAbstract;
import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.capex.dom.triggers.DomainObject_triggerAbstract;
import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.asset.Property;

import lombok.Getter;

@Mixin(method = "act")
public class IncomingOrderViewmodel_saveOrder extends DomainObject_triggerAbstract<
        Document,
        IncomingDocumentCategorisationStateTransition,
        IncomingDocumentCategorisationStateTransitionType,
        IncomingDocumentCategorisationState
        > {

    @Getter
    private final IncomingOrderViewModel viewmodel;

    public IncomingOrderViewmodel_saveOrder(final IncomingOrderViewModel viewModel) {
        super(viewModel.getDocument(), IncomingDocumentCategorisationStateTransitionType.CLASSIFY_AS_INVOICE_OR_ORDER);
        this.viewmodel = viewModel;
    }


    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    public Object act(
            @Nullable final String comment,
            final boolean goToNext){
        final Order order = doCreate();

        triggerStateTransition(comment);

        this.viewmodel.order = order;
        return goToNext && nextDocument()!=null ? factory.createFor(nextDocument()) : order;
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

    private Order doCreate(){
        // create order
        Order order = orderRepository.findOrCreate(
                getViewmodel().getOrderNumber(),
                getViewmodel().getSellerOrderReference(),
                clockService.now(),
                getViewmodel().getOrderDate(),
                getViewmodel().getSeller(),
                getViewmodel().getBuyer(),
                getViewmodel().getBuyer().getAtPath(),
                null,
                null
        );
        // create order item
        order.addItem(
                getViewmodel().getCharge(),
                getViewmodel().getDescription(),
                getViewmodel().getNetAmount(),
                getViewmodel().getVatAmount(),
                getViewmodel().getGrossAmount(),
                getViewmodel().getTax(),
                getViewmodel().getPeriod()!= null ? PeriodUtil.yearFromPeriod(getViewmodel().getPeriod()).startDate() : null,
                getViewmodel().getPeriod()!= null ? PeriodUtil.yearFromPeriod(getViewmodel().getPeriod()).endDate() : null,
                (Property) getViewmodel().getFixedAsset(),
                getViewmodel().getProject(),
                getViewmodel().getBudgetItem()
        );
        // attach document
        paperclipRepository.attach(getViewmodel().getDocument(),null,order);
        return order;
    }

    private Document nextDocument(){
        List<Document> incomingDocuments = incomingDocumentRepository.findUnclassifiedIncomingOrders();
        return incomingDocuments.size() > 0 ? incomingDocuments.get(0) : null;
    }

    @Inject
    private IncomingDocumentRepository incomingDocumentRepository;

    @Inject
    private HasDocumentAbstract.Factory factory;

    @Inject
    private PaperclipRepository paperclipRepository;

    @Inject
    private OrderRepository orderRepository;

    @Inject
    private ClockService clockService;

}
