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
package org.estatio.module.application.platform.document;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.applicability.Applicability;
import org.incode.module.document.dom.impl.docs.DocumentSort;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.impl.rendering.RenderingStrategy;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.types.NameType;

import org.estatio.module.base.dom.apptenancy.EstatioApplicationTenancyRepository;

/**
 * TODO: remove this once move to RenderingStrategyData
 */
@Mixin
public class DocumentTemplate_cloneWhenText {

    protected final DocumentTemplate documentTemplate;

    public DocumentTemplate_cloneWhenText(final DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(
            named = "Clone",
            contributed = Contributed.AS_ACTION
    )
    public DocumentTemplate $$(
            @Parameter(maxLength = NameType.Meta.MAX_LEN)
            @ParameterLayout(named = "Name")
            final String name,
            final ApplicationTenancy applicationTenancy,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Date")
            final LocalDate date,
            @ParameterLayout(named = "Text", multiLine = DocumentModule.Constants.TEXT_MULTILINE)
            final String templateText,
            @ParameterLayout(named = "Content rendering strategy")
            final RenderingStrategy contentRenderingStrategy,
            @Parameter(maxLength = DocumentTemplate.NameTextType.Meta.MAX_LEN)
            final String nameText,
            @ParameterLayout(named = "Name rendering strategy")
            final RenderingStrategy nameRenderingStrategy,
            @ParameterLayout(named = "Preview only?")
            final boolean previewOnly) {

        final DocumentType type = documentTemplate.getType();
        final String mimeType = documentTemplate.getMimeType();
        final String fileSuffix = documentTemplate.getFileSuffix();

        final DocumentTemplate template = documentTemplateRepository.createText(
                type, date, applicationTenancy.getPath(), fileSuffix, previewOnly, name, mimeType,
                templateText, contentRenderingStrategy,
                nameText, nameRenderingStrategy);

        final DocumentTemplate._applicable template_applicable =
                factoryService.mixin(DocumentTemplate._applicable.class, template);
        for (Applicability applicability : documentTemplate.getAppliesTo()) {
            template_applicable.applicable(
                    applicability.getDomainClassName(),
                    applicability.getRendererModelFactoryClassName(),
                    applicability.getAttachmentAdvisorClassName());
        }
        return template;
    }


    // hide
    public boolean hide$$() {
        return documentTemplate.getSort() != DocumentSort.TEXT;
    }


    // defaults

    public String default0$$() {
        return documentTemplate.getName();
    }

    public ApplicationTenancy default1$$() {
        final String atPath = documentTemplate.getAtPath();
        return applicationTenancyRepository.findByPath(atPath);
    }

    public LocalDate default2$$() {
        return documentTemplate.getDate();
    }

    public String default3$$() {
        return documentTemplate.getText();
    }

    public RenderingStrategy default4$$() {
        return documentTemplate.getContentRenderingStrategy();
    }

    public String default5$$() {
        return documentTemplate.getNameText();
    }

    public RenderingStrategy default6$$() {
        return documentTemplate.getNameRenderingStrategy();
    }

    public boolean default7$$() {
        return documentTemplate.isPreviewOnly();
    }


    // choices
    public List<ApplicationTenancy> choices1$$() {
        return estatioApplicationTenancyRepository.allTenancies();
    }


    // validate
    public TranslatableString validate$$(
            final String name,
            final ApplicationTenancy proposedApplicationTenancy,
            final LocalDate proposedDate,
            final String templateText,
            final RenderingStrategy customRenderingStrategy,
            final String subjectText,
            final RenderingStrategy subjectRenderingStrategy,
            final boolean previewOnly) {

        return documentTemplateRepository.validateApplicationTenancyAndDate(
                documentTemplate.getType(), proposedApplicationTenancy.getPath(), proposedDate, null);
    }


    @Inject
    EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;
    @Inject
    ApplicationTenancyRepository applicationTenancyRepository;
    @Inject
    DocumentTemplateRepository documentTemplateRepository;
    @Inject
    FactoryService factoryService;


}
