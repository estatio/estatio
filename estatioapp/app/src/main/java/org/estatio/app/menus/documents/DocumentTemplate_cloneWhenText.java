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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.impl.applicability.Applicability;
import org.incode.module.documents.dom.impl.docs.DocumentSort;
import org.incode.module.documents.dom.impl.docs.DocumentTemplate;
import org.incode.module.documents.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.documents.dom.impl.rendering.RenderingStrategy;
import org.incode.module.documents.dom.impl.types.DocumentType;

import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;

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
            @Parameter(maxLength = DocumentsModule.JdoColumnLength.NAME)
            @ParameterLayout(named = "Name")
            final String name,
            final ApplicationTenancy applicationTenancy,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Date")
            final LocalDate date,
            @ParameterLayout(named = "Text", multiLine = DocumentsModule.Constants.TEXT_MULTILINE)
            final String templateText,
            final RenderingStrategy contentRenderingStrategy,
            @Parameter(maxLength = DocumentsModule.JdoColumnLength.SUBJECT_TEXT)
            @ParameterLayout(named = "Subject text")
            final String subjectText,
            @ParameterLayout(named = "Subject rendering strategy")
            final RenderingStrategy subjectRenderingStrategy,
            @ParameterLayout(named = "Preview only?")
            final boolean previewOnly) {

        final DocumentType type = documentTemplate.getType();
        final String mimeType = documentTemplate.getMimeType();
        final String fileSuffix = documentTemplate.getFileSuffix();
        final DocumentTemplate template = documentTemplateRepository.createText(
                type, date, applicationTenancy.getPath(), fileSuffix, previewOnly, name, mimeType, templateText, contentRenderingStrategy,
                subjectText, subjectRenderingStrategy);
        for (Applicability applicability : documentTemplate.getAppliesTo()) {
            template.applicable(applicability.getDomainClassName(), applicability.getBinderClassName());
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
        return documentTemplate.getSubjectText();
    }

    public RenderingStrategy default6$$() {
        return documentTemplate.getSubjectRenderingStrategy();
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
    private EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;
    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;
    @Inject
    private DocumentTemplateRepository documentTemplateRepository;


}
