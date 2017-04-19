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

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.HasBuyer;
import org.estatio.capex.dom.documents.HasDocumentAbstract;
import org.estatio.capex.dom.documents.HasSeller;
import org.estatio.capex.dom.project.Project;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.tax.Tax;

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
public class IncomingOrderViewModel extends HasDocumentAbstract implements HasBuyer, HasSeller {

    public IncomingOrderViewModel() {}
    public IncomingOrderViewModel(final Document document) {
        super(document);
    }

    private String orderNumber;

    private Organisation buyer;

    private Organisation seller;

    private String sellerOrderReference;

    private LocalDate orderDate;

    private String description;

    private Charge charge;

    private FixedAsset<?> fixedAsset;

    private Project project;

    private String period;

    private BigDecimal netAmount;

    private BigDecimal vatAmount;

    private Tax tax;

    private BigDecimal grossAmount;

}
