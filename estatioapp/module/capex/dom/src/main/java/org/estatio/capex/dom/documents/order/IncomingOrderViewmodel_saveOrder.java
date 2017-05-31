package org.estatio.capex.dom.documents.order;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.dom.documents.incoming.IncomingOrderOrInvoiceViewmodel_saveAbstract;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.asset.Property;

@Mixin(method = "act")
public class IncomingOrderViewmodel_saveOrder
        extends IncomingOrderOrInvoiceViewmodel_saveAbstract<Order, IncomingOrderViewModel> {

    // workaround for ISIS-1628
    private final IncomingOrderViewModel viewModel;

    public IncomingOrderViewmodel_saveOrder(final IncomingOrderViewModel viewModel) {
        super(viewModel);
        this.viewModel = viewModel;
    }

    public static class DomainEvent
            extends EstatioCapexDomModule.ActionDomainEvent<IncomingOrderViewmodel_saveOrder> {}

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = DomainEvent.class
    )
    public Object act(
            @Nullable final String comment,
            final boolean goToNext){
        return super.act(comment, goToNext);
    }

    protected Order doCreate(){
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

}
