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

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.incode.module.documents.dom.DocumentsModule;

@Mixin
public class DocumentTemplate_changeDate {

    //region > constructor
    private final DocumentTemplate documentTemplate;

    public DocumentTemplate_changeDate(final DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<DocumentTemplate_changeDate>  { }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    public DocumentTemplate $$(
            @ParameterLayout(named = "New date")
            final LocalDate date) {
        documentTemplate.setDate(date);
        return documentTemplate;
    }

    public LocalDate default0$$() {
        return documentTemplate.getDate();
    }

    public TranslatableString validate0$$(LocalDate proposedDate) {
        final DocumentTemplate original = documentTemplate;
        final String proposedAtPath = documentTemplate.getAtPath();

        return documentTemplateRepository.validateApplicationTenancyAndDate(original.getType(), proposedAtPath, proposedDate, original);
    }


    @Inject
    private DocumentTemplateRepository documentTemplateRepository;


}
