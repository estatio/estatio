/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.app.menus.demo;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Clob;

import org.isisaddons.module.freemarker.dom.service.FreeMarkerService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.docs.Document;
import org.incode.module.documents.dom.docs.DocumentRepository;
import org.incode.module.documents.dom.templates.DocumentTemplate;
import org.incode.module.documents.dom.templates.DocumentTemplateRepository;
import org.incode.module.documents.dom.types.DocumentType;
import org.incode.module.documents.dom.types.DocumentTypeRepository;
import org.incode.module.documents.dom.valuetypes.FullyQualifiedClassNameSpecification;

import org.estatio.app.menus.documents.DocumentTemplateMenu;
import org.estatio.app.menus.documents.DocumentTypeMenu;

import freemarker.template.TemplateException;
import lombok.Data;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Prototyping",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "999")
public class DemoDocumentMenu {


    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "1")
    public DocumentType demoDocumentCreateDocumentType(
            @ParameterLayout(named = "Reference")
            final String typeReference,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Name")
            final String name) throws IOException, TemplateException {
        return documentTypeMenu.newType(typeReference, name);
    }

    public String default0DemoDocumentCreateDocumentType() {
        return "HELLO";
    }
    public String default1DemoDocumentCreateDocumentType() {
        return "Hello world";
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "1")
    public DocumentType demoDocumentCreateRenderingStrategy(
            @ParameterLayout(named = "Reference")
            final String typeReference,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Name")
            final String name) throws IOException, TemplateException {
        return documentTypeMenu.newType(typeReference, name);
    }

    public String default0DemoDocumentRenderingStrategy() {
        return "FREEMARKER";
    }
    public String default1DemoDocumentRenderingStrategy() {
        return "Freemarker rendering strategy";
    }

    // //////////////////////////////////////

    @Data
    public static class UserDataModel {
        private String user;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3")
    public DocumentTemplate demoDocumentCreateTemplate(
            final DocumentType documentType,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Name")
            final String name,
            @ParameterLayout(named = "Mime type")
            final String mimeType,
            final ApplicationTenancy applicationTenancy,
            @ParameterLayout(named = "Template text")
            final String templateText,
            @Parameter(maxLength = DocumentsModule.JdoColumnLength.FQCN, mustSatisfy = FullyQualifiedClassNameSpecification.class)
            @ParameterLayout(named = "Data model class name")
            final String dataModelClassName) {
        return documentTemplateMenu.newClobTemplate(
                documentType, name, mimeType, applicationTenancy, templateText, dataModelClassName);
    }
    public String disableDemoDocumentCreateTemplate() {
        return (default0DemoDocumentCreateTemplate() == null)? "No document types exist": null;
    }

    public DocumentType default0DemoDocumentCreateTemplate() {
        final List<DocumentType> documentTypes = documentTypeRepository.allTypes();
        return documentTypes.isEmpty() ? null : documentTypes.get(0);
    }

    public String default1DemoDocumentCreateTemplate() {
        return "HelloWorld.txt";
    }

    public String default2DemoDocumentCreateTemplate() {
        return "text/plain";
    }

    public List<ApplicationTenancy> choices3DemoDocumentCreateTemplate() {
        return documentTemplateMenu.choices3NewClobTemplate();
    }
    public ApplicationTenancy default3DemoDocumentCreateTemplate() {
        return choices3DemoDocumentCreateTemplate().get(0);
    }
    public String default4DemoDocumentCreateTemplate() {
        return "Hello ${user} !!!";
    }
    public String default5DemoDocumentCreateTemplate() {
        return "org.estatio.app.menus.demo.DemoDocumentMenu$UserDataModel";
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "4")
    public Document demoDocumentMerge(
            final ApplicationTenancy applicationTenancy,
            final DocumentTemplate documentTemplate,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Name")
            final String name,
            @ParameterLayout(named = "User")
            final String user) throws IOException, TemplateException {
        final String typeReference = documentTemplate.getType().getReference();

        final UserDataModel dataModel = (UserDataModel) documentTemplate.instantiateDataModel();
        dataModel.setUser(user);
        final String text = freeMarkerService.process(typeReference, applicationTenancy.getPath(), dataModel);

        final Clob clob = new Clob(name, documentTemplate.getMimeType(), text);
        final Document document = documentRepository.createClob(
                documentTemplate.getType(), applicationTenancy.getPath(), clob);
        return document;
    }
    public String disableDemoDocumentMerge() {
        return documentTemplateRepository.allTemplates().isEmpty() ? "No document templates exist": null;
    }

    public List<ApplicationTenancy> choices0DemoDocumentMerge() {
        return choices3DemoDocumentCreateTemplate();
    }
    public ApplicationTenancy default0DemoDocumentMerge() {
        return default3DemoDocumentCreateTemplate();
    }

    public List<DocumentTemplate> choices1DemoDocumentMerge(final ApplicationTenancy applicationTenancy) {
        return documentTemplateRepository.findCurrentByAtPath(applicationTenancy.getPath());
    }
    public String default2DemoDocumentMerge() {
        return "Hello Joe.txt";
    }
    public String default3DemoDocumentMerge() {
        return "Joe";
    }

    // //////////////////////////////////////

    @Inject
    private DocumentTypeMenu documentTypeMenu;

    @Inject
    private DocumentTypeRepository documentTypeRepository;

    @Inject
    private DocumentTemplateRepository documentTemplateRepository;

    @Inject
    private DocumentRepository documentRepository;

    @Inject
    private DocumentTemplateMenu documentTemplateMenu;

    @Inject
    private FreeMarkerService freeMarkerService;

}
