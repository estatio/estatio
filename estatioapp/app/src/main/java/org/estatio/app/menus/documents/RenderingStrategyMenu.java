/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
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
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.impl.docs.DocumentNature;
import org.incode.module.documents.dom.impl.rendering.Renderer;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategy;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategyRepository;
import org.incode.module.documents.dom.services.ClassNameViewModel;
import org.incode.module.documents.dom.impl.rendering.RendererClassNameSpecification;

import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainService;
import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "80.152")
public class RenderingStrategyMenu extends UdoDomainService<RenderingStrategyMenu> {

    public RenderingStrategyMenu() {
        super(RenderingStrategyMenu.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @MemberOrder(sequence = "1")
    public RenderingStrategy newRenderingStrategy(
            @Parameter(
                    regexPattern = RegexValidation.REFERENCE,
                    regexPatternReplacement = RegexValidation.REFERENCE_DESCRIPTION,
                    maxLength = DocumentsModule.JdoColumnLength.REFERENCE
            )
            @ParameterLayout(named = "Reference")
            final String reference,
            @Parameter(maxLength = DocumentsModule.JdoColumnLength.NAME)
            @ParameterLayout(named = "Name")
            final String name,
            @ParameterLayout(named = "Input nature")
            final DocumentNature inputNature,
            @ParameterLayout(named = "Output nature")
            final DocumentNature outputNature,
            @Parameter(
                    maxLength = DocumentsModule.JdoColumnLength.FQCN,
                    mustSatisfy = RendererClassNameSpecification.class
            )
            @ParameterLayout(named = "Renderer class name")
            final ClassNameViewModel classViewModel) {

        final Class<? extends Renderer> rendererClass =
                rendererClassNameService.asClass(classViewModel.getFullyQualifiedClassName());
        return renderingStrategyRepository.create(reference, name, inputNature, outputNature , rendererClass);
    }


    public List<ClassNameViewModel> choices4NewRenderingStrategy(
            final String reference,
            final String name,
            final DocumentNature inputNature,
            final DocumentNature outputNature
            ) {
        return rendererClassNameService.renderClassNamesFor(inputNature, outputNature);
    }


    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<RenderingStrategy> allRenderingStrategies() {
        return renderingStrategyRepository.allStrategies();
    }


    // //////////////////////////////////////

    @Inject
    private EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

    @Inject
    private RenderingStrategyRepository renderingStrategyRepository;

    @Inject
    private RendererClassNameService rendererClassNameService;


}
