package org.estatio.module.capex.dom.order.buyer;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.party.dom.Party;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = OrderSecondaryBuyerLink.class
)
public class OrderSecondaryBuyerLinkRepository {

    @Programmatic
    public OrderSecondaryBuyerLink createLink(final Order order, final Party buyer) {
        final OrderSecondaryBuyerLink link = new OrderSecondaryBuyerLink();
        link.setOrdr(order);
        link.setSecondaryBuyer(buyer);

        repositoryService.persistAndFlush(link);
        return link;
    }

    @Programmatic
    public void removeLink(final Order order) {
        final OrderSecondaryBuyerLink link = findByOrder(order);
        if (link != null)
            repositoryService.removeAndFlush(link);
    }

    @Programmatic
    public OrderSecondaryBuyerLink findByOrder(final Order order) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        OrderSecondaryBuyerLink.class,
                        "findByOrder",
                        "order", order
                ));
    }

    @Inject
    RepositoryService repositoryService;
}
