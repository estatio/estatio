/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.capex.dom.documents.invoice;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.incoming.IncomingOrderAndInvoiceViewModel;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderItemRepository;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Organisation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@DomainObject(
        // WORKAROUND: using fqcn as objectType because Isis' invalidation of cache in prototyping mode causing NPEs in some situations
        objectType = "org.estatio.capex.dom.documents.invoice.IncomingInvoiceViewModel"
)
@XmlRootElement(name = "categorizeIncomingInvoice")
@XmlType(
        propOrder = {
                "document",
                "invoiceNumber",
                "buyer",
                "seller",
                "invoiceDate",
                "dueDate",
                "paymentMethod",
                "description",
                "orderItem",
                "fixedAsset",
                "project",
                "period",
                "charge",
                "netAmount",
                "vatAmount",
                "tax",
                "grossAmount"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@Getter @Setter
public class IncomingInvoiceViewModel extends IncomingOrderAndInvoiceViewModel {

    public IncomingInvoiceViewModel() {}
    public IncomingInvoiceViewModel(final Document document, final FixedAsset fixedAsset) {
        super(document, fixedAsset);
    }

    private String invoiceNumber;

    private LocalDate invoiceDate;

    private LocalDate dueDate;

    private PaymentMethod paymentMethod;

//    private OrderItem orderItem;
//    @Action(
//            semantics = SemanticsOf.IDEMPOTENT
//    )
//    @ActionLayout(
//            position = ActionLayout.Position.RIGHT
//    )
//    @MemberOrder(name = "orderItem", sequence = "1")
//    public IncomingInvoiceViewModel findOrderItem(
//            @Parameter(optionality = Optionality.OPTIONAL)
//            final OrderItem orderItem
//    ) {
//        setOrderItem(orderItem);
//        autoFillIn();
//        return this;
//    }
//
//    public List<OrderItem> autoComplete0FindOrderItem(@MinLength(3) final String searchString){
//        return orderItemsForAutoComplete(searchString);
//    }

    // TODO: this wrapper is done because of
    // Error marshalling domain object to XML; domain object class is ''org.estatio.capex.dom.documents.invoice.IncomingInvoiceViewModel''
    // A cycle is detected in the object graph. This will cause infinitely deep XML: Organisation{name=Hello World Properties} -> PartyRole{party=HELLOWORLD_GB, roleType=PartyRoleType{title=Landlord}} -> Organisation{name=Hello World Properties}]
    private OrderItemWrapper orderItem;
    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(
            position = ActionLayout.Position.RIGHT
    )
    @MemberOrder(name = "orderItem", sequence = "1")
    public IncomingInvoiceViewModel findOrderItem(
            @Parameter(optionality = Optionality.OPTIONAL)
            final OrderItemWrapper orderItem
    ) {
        setOrderItem(orderItem);
        autoFillIn();
        return this;
    }

    public List<OrderItemWrapper> autoComplete0FindOrderItem(@MinLength(3) final String searchString){
        List<OrderItemWrapper> result1 = new ArrayList<>();
        for (OrderItem item : orderItemsForAutoComplete(searchString)){
            result1.add(new OrderItemWrapper(item.getOrdr().getOrderNumber(), item.getCharge()));
        }
        return result1;
    }

    List<OrderItem> orderItemsForAutoComplete(final String searchString){
        List<OrderItem> result = new ArrayList<>();

        for (Order order : orderRepository.matchByOrderNumber(searchString)){
            for (OrderItem item : order.getItems()){
                if (!result.contains(item)){
                    result.add(item);
                }
            }
        }

        if (hasSeller()){
            result = Lists.newArrayList(
                    FluentIterable.from(result)
                    .filter(x->x.getOrdr().getSeller()!=null && x.getOrdr().getSeller().equals(getSeller()))
                    .toList()
            );
        }
        if (hasCharge()) {
            result = Lists.newArrayList(
                    FluentIterable.from(result)
                            .filter(x->x.getCharge().equals(getCharge()))
                            .toList()
            );
        }
        if (hasProject()) {
            result = Lists.newArrayList(
                    FluentIterable.from(result)
                            .filter(x->x.getProject()!=null && x.getProject().equals(getProject()))
                            .toList()
            );
        }
        return result;
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    public IncomingInvoiceViewModel changeInvoiceDetails(
            final String invoiceNumber,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Organisation buyer,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Organisation seller,
            @Parameter(optionality = Optionality.OPTIONAL)
            final LocalDate invoiceDate,
            final LocalDate dueDate,
            @Parameter(optionality = Optionality.OPTIONAL)
            final PaymentMethod paymentMethod
    ){
        setInvoiceNumber(invoiceNumber);
        setBuyer(buyer);
        setSeller(seller);
        setInvoiceDate(invoiceDate);
        setDueDate(dueDate);
        setPaymentMethod(paymentMethod);
        return this;
    }

    public String default0ChangeInvoiceDetails(){
        return getInvoiceNumber();
    }

    public Organisation default1ChangeInvoiceDetails(){
        return getBuyer();
    }

    public Organisation default2ChangeInvoiceDetails(){
        return getSeller();
    }

    public LocalDate default3ChangeInvoiceDetails(){
        return getInvoiceDate();
    }

    public LocalDate default4ChangeInvoiceDetails(){
        return getDueDate();
    }

    public PaymentMethod default5ChangeInvoiceDetails(){
        return getPaymentMethod()==null ? PaymentMethod.BANK_TRANSFER : getPaymentMethod();
    }


    @Programmatic
    public void autoFillIn(){
        if (hasOrderItem()){
            Order order = orderRepository.findByOrderNumber(getOrderItem().getOrderNumber());
            OrderItem orderItem = orderItemRepository.findByOrderAndCharge(order, getOrderItem().getCharge());
            if (!(hasNet() && hasGross() && hasVat())){
                setNetAmount(orderItem.getNetAmount());
                setVatAmount(orderItem.getVatAmount());
                setGrossAmount(orderItem.getGrossAmount());
            }
            if (!hasTax()){
                setTax(orderItem.getTax());
            }
            if (!hasBuyer()){
                setBuyer((Organisation) order.getBuyer());
            }
            if (!hasSeller()){
                setSeller((Organisation) order.getSeller());
            }
            if (!hasDescription()){
                setDescription(orderItem.getDescription());
            }
            if (!hasCharge()){
                setCharge(orderItem.getCharge());
            }
            if (!hasProject()){
                setProject(orderItem.getProject());
            }
            if (!hasFixedAsset()){
                setFixedAsset(orderItem.getFixedAsset());
            }
            if (!hasPeriod()){
                setPeriod(orderItem.getPeriod());
            }
        }
    }

    @Programmatic
    public boolean hasOrderItem(){
        return getOrderItem()==null ? false : true;
    }

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private OrderItemRepository orderItemRepository;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    OrderRepository orderRepository;

}
