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
package org.estatio.app.menus.link;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import org.isisaddons.module.security.dom.tenancy.WithApplicationTenancy;
import org.isisaddons.module.stringinterpolator.dom.StringInterpolatorService.Root;

import org.incode.module.documents.dom.docs.DocumentTemplate;
import org.incode.module.documents.dom.docs.DocumentTemplateRepository;
import org.incode.module.documents.dom.types.DocumentType;
import org.incode.module.documents.dom.types.DocumentTypeRepository;

import org.estatio.dom.appsettings.EstatioSettingsService;

public abstract class T_documentTemplates<T extends WithApplicationTenancy> {

    //region > constructor
    protected final T domainObject;
    private final List<String> docTypes;

    public T_documentTemplates(final T domainObject, final String... docTypes) {
        this.domainObject = domainObject;
        this.docTypes = Arrays.asList(docTypes);
    }
    //endregion


    protected Root newRoot() {
        return new Root(domainObject){
                @SuppressWarnings("unused")
                public String getReportServerBaseUrl() {
                    return estatioSettingsService.fetchReportServerBaseUrl();
                }
            };
    }

    protected List<DocumentTemplate> getDocumentTemplates() {
        final List<DocumentTemplate> templates = Lists.newArrayList();

        // hard-coded list of candidate doc types (equivalent to class names in LinkRefDat entity, see LinkRefData fixture)
        for (String docType : docTypes) {
            append(docType, templates);
        }

        return templates;
    }

    private void append(final String docTypeRef, final List<DocumentTemplate> templates) {
        final String atPath = domainObject.getApplicationTenancy().getPath();
        final DocumentType documentType = documentTypeRepository.findByReference(docTypeRef);
        final List<DocumentTemplate> applicableTemplates =
                documentTemplateRepository.findByTypeAndApplicableToAtPath(documentType, atPath);
        for (DocumentTemplate template : applicableTemplates) {
            final boolean previewsToUrl = canAccept(template);
            if(previewsToUrl) {
                templates.add(template);
            }
        }
    }

    /**
     * Optional hook
     */
    protected boolean canAccept(final DocumentTemplate template) {
        return true;
    }

    //region > injected services

    @javax.inject.Inject
    private DocumentTypeRepository documentTypeRepository;
    
    @javax.inject.Inject
    private DocumentTemplateRepository documentTemplateRepository;

    @javax.inject.Inject
    private EstatioSettingsService estatioSettingsService;

    //endregion

}
