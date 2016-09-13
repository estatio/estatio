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
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategy;

@Mixin
public class DocumentTemplate_changeRenderingStrategy {

    //region > constructor
    private final DocumentTemplate documentTemplate;

    public DocumentTemplate_changeRenderingStrategy(final DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<DocumentTemplate_changeRenderingStrategy>  { }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    public DocumentTemplate $$(
            final RenderingStrategy renderingStrategy) {
        documentTemplate.setRenderingStrategy(renderingStrategy);
        return documentTemplate;
    }

    public RenderingStrategy default0$$() {
        return currentRenderingStrategy();
    }

    public TranslatableString validate0$$(final RenderingStrategy proposedRenderingStrategy) {
        if(currentRenderingStrategy().getInputNature() != proposedRenderingStrategy.getInputNature()) {
            return TranslatableString.tr("The input nature of the new rendering strategy (binary or characters) must be the same as the current");
        }
        return null;
    }

    private RenderingStrategy currentRenderingStrategy() {
        return documentTemplate.getRenderingStrategy();
    }


    @Inject
    private DocumentTemplateRepository documentTemplateRepository;


}
