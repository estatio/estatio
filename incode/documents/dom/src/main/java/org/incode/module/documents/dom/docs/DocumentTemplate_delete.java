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
package org.incode.module.documents.dom.docs;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.links.Paperclip;
import org.incode.module.documents.dom.links.PaperclipRepository;

@Mixin
public class DocumentTemplate_delete {

    //region > constructor
    private final DocumentTemplate documentTemplate;

    public DocumentTemplate_delete(final DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<DocumentTemplate_delete>  { }
    @Action(
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE,
            domainEvent = ActionDomainEvent.class
    )
    public void $$() {
        documentTemplateRepository.delete(documentTemplate);
        return;
    }
    public TranslatableString disable$$() {
        final List<Paperclip> paperclips = paperclipRepository.findByDocument(documentTemplate);
        return !paperclips.isEmpty()
                ? TranslatableString.tr("This template is attached to objects")
                : null;
    }

    @Inject
    private PaperclipRepository paperclipRepository;

    @Inject
    private DocumentTemplateRepository documentTemplateRepository;


}
