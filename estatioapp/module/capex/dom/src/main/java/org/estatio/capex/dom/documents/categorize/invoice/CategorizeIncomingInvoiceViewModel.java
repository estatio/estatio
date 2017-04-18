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
package org.estatio.capex.dom.documents.categorize.invoice;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.capex.dom.documents.HasDocument;
import org.estatio.capex.dom.documents.HasDocumentAbstract;
import org.estatio.capex.dom.documents.HasDocument_all;
import org.estatio.capex.dom.documents.IncomingDocumentViewModel;
import org.estatio.dom.invoice.DocumentTypeData;

@DomainObject(
        objectType = "capex.CategorizeIncomingInvoiceViewModel"
)
@XmlRootElement(name = "categorizeIncomingInvoice")
@XmlType(
        propOrder = {
                "document"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
public class CategorizeIncomingInvoiceViewModel extends HasDocumentAbstract {

    public CategorizeIncomingInvoiceViewModel() {}
    public CategorizeIncomingInvoiceViewModel(final IncomingDocumentViewModel viewModel) {
        super(viewModel.getDocument());
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class Subscriber extends AbstractSubscriber {

        @org.axonframework.eventhandling.annotation.EventHandler
        @com.google.common.eventbus.Subscribe
        public void on(HasDocument_all.ActionDomainEvent ev) {
            final HasDocument_all source = ev.getSource();
            final HasDocument hasDocument = source.getHasDocument();
            if(hasDocument instanceof CategorizeIncomingInvoiceViewModel) {
                Document document = hasDocument.getDocument();
                document.setType(DocumentTypeData.INCOMING_INVOICE.findUsing(documentTypeRepository));
            }
        }

        @Inject
        DocumentTypeRepository documentTypeRepository;

    }

}
