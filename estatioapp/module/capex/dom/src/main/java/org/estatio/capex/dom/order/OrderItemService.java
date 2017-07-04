package org.estatio.capex.dom.order;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.capex.dom.project.Project;
import org.estatio.dom.asset.Property;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.party.Party;

@DomainService(nature = NatureOfService.DOMAIN)
public class OrderItemService {

    public List<OrderItem> searchOrderItem(
            final String searchString,
            final Party seller,
            final Charge charge,
            final Project project,
            final Property property){
        List<OrderItem> result = new ArrayList<>();

        for (Order order : orderRepository.matchByOrderNumber(searchString)){
            for (OrderItem item : order.getItems()) {
                if (!result.contains(item) && !item.isInvoiced()) {
                    result.add(item);
                }
            }
        }

        for (OrderItem item : orderItemRepository.matchByDescription(searchString)) {
            if (!result.contains(item) && !item.isInvoiced()) {
                result.add(item);
            }
        }

        for (Order order : orderRepository.matchBySellerReferenceOrName(searchString)){
            for (OrderItem item : order.getItems()){
                if (!result.contains(item) && !item.isInvoiced()) {
                    result.add(item);
                }
            }
        }

        if (seller!=null){
            result = Lists.newArrayList(
                    FluentIterable.from(result)
                            .filter(x->x.getOrdr().getSeller()!=null && x.getOrdr().getSeller().equals(seller))
                            .toList()
            );
        }
        if (charge!=null) {
            result = Lists.newArrayList(
                    FluentIterable.from(result)
                            .filter(x->x.getCharge().equals(charge))
                            .toList()
            );
        }
        if (project!=null) {
            result = Lists.newArrayList(
                    FluentIterable.from(result)
                            .filter(x->x.getProject()!=null && x.getProject().equals(project))
                            .toList()
            );
        }
        if (property!=null) {
            result = Lists.newArrayList(
                    FluentIterable.from(result)
                            .filter(x->x.getProperty()!=null && x.getProperty().equals(property))
                            .toList()
            );
        }
        return result;
    }

    @Inject
    OrderRepository orderRepository;

    @Inject
    OrderItemRepository orderItemRepository;

}
