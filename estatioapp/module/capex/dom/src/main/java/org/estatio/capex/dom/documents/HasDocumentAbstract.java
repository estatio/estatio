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

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlTransient;

import org.wicketstuff.pdfjs.Scale;

import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.wicket.pdfjs.cpt.applib.PdfJsViewer;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.types.DocumentType;

import lombok.Getter;
import lombok.Setter;

@XmlTransient // so not mapped
public abstract class HasDocumentAbstract implements HasDocument {

    public HasDocumentAbstract() {}
    public HasDocumentAbstract(final Document document) {
        this.document = document;
    }

    public String title() {
        return titleService.titleOf(getDocument());
    }

    @Getter @Setter
    protected Document document;

    public DocumentType getType() {
        return getDocument().getType();
    }

    @Property(hidden = Where.ALL_TABLES)
    @PdfJsViewer(initialPageNum = 1, initialScale = Scale._1_00, initialHeight = 600)
    public Blob getBlob() {
        return getDocument() != null ? getDocument().getBlob() : null;
    }

    @XmlTransient
    @Inject
    protected TitleService titleService;


}
