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
package org.estatio.capex.dom.documents;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.joda.time.DateTime;
import org.wicketstuff.pdfjs.Scale;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.wicket.pdfjs.cpt.applib.PdfJsViewer;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.types.DocumentType;

import org.estatio.capex.dom.documents.invoice.IncomingInvoiceViewModel;
import org.estatio.capex.dom.documents.order.IncomingOrderViewModel;
import org.estatio.capex.dom.documents.incoming.IncomingDocumentViewModel;
import org.estatio.dom.invoice.DocumentTypeData;

import lombok.Getter;
import lombok.Setter;

@XmlTransient // so not mapped
public abstract class HasDocumentAbstract implements HasDocument {

    public HasDocumentAbstract() {}
    public HasDocumentAbstract(final Document document) {
        this.document = document;
    }

    public String title() {
        return getDocument().getName();
    }

    @Getter @Setter
    protected Document document;

    public DocumentType getType() {
        return getDocument().getType();
    }

    public DateTime getCreatedAt() {
        return getDocument().getCreatedAt();
    }

    @Property(hidden = Where.ALL_TABLES)
    @PdfJsViewer(initialPageNum = 1, initialScale = Scale._2_00, initialHeight = 900)
    public Blob getBlob() {
        return getDocument() != null ? getDocument().getBlob() : null;
    }

    @XmlTransient
    @Inject
    protected TitleService titleService;


    @DomainService(nature = NatureOfService.DOMAIN)
    public static class Factory {

        @Programmatic
        public HasDocumentAbstract map(final Document document) {
            HasDocumentAbstract viewModel = createFor(document);
            return viewModel != null ? serviceRegistry2.injectServicesInto(viewModel) :  null;
        }

        private HasDocumentAbstract createFor(final Document document) {
            if(DocumentTypeData.INCOMING_ORDER.isDocTypeFor(document)) {
                return new IncomingOrderViewModel(document);
            }
            if(DocumentTypeData.INCOMING_INVOICE.isDocTypeFor(document)) {
                return new IncomingInvoiceViewModel(document);
            }
            if(DocumentTypeData.INCOMING.isDocTypeFor(document)) {
                return new IncomingDocumentViewModel(document);
            }
            return null;
        }

        @Programmatic
        public List<HasDocumentAbstract> map(final List<Document> documents) {
            return Lists.newArrayList(
                    FluentIterable.from(documents)
                            .transform(doc -> map(doc))
                            .filter(Objects::nonNull)
                            .toList());
        }

        @Inject
        ServiceRegistry2 serviceRegistry2;

    }

}
