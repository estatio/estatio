package org.estatio.capex.dom.documents.order;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.documents.HasDocumentAbstract;
import org.estatio.capex.dom.documents.IncomingDocumentRepository;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.asset.Property;

import lombok.Getter;

@Mixin
public class IncomingOrderViewmodel_saveOrder {

    @Getter
    private final IncomingOrderViewModel viewmodel;

    public IncomingOrderViewmodel_saveOrder(final IncomingOrderViewModel viewModel) {
        this.viewmodel = viewModel;
    }


    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Object saveOrder(final boolean goToNext){
        Order order = doCreate();
        return goToNext && nextDocument()!=null ? factory.map(nextDocument()) : order;
    }

    public boolean default0SaveOrder(){
        return true;
    }

    public String disableSaveOrder(){
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
