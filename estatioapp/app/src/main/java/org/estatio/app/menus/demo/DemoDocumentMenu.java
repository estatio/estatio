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

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.docs.Document;
import org.incode.module.documents.dom.rendering.RenderingStrategy;
import org.incode.module.documents.dom.rendering.RenderingStrategyRepository;
import org.incode.module.documents.dom.templates.DocumentTemplate;
import org.incode.module.documents.dom.templates.DocumentTemplateRepository;
import org.incode.module.documents.dom.types.DocumentType;
import org.incode.module.documents.dom.types.DocumentTypeRepository;
import org.incode.module.documents.dom.valuetypes.FullyQualifiedClassNameSpecification;
import org.incode.module.documents.dom.valuetypes.RendererClassNameSpecification;

import org.estatio.app.integration.documents.RendererForFreemarker;
import org.estatio.app.menus.documents.DocumentTemplateMenu;
import org.estatio.app.menus.documents.DocumentTypeMenu;
import org.estatio.app.menus.documents.RenderingStrategyMenu;

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
    public RenderingStrategy demoDocumentCreateRenderingStrategy(
            @ParameterLayout(named = "Reference")
            final String typeReference,
            @ParameterLayout(named = "Name")
            final String name,
            @Parameter(mustSatisfy = RendererClassNameSpecification.class)
            @ParameterLayout(named = "Renderer class name")
            final String rendererClassName) throws IOException, TemplateException {
        return renderingStrategyMenu.newStrategy(typeReference, name, rendererClassName);
    }

    public String default0DemoDocumentCreateRenderingStrategy() {
        return "FREEMARKER";
    }
    public String default1DemoDocumentCreateRenderingStrategy() {
        return "Freemarker rendering strategy";
    }
    public String default2DemoDocumentCreateRenderingStrategy() {
        return RendererForFreemarker.class.getName();
    }

    // //////////////////////////////////////

    @Data
    public static class UserDataModel {
        private String user;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "3")
    public DocumentTemplate demoDocumentCreateTextTemplate(
            final DocumentType documentType,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Name")
            final String name,
            @ParameterLayout(named = "Mime type")
            final String mimeType,
            final ApplicationTenancy applicationTenancy,
            @ParameterLayout(named = "Template text", multiLine = DocumentsModule.Constants.CLOB_MULTILINE)
            final String templateText,
            @Parameter(maxLength = DocumentsModule.JdoColumnLength.FQCN, mustSatisfy = FullyQualifiedClassNameSpecification.class)
            @ParameterLayout(named = "Data model class name")
            final String dataModelClassName,
            final RenderingStrategy renderingStrategy) {
        return documentTemplateMenu.newTextTemplate(
                documentType, name, mimeType, applicationTenancy, templateText, dataModelClassName, renderingStrategy);
    }
    public String disableDemoDocumentCreateTextTemplate() {
        if (default0DemoDocumentCreateTextTemplate() == null) {
            return "No document types exist";
        }
        if (default6DemoDocumentCreateTextTemplate() == null) {
            return "No rendering strategies exist";
        }
        return null;
    }

    public DocumentType default0DemoDocumentCreateTextTemplate() {
        final List<DocumentType> documentTypes = documentTypeRepository.allTypes();
        return documentTypes.isEmpty() ? null : documentTypes.get(0);
    }

    public String default1DemoDocumentCreateTextTemplate() {
        return "HelloWorld.txt";
    }

    public String default2DemoDocumentCreateTextTemplate() {
        return "text/plain";
    }

    public List<ApplicationTenancy> choices3DemoDocumentCreateTextTemplate() {
        return documentTemplateMenu.choices3NewTextTemplate();
    }
    public ApplicationTenancy default3DemoDocumentCreateTextTemplate() {
        return choices3DemoDocumentCreateTextTemplate().get(0);
    }
    public String default4DemoDocumentCreateTextTemplate() {
        return "Hello ${user} !!!";
    }
    public String default5DemoDocumentCreateTextTemplate() {
        return UserDataModel.class.getName();
    }

    public RenderingStrategy default6DemoDocumentCreateTextTemplate() {
        final List<RenderingStrategy> renderingStrategies = renderingStrategyRepository.allStrategies();
        return renderingStrategies.isEmpty() ? null : renderingStrategies.get(0);
    }



    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "4")
    public Document demoDocumentRender(
            final ApplicationTenancy applicationTenancy,
            final DocumentTemplate documentTemplate,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Name")
            final String name,
            @ParameterLayout(named = "User")
            final String user) throws IOException, TemplateException {

        final UserDataModel dataModel = (UserDataModel) documentTemplate.instantiateDataModel();
        dataModel.setUser(user);

        final Document document = documentTemplate.render(dataModel, name);

        return document;
    }
    public String disableDemoDocumentRender() {
        return documentTemplateRepository.allTemplates().isEmpty() ? "No document templates exist": null;
    }

    public List<ApplicationTenancy> choices0DemoDocumentRender() {
        return choices3DemoDocumentCreateTextTemplate();
    }
    public ApplicationTenancy default0DemoDocumentRender() {
        return default3DemoDocumentCreateTextTemplate();
    }

    public List<DocumentTemplate> choices1DemoDocumentRender(final ApplicationTenancy applicationTenancy) {
        return documentTemplateRepository.findCurrentByAtPath(applicationTenancy.getPath());
    }
    public String default2DemoDocumentRender() {
        return "Hello Joe.txt";
    }
    public String default3DemoDocumentRender() {
        return "Joe";
    }

    // //////////////////////////////////////

    @Inject
    private DocumentTypeMenu documentTypeMenu;

    @Inject
    private RenderingStrategyMenu renderingStrategyMenu;

    @Inject
    private DocumentTemplateMenu documentTemplateMenu;


    @Inject
    private DocumentTypeRepository documentTypeRepository;

    @Inject
    private RenderingStrategyRepository renderingStrategyRepository;

    @Inject
    private DocumentTemplateRepository documentTemplateRepository;


}
