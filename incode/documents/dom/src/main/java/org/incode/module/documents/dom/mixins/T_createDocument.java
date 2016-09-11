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
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.documents.dom.docs.DocumentAbstract;
import org.incode.module.documents.dom.docs.DocumentTemplate;
import org.incode.module.documents.dom.docs.DocumentTemplateRepository;
import org.incode.module.documents.dom.links.PaperclipRepository;
import org.incode.module.documents.dom.services.DocumentNamingService;
import org.incode.module.documents.dom.types.DocumentType;
import org.incode.module.documents.dom.types.DocumentTypeRepository;

public abstract class T_createDocument<T> {

    public static enum Intent {
        PREVIEW,
        CREATE_AND_SAVE
    }

    //region > constructor
    protected final T domainObject;
    private final List<String> docTypes;

    public T_createDocument(final T domainObject, final String... docTypes) {
        this.domainObject = domainObject;
        this.docTypes = Arrays.asList(docTypes);
    }
    //endregion


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Object $$(
            final DocumentTemplate template,
            @ParameterLayout(named = "Action")
            final Intent intent
            ) throws IOException {
        final String documentName = null;
        final String roleName = null;
        final Object dataModel = newDataModel(domainObject);
        if (intent == Intent.PREVIEW) {
            return template.preview(dataModel, null);
        }
        final String documentNameToUse = documentNameOf(documentName, domainObject, template);
        final DocumentAbstract doc = template.render(dataModel, documentNameToUse);
        paperclipRepository.attach(doc, roleName, domainObject);
        return doc;
    }

    public boolean hide$$() {
        return choices0$$().isEmpty();
    }

    public List<DocumentTemplate> choices0$$() {
        return getDocumentTemplates();
    }

    public List<Intent> choices1$$(final DocumentTemplate template) {
        final List<Intent> intents = Lists.newArrayList();
        if(template != null && template.getRenderingStrategy().isPreviewsToUrl()) {
            intents.add(Intent.PREVIEW);
        }
        intents.add(Intent.CREATE_AND_SAVE);
        return intents;
    }

    /**
     * Delegates to {@link DocumentNamingService} to allow more sophisticated rules to be plugged in (eg substitute
     * for any invalid characters).
     */
    private String documentNameOf(
            final String documentName, final T domainObject, final DocumentTemplate template) {
        return documentNamingService.nameOf(documentName, domainObject, template);
    }

    protected abstract Object newDataModel(final T domainObject);

    private List<DocumentTemplate> getDocumentTemplates() {

        final String atPath = atPathFor(domainObject);

        final List<DocumentTemplate> templates = Lists.newArrayList();
        for (String docType : docTypes) {
            final DocumentType documentType = documentTypeRepository.findByReference(docType);
            templates.addAll(documentTemplateRepository.findByTypeAndApplicableToAtPath(documentType, atPath));
        }
        return templates;
    }

    protected abstract String atPathFor(final T domainObject);

    //region > injected services

    @Inject
    PaperclipRepository paperclipRepository;

    @javax.inject.Inject
    private DocumentTypeRepository documentTypeRepository;
    
    @javax.inject.Inject
    private DocumentTemplateRepository documentTemplateRepository;

    @javax.inject.Inject
    private DocumentNamingService documentNamingService;

    //endregion

}
