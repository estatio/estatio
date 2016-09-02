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

import org.incode.module.documents.dom.docs.DocumentAbstract;
import org.incode.module.documents.dom.docs.DocumentTemplate;
import org.incode.module.documents.dom.docs.DocumentTemplateRepository;

import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;

import freemarker.template.TemplateException;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Prototyping",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "999")
public class DemoDocumentMenu {


    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "4")
    public DocumentAbstract demoDocumentRender(
            final ApplicationTenancy applicationTenancy,
            final DocumentTemplate documentTemplate,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Name")
            final String name,
            @ParameterLayout(named = "User")
            final String user) throws IOException, TemplateException {

        final HelloDocumentTemplateUserDataModel dataModel = (HelloDocumentTemplateUserDataModel) documentTemplate.instantiateDataModel();
        dataModel.setUser(user);

        final DocumentAbstract document = documentTemplate.render(dataModel, name);

        return document;
    }
    public String disableDemoDocumentRender() {
        return documentTemplateRepository.allTemplates().isEmpty() ? "No document templates exist": null;
    }

    public List<ApplicationTenancy> choices0DemoDocumentRender() {
        return estatioApplicationTenancyRepository.allTenancies();
    }
    public ApplicationTenancy default0DemoDocumentRender() {
        return choices0DemoDocumentRender().get(0);
    }

    public List<DocumentTemplate> choices1DemoDocumentRender(final ApplicationTenancy applicationTenancy) {
        return documentTemplateRepository.findByApplicableToAtPathAndCurrent(applicationTenancy.getPath());
    }
    public String default2DemoDocumentRender() {
        return "Hello Joe.txt";
    }
    public String default3DemoDocumentRender() {
        return "Joe";
    }

    // //////////////////////////////////////

    @Inject
    private EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;

    @Inject
    private DocumentTemplateRepository documentTemplateRepository;



}
