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

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategy;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategyRepository;

@Mixin
public class DocumentTemplate_changeNameRenderingStrategy {

    //region > constructor
    private final DocumentTemplate documentTemplate;

    public DocumentTemplate_changeNameRenderingStrategy(final DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<DocumentTemplate_changeNameRenderingStrategy>  { }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    public DocumentTemplate $$(
            final RenderingStrategy renderingStrategy) {
        documentTemplate.setNameRenderingStrategy(renderingStrategy);
        return documentTemplate;
    }

    public RenderingStrategy default0$$() {
        return currentNameRenderingStrategy();
    }

    public List<RenderingStrategy> choices0$$() {
        return renderingStrategyRepository.findForUseWithSubjectText();
    }

    private RenderingStrategy currentNameRenderingStrategy() {
        return documentTemplate.getNameRenderingStrategy();
    }


    @Inject
    private DocumentTemplateRepository documentTemplateRepository;
    @Inject
    private RenderingStrategyRepository renderingStrategyRepository;


}
