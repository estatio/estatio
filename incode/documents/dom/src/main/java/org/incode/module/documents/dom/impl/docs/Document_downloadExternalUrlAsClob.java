/*
 *  Copyright 2016 Dan Haywood
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
package org.incode.module.documents.dom.impl.docs;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Clob;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.spi.UrlDownloadService;

@Mixin
public class Document_downloadExternalUrlAsClob {

    //region > constructor
    private final Document document;

    public Document_downloadExternalUrlAsClob(final Document document) {
        this.document = document;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<Document_downloadExternalUrlAsClob> { }
    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(named = "Download")
    public Clob $$() {
        return urlDownloadService.downloadAsClob(document);
    }

    public boolean hide$$() {
        return document.getSort() != DocumentSort.EXTERNAL_CLOB;
    }

    @Inject
    UrlDownloadService urlDownloadService;

}
