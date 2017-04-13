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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.scratchpad.Scratchpad;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentRepository;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "toDoItemDto")
@XmlType(
        propOrder = {
                "idx"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
public class IncomingInvoiceViewModel {


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

    public String title() {
        cssHighlighter.setContext(this);
        Document selected = getSelected();
        return selected != null ? titleService.titleOf(selected): "No documents";
    }

    public List<Document> getDocuments() {
        return documentRepository.findWithNoPaperclips();
    }


    @XmlTransient
//    @PdfJsViewer(initialPageNum = 1, initialScale = 1.0d, initialHeight = 600)
    public Blob getBlob() {
        return getSelected() != null ? getSelected().getBlob() : null;
    }


    @XmlTransient
    @Property(hidden = Where.EVERYWHERE)
    public int getNumObjects() {
        return getDocuments().size();
    }


    @XmlElement(required = true)
    @Getter @Setter
    private int idx;


    public Document getSelected() {
        return getNumObjects() >= getIdx() + 1 ? getDocuments().get(getIdx()) : null;
    }


    public IncomingInvoiceViewModel previous() {
        final IncomingInvoiceViewModel viewModel = factoryService.instantiate(IncomingInvoiceViewModel.class);
        viewModel.setIdx(getIdx()-1);
        return viewModel;
    }
    public String disablePrevious() {
        return getIdx() == 0 ? "At start" : null;
    }


    public IncomingInvoiceViewModel next() {
        final IncomingInvoiceViewModel viewModel = factoryService.instantiate(IncomingInvoiceViewModel.class);
        viewModel.setIdx(getIdx()+1);
        return viewModel;
    }
    public String disableNext() {
        return getIdx() == getNumObjects() - 1 ? "At end" : null;
    }


    @XmlTransient
    @javax.inject.Inject
    CssHighlighter cssHighlighter;

    @XmlTransient
    @Inject
    FactoryService factoryService;

    @Inject
    @XmlTransient
    DocumentRepository documentRepository;

    @Inject
    @XmlTransient
    TitleService titleService;


}
