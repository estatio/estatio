/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.module.order.dom.attr;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.factory.FactoryService;

import org.estatio.module.base.dom.CurrencyUtil;
import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.order.dom.attr.act.Order_changeIntroduction;
import org.estatio.module.order.dom.attr.act.Order_changeOrderDescription;
import org.estatio.module.order.dom.attr.act.Order_changePriceAndPayments;
import org.estatio.module.order.dom.attr.act.Order_changeSignature;
import org.estatio.module.order.dom.attr.act.Order_changeSubject;
import org.estatio.module.order.dom.attr.act.Order_changeTotalWorkCost;
import org.estatio.module.order.dom.attr.act.Order_changeWorkSchedule;

@DomainService(nature = NatureOfService.DOMAIN, repositoryFor = OrderAttribute.class)
public class OrderAttributeRepository extends UdoDomainRepositoryAndFactory<OrderAttribute> {

    public OrderAttributeRepository() {
        super(OrderAttributeRepository.class, OrderAttribute.class);
    }

    @Programmatic
    public List<OrderAttribute> findByOrder(
            final Order order) {
        return allMatches("findByOrder",
                "order", order);
    }

    @Programmatic
    public OrderAttribute findByOrderAndName(
            final Order order,
            final OrderAttributeName name) {
        return uniqueMatch("findByOrderAndName",
                "order", order,
                "name", name);
    }

    @Programmatic
    public String findValueByOrderAndName(final OrderAttributeName orderAttributeName, final Order order) {
        final OrderAttribute orderAttribute = findByOrderAndName(order, orderAttributeName);
        return orderAttribute == null ? "" : orderAttribute.getValue();
    }

//    @Programmatic
//    public boolean findIsOverriddenByOrderAndName(final OrderAttributeName orderAttributeName, final Order order) {
//        final OrderAttribute orderAttribute = findByOrderAndName(order, orderAttributeName);
//        return orderAttribute != null && orderAttribute.isOverridden();
//    }

    @Programmatic
    public OrderAttribute newAttribute(
            final Order order,
            final OrderAttributeName name,
            final String value
            //,final boolean overridden
    ) {
        OrderAttribute orderAttribute = newTransientInstance();
        orderAttribute.setOrdr(order);
        orderAttribute.setName(name);
        orderAttribute.setValue(value);
        //orderAttribute.setOverridden(overridden);
        persistIfNotAlready(orderAttribute);
        return orderAttribute;
    }

    @Programmatic
    public void initializeAttributes(final Order order) {
        factoryService.mixin(Order_changeSubject.class, order).act(order.getDescriptionSummary());
        factoryService.mixin(Order_changeIntroduction.class, order).act("Con la presente e in riferimento alla Vostra nuova offerta del DD MMM YYYY, Vi confermiamo l’ordine come di seguito precisato.");
        factoryService.mixin(Order_changeOrderDescription.class, order).act("Le prestazioni in oggetto si riferiscono alle seguenti attività:");
        final String orderNetAmountStr =
                String.format("€ %s + IVA", CurrencyUtil.formattedAmount(order.getNetAmount(), order.getAtPath()));
        factoryService.mixin(Order_changeTotalWorkCost.class, order).act(orderNetAmountStr);
        factoryService.mixin(Order_changeWorkSchedule.class, order).act("I lavori dovranno essere effettuati entro il DD MMM YYYY.");
        factoryService.mixin(Order_changePriceAndPayments.class, order).act("L’importo dell’incarico a Voi affidato ammonta a € X.XXX,00 (XXXX/00) oltre IVA secondo aliquota di legge e oneri di legge.");
        factoryService.mixin(Order_changeSignature.class, order).act("Luca Cagnani");
    }

    @Inject
    FactoryService factoryService;
}
