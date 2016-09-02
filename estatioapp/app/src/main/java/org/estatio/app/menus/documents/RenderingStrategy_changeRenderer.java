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
package org.estatio.app.menus.documents;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.rendering.RenderingStrategy;
import org.incode.module.documents.dom.valuetypes.RendererClassNameSpecification;

@Mixin
public class RenderingStrategy_changeRenderer {

    //region > constructor
    private final RenderingStrategy renderingStrategy;

    public RenderingStrategy_changeRenderer(final RenderingStrategy renderingStrategy) {
        this.renderingStrategy = renderingStrategy;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<RenderingStrategy_changeRenderer>  { }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    public RenderingStrategy $$(
            @Parameter(maxLength = DocumentsModule.JdoColumnLength.NAME, mustSatisfy = RendererClassNameSpecification.class)
            @ParameterLayout(named = "Renderer class name")
            final String rendererClassName) {

        final Class rendererClass = rendererClassNameService.asRendererClass(rendererClassName);
        renderingStrategy.setRendererClassName(rendererClass.getName());
        return renderingStrategy;
    }

    public List<String> choices0$$() {
        return rendererClassNameService.renderClassNamesFor(renderingStrategy.getDocumentNature());
    }

    @Inject
    private RendererClassNameService rendererClassNameService;

}
