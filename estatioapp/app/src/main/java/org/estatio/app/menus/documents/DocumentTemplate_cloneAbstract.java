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

import org.apache.isis.applib.services.i18n.TranslatableString;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.documents.dom.templates.DocumentTemplate;
import org.incode.module.documents.dom.templates.DocumentTemplateRepository;

import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;

public abstract class DocumentTemplate_cloneAbstract {

    protected final DocumentTemplate documentTemplate;

    public DocumentTemplate_cloneAbstract(final DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }


    protected String default0$$() {
        return documentTemplate.getName();
    }

    protected ApplicationTenancy default1$$() {
        final String atPath = documentTemplate.getAtPath();
        return applicationTenancyRepository.findByPath(atPath);
    }
    protected List<ApplicationTenancy> choices1$$() {
        return estatioApplicationTenancyRepository.allTenancies();
    }

    protected LocalDate default2$$() {
        return documentTemplate.getDate();
    }

    protected TranslatableString validateApplicationTenancyAndDate(
            final ApplicationTenancy proposedApplicationTenancy,
            final LocalDate proposedDate) {

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
