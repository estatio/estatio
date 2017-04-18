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

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.wicketstuff.pdfjs.Scale;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.scratchpad.Scratchpad;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.wicket.pdfjs.cpt.applib.PdfJsViewer;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentRepository;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "toDoItemDto")
@XmlType(
        propOrder = {
                "idx",
                "documents"
//                "documentOids"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
public class IncomingInvoiceViewModel
                implements Comparable<IncomingInvoiceViewModel> {

    public IncomingInvoiceViewModel() {}
    public IncomingInvoiceViewModel(final List<Document> documents, final Integer idx) {
        setDocuments(documents);
        setIdx(idx);
    }

    public String title() {
        Document selected = getSelected();
        return getSelected() != null ? titleService.titleOf(selected): "No incoming documents";
    }

    public List<IncomingInvoiceViewModel> getIncomingDocuments() {
        List<IncomingInvoiceViewModel> viewModels = Lists.newArrayList();
        List<Document> documents = getDocuments();
        for (int i = 0; i < documents.size(); i++) {
            IncomingInvoiceViewModel viewModel = i == getIdx() ? this : new IncomingInvoiceViewModel(documents, i);
            viewModels.add(viewModel);
        }
        return viewModels;
    }

    @Collection(hidden = Where.EVERYWHERE)
    @XmlElementWrapper
    @XmlElement
    @Getter @Setter
    private List<Document> documents = Lists.newArrayList();

    @Property(hidden = Where.ALL_TABLES)
    @PdfJsViewer(initialPageNum = 1, initialScale = Scale._1_00, initialHeight = 600)
    public Blob getBlob() {

        // HACK: only displayed on forms, so use to set the context of which object is being rendered.
        cssHighlighter.setContext(this);

        return getSelected() != null ? getSelected().getBlob() : null;
    }

    @XmlElement(required = false)
    @Getter @Setter
    private Integer idx;

    public Document getSelected() {
        if(getIdx() == null) {
            return null;
        }
        return getDocuments().get(getIdx());
    }

    @Override
    public int compareTo(final IncomingInvoiceViewModel o) {
        if(idx == null && o.idx != null) { return -1; }
        if(idx != null && o.idx == null) { return +1; }
        return idx - o.idx;
    }

    @Override public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final IncomingInvoiceViewModel that = (IncomingInvoiceViewModel) o;

        return idx != null ? idx.equals(that.idx) : that.idx == null;
    }

    @Override
    public int hashCode() {
        return idx != null ? idx.hashCode() : 0;
    }

    @Mixin(method="act")
    public static class previous {
        private final IncomingInvoiceViewModel viewModel;
        public previous(final IncomingInvoiceViewModel viewModel) {
            this.viewModel = viewModel;
        }
        @Action(semantics = SemanticsOf.SAFE)
        public IncomingInvoiceViewModel act() {
            return factory.create(viewModel.getDocuments(), viewModel.getIdx()-1);
        }

        public String disableAct() {
            if(viewModel.getIdx() == null) {
                return "No documents";
            }
            return viewModel.getIdx() == 0 ? "At start" : null;
        }

        @XmlTransient
        @Inject
        Factory factory;
    }
    
    @Mixin(method="act")
    public static class next {
        private final IncomingInvoiceViewModel viewModel;
        public next(final IncomingInvoiceViewModel viewModel) {
            this.viewModel = viewModel;
        }
        @Action(semantics = SemanticsOf.SAFE)
        public IncomingInvoiceViewModel act() {
            return factory.create(viewModel.getDocuments(), viewModel.getIdx()+1);
        }

        public String disableAct() {
            if(viewModel.getIdx() == null) {
                return "No documents";
            }
            return viewModel.getIdx() == viewModel.getDocuments().size() - 1 ? "At end" : null;
        }

        @XmlTransient
        @Inject
        Factory factory;
    }


    @DomainService(nature = NatureOfService.DOMAIN)
    public static class Factory {

        @Programmatic
        public IncomingInvoiceViewModel create(final List<Document> documents, final Integer idx) {
            IncomingInvoiceViewModel viewModel = new IncomingInvoiceViewModel(documents, idx);
            serviceRegistry.injectServicesInto(viewModel);
            return viewModel;
        }

        @Inject
        ServiceRegistry2 serviceRegistry;
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class CssHighlighter extends AbstractSubscriber {

        @EventHandler
        @Subscribe
        public void on(Document.CssClassUiEvent ev) {
            if(getContext() == null) {
                return;
            }
            if(ev.getSource() == getContext().getSelected()) {
                ev.setCssClass("selected");
            }
        }

        private IncomingInvoiceViewModel getContext() {
            return (IncomingInvoiceViewModel) scratchpad.get("context");
        }
        void setContext(final IncomingInvoiceViewModel viewModel) {
            scratchpad.put("context", viewModel);
        }

        @Inject
        Scratchpad scratchpad;
    }


    @XmlTransient
    @Inject
    CssHighlighter cssHighlighter;

    @XmlTransient
    @Inject
    Factory factory;

    @XmlTransient
    @Inject
    DocumentRepository documentRepository;

    @Inject
    @XmlTransient
    TitleService titleService;



}
