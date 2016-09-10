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
package org.incode.module.documents.dom.mixins;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.title.TitleService;

import org.incode.module.documents.dom.docs.DocumentAbstract;
import org.incode.module.documents.dom.docs.DocumentTemplate;
import org.incode.module.documents.dom.docs.DocumentTemplateRepository;
import org.incode.module.documents.dom.links.PaperclipRepository;
import org.incode.module.documents.dom.types.DocumentType;
import org.incode.module.documents.dom.types.DocumentTypeRepository;

public abstract class T_createDocument<T> {

    //region > constructor
    protected final T domainObject;
    private final List<String> docTypes;

    public T_createDocument(final T domainObject, final String... docTypes) {
        this.domainObject = domainObject;
        this.docTypes = Arrays.asList(docTypes);
    }
    //endregion


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT, restrictTo = RestrictTo.PROTOTYPING)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Object $$(
            final DocumentTemplate template,
            @ParameterLayout(named = "Preview?")
            final Boolean preview
            ) throws IOException {
        final String documentName = null;
        final String roleName = null;
        final Object dataModel = newDataModel(domainObject);
        if (preview) {
            return template.preview(dataModel, null);
        }
        final String documentNameToUse = documentNameOf(domainObject, template, documentName);
        final DocumentAbstract doc = template.render(dataModel, documentNameToUse);
        paperclipRepository.attach(doc, roleName, domainObject);
        return doc;
    }

    public boolean hide$$() {
        return getDocumentTemplates().isEmpty();
    }

    public TranslatableString validate$$(
            final DocumentTemplate template,
            final Boolean preview) {
        return preview && !template.getRenderingStrategy().isPreviewsToUrl()
                ? TranslatableString.tr("This template does not support previewing")
                : null;
    }

    private String documentNameOf(
            final T domainObject,
            final DocumentTemplate template,
            final String documentName) {
        final String name =
                documentName != null
                        ? documentName
                        : titleService.titleOf(domainObject);
        return template.withFileSuffix(name);
    }

    protected abstract Object newDataModel(final T domainObject);

    private List<DocumentTemplate> getDocumentTemplates() {
        final List<DocumentTemplate> templates = Lists.newArrayList();

        for (String docType : docTypes) {
            templates.addAll(applicableTemplatesFor(docType));
        }
        return templates;
    }

    private List<DocumentTemplate> applicableTemplatesFor(
            final String docTypeRef) {
        final String atPath = atPathFor(domainObject);
        final DocumentType documentType = documentTypeRepository.findByReference(docTypeRef);
        return documentTemplateRepository.findByTypeAndApplicableToAtPath(documentType, atPath);
    }

    protected abstract String atPathFor(final T domainObject);

    //region > injected services

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    TitleService titleService;

    @javax.inject.Inject
    private DocumentTypeRepository documentTypeRepository;
    
    @javax.inject.Inject
    private DocumentTemplateRepository documentTemplateRepository;

    //endregion

}
