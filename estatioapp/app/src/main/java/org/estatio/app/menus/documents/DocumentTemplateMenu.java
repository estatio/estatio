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
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.rendering.RenderingStrategy;
import org.incode.module.documents.dom.templates.DocumentTemplate;
import org.incode.module.documents.dom.templates.DocumentTemplateRepository;
import org.incode.module.documents.dom.types.DocumentType;
import org.incode.module.documents.dom.valuetypes.FullyQualifiedClassNameSpecification;
import org.incode.module.documents.dom.valuetypes.MimeTypeSpecification;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Documents",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "83.15")
public class DocumentTemplateMenu extends UdoDomainService<DocumentTemplateMenu> {

    public DocumentTemplateMenu() {
        super(DocumentTemplateMenu.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @MemberOrder(sequence = "1")
    public DocumentTemplate newTextTemplate(
            final DocumentType type,
            @Parameter(
                    optionality = Optionality.OPTIONAL, // will default to the name of the doc type
                    maxLength = DocumentsModule.JdoColumnLength.NAME)
            @ParameterLayout(named = "Name", describedAs = "Optional, will defaults to the name of selected document type")
            final String name,
            @Parameter(maxLength = DocumentsModule.JdoColumnLength.MIME_TYPE, mustSatisfy = MimeTypeSpecification.class)
            @ParameterLayout(named = "Mime type")
            final String mimeType,
            final ApplicationTenancy applicationTenancy,
            @ParameterLayout(named = "Text", multiLine = DocumentsModule.Constants.CLOB_MULTILINE)
            final String templateText,
            @Parameter(maxLength = DocumentsModule.JdoColumnLength.FQCN, mustSatisfy = FullyQualifiedClassNameSpecification.class)
            @ParameterLayout(named = "Data model class name")
            final String dataModelClassName,
            final RenderingStrategy renderingStrategy) {

        final String documentName = name != null? name : type.getName();
        return documentTemplateRepository.createText(
                type, applicationTenancy.getPath(), documentName, mimeType, templateText, dataModelClassName, renderingStrategy
        );
    }

    public String default2NewTextTemplate() {
        return "text/html";
    }
    public List<ApplicationTenancy> choices3NewTextTemplate() {
        return estatioApplicationTenancyRepository.allTenancies();
    }



    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @MemberOrder(sequence = "2")
    public DocumentTemplate newClobTemplate(
            final DocumentType type,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = DocumentsModule.JdoColumnLength.NAME)
            @ParameterLayout(named = "Name", describedAs = "Optional, will default to the file name of the uploaded Clob")
            final String name,
            final ApplicationTenancy applicationTenancy,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Clob clob,
            @Parameter(maxLength = DocumentsModule.JdoColumnLength.FQCN, mustSatisfy = FullyQualifiedClassNameSpecification.class)
            @ParameterLayout(named = "Data model class name")
            final String dataModelClassName,
            final RenderingStrategy renderingStrategy) {

        final DocumentTemplate template = documentTemplateRepository.createClob(
                type, applicationTenancy.getPath(), clob, dataModelClassName, renderingStrategy);
        if(name != null) {
            template.setName(name);
        }
        return template;
    }

    public List<ApplicationTenancy> choices2NewClobTemplate() {
        return estatioApplicationTenancyRepository.allTenancies();
    }



    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_NEITHER)
    @MemberOrder(sequence = "3")
    public DocumentTemplate newBlobTemplate(
            final DocumentType type,
            @Parameter(
                    optionality = Optionality.OPTIONAL, // will default to the name of the doc type
                    maxLength = DocumentsModule.JdoColumnLength.NAME)
            @ParameterLayout(named = "Name", describedAs = "Optional, will default to the file name of the uploaded Blob")
            final String name,
            @Parameter(maxLength = DocumentsModule.JdoColumnLength.MIME_TYPE, mustSatisfy = MimeTypeSpecification.class)
            final ApplicationTenancy applicationTenancy,
            final Blob blob,
            @Parameter(maxLength = DocumentsModule.JdoColumnLength.FQCN, mustSatisfy = FullyQualifiedClassNameSpecification.class)
            @ParameterLayout(named = "Data model class name")
            final String dataModelClassName,
            final RenderingStrategy renderingStrategy) {

        final DocumentTemplate template = documentTemplateRepository.createBlob(
                type, applicationTenancy.getPath(), blob, dataModelClassName, renderingStrategy
        );
        if(name != null) {
            template.setName(name);
        }
        return template;
    }

    public List<ApplicationTenancy> choices2NewBlobTemplate() {
        return estatioApplicationTenancyRepository.allTenancies();
    }


    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "4")
    public List<DocumentTemplate> allTemplates() {
        return documentTemplateRepository.allTemplates();
    }


    // //////////////////////////////////////

    @Inject
    private EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

    @Inject
    private DocumentTemplateRepository documentTemplateRepository;


}
