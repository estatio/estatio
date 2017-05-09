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
package org.estatio.capex.dom.documents.order;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.schema.utils.jaxbadapters.JodaLocalDateStringAdapter;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.incoming.IncomingOrderAndInvoiceViewModel;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.party.Party;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        // WORKAROUND: using fqcn as objectType because Isis' invalidation of cache in prototyping mode causing NPEs in some situations
        objectType = "org.estatio.capex.dom.documents.order.IncomingOrderViewModel",
        editing = Editing.ENABLED
)
@XmlRootElement(name = "categorizeIncomingOrder")
@XmlType(
        propOrder = {
                "document",
                "orderNumber",
                "buyer",
                "seller",
                "sellerOrderReference",
                "orderDate",
                "description",
                "charge",
                "fixedAsset",
                "project",
                "period",
                "netAmount",
                "vatAmount",
                "tax",
                "grossAmount"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@Getter @Setter
public class IncomingOrderViewModel extends IncomingOrderAndInvoiceViewModel {

    public IncomingOrderViewModel() {}
    public IncomingOrderViewModel(final Document document, final FixedAsset fixedAsset) {
        super(document, fixedAsset);
    }

    @Property(editing = Editing.ENABLED)
    private String orderNumber;

    @Property(editing = Editing.ENABLED)
    private String sellerOrderReference;

    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    @Property(editing = Editing.ENABLED)
    private LocalDate orderDate;


    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    public IncomingOrderViewModel changeOrderDetails(
            final String orderNumber,
            final Party buyer,
            final Party seller,
            @Nullable
            final String sellerOrderReference,
            @Nullable
            final LocalDate orderDate
    ){
        setOrderNumber(orderNumber);
        setBuyer(buyer);
        setSeller(seller);
        setSellerOrderReference(sellerOrderReference);
        setOrderDate(orderDate);
        return this;
    }

    public String default0ChangeOrderDetails(){
        return getOrderNumber();
    }

    public Party default1ChangeOrderDetails(){
        return getBuyer();
    }

    public Party default2ChangeOrderDetails(){
        return getSeller();
    }

    public String default3ChangeOrderDetails(){
        return getSellerOrderReference();
    }

    public LocalDate default4ChangeOrderDetails(){
        return getOrderDate();
    }

    String minimalRequiredDataToComplete(){
        StringBuilder buffer = new StringBuilder();
        if (getOrderNumber()==null){
            buffer.append("order number, ");
        }
        if (getBuyer()==null){
            buffer.append("buyer, ");
        }
        if (getSeller()==null){
            buffer.append("seller, ");
        }
        if (getDescription()==null){
            buffer.append("description, ");
        }
        if (getNetAmount()==null){
            buffer.append("net amount, ");
        }
        if (getGrossAmount()==null){
            buffer.append("gross amount, ");
        }
        if (getCharge()==null){
            buffer.append("charge, ");
        }
        if (getPeriod()==null){
            buffer.append("period, ");
        }
        final int buflen = buffer.length();
        return buflen != 0
                ? buffer.replace(buflen - 2, buflen, " required").toString()
                : null;
    }

}
