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
import java.util.Map;

import javax.inject.Inject;

import com.google.common.collect.ImmutableMap;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Clob;

import org.isisaddons.module.freemarker.dom.service.FreeMarkerService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.doctemplates.dom.DocTemplate;

import org.estatio.app.menus.doctemplates.DocTemplateMenu;

import freemarker.template.TemplateException;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Prototyping",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "999")
public class FreemarkerDemoMenu {


    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "1")
    public DocTemplate FreeMarkerDemoCreate(
            @ParameterLayout(named = "Doc Template reference")
            final String templateReference,
            final ApplicationTenancy applicationTenancy,
            @ParameterLayout(named = "Template text")
            final String templateText) throws IOException, TemplateException {
        return docTemplateMenu.newTemplate(templateReference, applicationTenancy, templateText);
    }

    public String default0FreeMarkerDemoCreate() {
        return "ABC";
    }
    public List<ApplicationTenancy> choices1FreeMarkerDemoCreate() {
        return docTemplateMenu.choices1NewTemplate();
    }
    public ApplicationTenancy default1FreeMarkerDemoCreate() {
        return choices1FreeMarkerDemoCreate().get(0);
    }
    public String default2FreeMarkerDemoCreate() {
        return "Hello ${user} !!!";
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public Clob freeMarkerDemoMerge(
            @ParameterLayout(named = "Doc Template reference")
            final String templateReference,
            final ApplicationTenancy applicationTenancy,
            @ParameterLayout(named = "User")
            final String user) throws IOException, TemplateException {
        final Map<String, String> properties = ImmutableMap.of("user", user);
        final String text = freeMarkerService.process(templateReference, applicationTenancy.getPath(), properties);
        return new Clob(templateReference + ".txt", "text/plain", text);
    }

    public String default0FreeMarkerDemoMerge() {
        return default0FreeMarkerDemoCreate();
    }
    public List<ApplicationTenancy> choices1FreeMarkerDemoMerge() {
        return choices1FreeMarkerDemoCreate();
    }
    public ApplicationTenancy default1FreeMarkerDemoMerge() {
        return default1FreeMarkerDemoCreate();
    }
    public String default2FreeMarkerDemoMerge() {
        return "Joe";
    }

    // //////////////////////////////////////

    @Inject
    private DocTemplateMenu docTemplateMenu;

    @Inject
    private FreeMarkerService freeMarkerService;

}
