package org.estatio.capex.dom.order;

import java.util.Objects;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.util.ReasonBuffer;

import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.viewmodel.IncomingDocAsInvoiceViewModel;
import org.estatio.capex.dom.project.Project;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.Property;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.invoice.InvoiceItem;

/**
 * Supports the selection of {@link OrderItem}s of an {@link InvoiceItem}.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class OrderItemInvoiceItemLinkValidationService {

    @Programmatic
    public String validateOrderItem(
            final OrderItem orderItem,
            final IncomingDocAsInvoiceViewModel viewModel) {

        final Charge charge = viewModel.getCharge();
        final Project project = viewModel.getProject();
        final Property property = viewModel.getProperty();

        return validateOrderItem(orderItem, charge, project, property);
    }

    @Programmatic
    public String validateOrderItem(
            final OrderItem orderItem,
            final IncomingInvoiceItem invoiceItem) {

        final Charge charge = invoiceItem.getCharge();
        final Project project = invoiceItem.getProject();
        final FixedAsset fixedAsset = invoiceItem.getFixedAsset();

        return validateOrderItem(orderItem, charge, project, fixedAsset);
    }

    private String validateOrderItem(
            final OrderItem orderItem,
            final Charge charge,
            final Project project,
            final FixedAsset fixedAsset) {
        final ReasonBuffer buf = new ReasonBuffer();
        buf.appendOnCondition(!Objects.equals(charge, orderItem.getCharge()), "charge is different");
        buf.appendOnCondition(!Objects.equals(project, orderItem.getProject()), "project is different");
        buf.appendOnCondition(!Objects.equals(fixedAsset, orderItem.getProperty()), "property is different");
        final String reason = buf.getReason();
        return reason != null ? "Cannot link to this item: " + reason : null;
    }


}
