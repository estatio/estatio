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

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.docs.DocumentSort;
import org.incode.module.documents.dom.rendering.RenderingStrategy;
import org.incode.module.documents.dom.templates.DocumentTemplate;
import org.incode.module.documents.dom.templates.DocumentTemplateRepository;
import org.incode.module.documents.dom.types.DocumentType;
import org.incode.module.documents.dom.valuetypes.FullyQualifiedClassNameSpecification;

@Mixin
public class DocumentTemplate_cloneWhenText extends DocumentTemplate_cloneAbstract {

    public DocumentTemplate_cloneWhenText(final DocumentTemplate documentTemplate) {
        super(documentTemplate);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public DocumentTemplate $$(
            @Parameter(maxLength = DocumentsModule.JdoColumnLength.NAME)
            @ParameterLayout(named = "Name")
            final String name,
            final ApplicationTenancy applicationTenancy,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Date")
            final LocalDate date,
            @ParameterLayout(named = "Text", multiLine = DocumentsModule.Constants.CLOB_MULTILINE)
            final String templateText,
            @Parameter(maxLength = DocumentsModule.JdoColumnLength.FQCN, mustSatisfy = FullyQualifiedClassNameSpecification.class)
            @ParameterLayout(named = "Data model class name")
            final String dataModelClassName,
            final RenderingStrategy renderingStrategy) {

        final DocumentType type = documentTemplate.getType();
        final String mimeType = documentTemplate.getMimeType();
        return documentTemplateRepository.createText(
                type, date, applicationTenancy.getPath(), name, mimeType, templateText, dataModelClassName, renderingStrategy
        );
    }

    public boolean hide$$() {
        return documentTemplate.getSort() != DocumentSort.TEXT;
    }

    public String default0$$() {
        return super.default0$$();
    }

    public ApplicationTenancy default1$$() {
        return super.default1$$();
    }
    public List<ApplicationTenancy> choices1$$() {
        return super.choices1$$();
    }

    public LocalDate default2$$() {
        return super.default2$$();
    }

    public String default3$$() {
        return documentTemplate.getText();
    }

    public String default4$$() {
        return documentTemplate.getDataModelClassName();
    }

    public RenderingStrategy default5$$() {
        return documentTemplate.getRenderingStrategy();
    }


    public TranslatableString validate$$(
            final String name,
            final ApplicationTenancy proposedApplicationTenancy,
            final LocalDate proposedDate,
            final String templateText,
            final String dataModelClassName,
            final RenderingStrategy renderingStrategy) {
        return validateApplicationTenancyAndDate(proposedApplicationTenancy, proposedDate);
    }



    @Inject
    private DocumentTemplateRepository documentTemplateRepository;


}
