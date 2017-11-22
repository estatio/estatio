package org.estatio.module.capex.dom.orderinvoice;

import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.tablecol.TableColumnOrderService;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.order.OrderItem;

@DomainService(nature = NatureOfService.DOMAIN)
public class TableColumnOrderServiceForOrderItemInvoiceItemLinks implements TableColumnOrderService {

    @Override
    public List<String> orderParented(
            final Object parent,
            final String collectionId,
            final Class<?> collectionType,
            final List<String> propertyIds) {

        if(parent instanceof IncomingInvoiceItem && "orderItemLinks".equals(collectionId)) {
            final List<String> ids = Lists.newArrayList(propertyIds);
            ids.removeIf(x -> x.toLowerCase().contains("invoice"));
            return ids;
        }

        if(parent instanceof OrderItem && "invoiceItemLinks".equals(collectionId)) {
            final List<String> ids = Lists.newArrayList(propertyIds);
            ids.removeIf(x -> x.toLowerCase().contains("order"));
            return ids;
        }

        return null;
    }

    @Override
    public List<String> orderStandalone(final Class<?> collectionType, final List<String> propertyIds) {
        return null;
    }
}
