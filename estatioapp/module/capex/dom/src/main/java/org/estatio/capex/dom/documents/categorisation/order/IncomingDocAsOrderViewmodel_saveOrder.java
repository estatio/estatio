package org.estatio.capex.dom.documents.categorisation.order;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.dom.documents.categorisation.document.IncomingDocViewModel_saveAbstract;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.asset.Property;

@Mixin(method = "act")
public class IncomingDocAsOrderViewmodel_saveOrder
        extends IncomingDocViewModel_saveAbstract<Order, IncomingDocAsOrderViewModel> {

    // workaround for ISIS-1628
    private final IncomingDocAsOrderViewModel viewModel;

    public IncomingDocAsOrderViewmodel_saveOrder(final IncomingDocAsOrderViewModel viewModel) {
        super(viewModel);
        this.viewModel = viewModel;
    }

    public static class DomainEvent
            extends EstatioCapexDomModule.ActionDomainEvent<IncomingDocAsOrderViewmodel_saveOrder> {}

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
        Order order = orderRepository.create(
                viewModel.getOrderNumber(),
                viewModel.getSellerOrderReference(),
                clockService.now(),
                viewModel.getOrderDate(),
                viewModel.getSeller(),
                viewModel.getBuyer(),
                viewModel.getBuyer().getAtPath(),
                null,
                null
        );
        // create order item
        order.addItem(
                viewModel.getCharge(),
                viewModel.getDescription(),
                viewModel.getNetAmount(),
                viewModel.getVatAmount(),
                viewModel.getGrossAmount(),
                viewModel.getTax(),
                viewModel.getPeriod()!= null ? PeriodUtil.yearFromPeriod(viewModel.getPeriod()).startDate() : null,
                viewModel.getPeriod()!= null ? PeriodUtil.yearFromPeriod(viewModel.getPeriod()).endDate() : null,
                (Property) viewModel.getFixedAsset(),
                viewModel.getProject(),
                viewModel.getBudgetItem()
        );
        // attach document
        paperclipRepository.attach(viewModel.getDocument(),null,order);
        return order;
    }

}
