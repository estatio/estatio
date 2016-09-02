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

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

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
import org.apache.isis.applib.services.classdiscovery.ClassDiscoveryService2;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.docs.DocumentNature;
import org.incode.module.documents.dom.rendering.Renderer;
import org.incode.module.documents.dom.rendering.RenderingStrategy;
import org.incode.module.documents.dom.rendering.RenderingStrategyRepository;
import org.incode.module.documents.dom.services.ClassService;
import org.incode.module.documents.dom.valuetypes.RendererClassNameSpecification;

import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainService;
import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Documents",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "83.07")
public class RenderingStrategyMenu extends UdoDomainService<RenderingStrategyMenu> {

    public RenderingStrategyMenu() {
        super(RenderingStrategyMenu.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @MemberOrder(sequence = "1")
    public RenderingStrategy newStrategy(
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
            final DocumentNature documentNature,
            @Parameter(maxLength = DocumentsModule.JdoColumnLength.NAME, mustSatisfy = RendererClassNameSpecification.class)
            @ParameterLayout(named = "Renderer class name")
            final String rendererClassName) {

        final String rendererClassName2 = rendererClassName.replace("o.e", "org.estatio");
        final Class rendererClass = classService.load(rendererClassName2);
        return renderingStrategyRepository.create(reference, name, documentNature, rendererClass);
    }

    public List<String> choices3NewStrategy() {
        final Set<Class<? extends Renderer>> rendererClasses = classDiscoveryService2
                .findSubTypesOfClasses(Renderer.class, "org.estatio");

        return Lists.newArrayList(rendererClasses.stream()
                                    .filter(x -> !Modifier.isAbstract(x.getModifiers()))
                                    .map(x -> x.getName())
                                    .map(x -> x.replace("org.estatio", "o.e"))
                                    .collect(Collectors.toList()));
    }
    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<RenderingStrategy> allStrategies() {
        return renderingStrategyRepository.allStrategies();
    }


    // //////////////////////////////////////

    @Inject
    private EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

    @Inject
    private RenderingStrategyRepository renderingStrategyRepository;

    @Inject
    private ClassDiscoveryService2 classDiscoveryService2;

    @Inject
    private ClassService classService;


}
