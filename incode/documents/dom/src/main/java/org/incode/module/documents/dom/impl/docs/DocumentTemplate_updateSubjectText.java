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

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.documents.dom.DocumentsModule;

@Mixin
public class DocumentTemplate_updateSubjectText {

    //region > constructor
    private final DocumentTemplate documentTemplate;

    public DocumentTemplate_updateSubjectText(final DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<DocumentTemplate_updateSubjectText>  { }
    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public DocumentTemplate $$(
            @ParameterLayout(named = "Text", multiLine = DocumentsModule.Constants.TEXT_MULTILINE)
            final String text
    ) {
        documentTemplate.setText(text);
        return documentTemplate;
    }

    public String default0$$() {
        return documentTemplate.getSubjectText();
    }



}
