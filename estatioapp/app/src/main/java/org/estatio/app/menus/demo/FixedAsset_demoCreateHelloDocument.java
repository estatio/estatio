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
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.docs.DocumentAbstract;
import org.incode.module.documents.dom.links.PaperclipRepository;
import org.incode.module.documents.dom.docs.DocumentTemplate;
import org.incode.module.documents.dom.docs.DocumentTemplateRepository;
import org.incode.module.documents.dom.types.DocumentType;
import org.incode.module.documents.dom.types.DocumentTypeRepository;

import org.estatio.dom.asset.FixedAsset;
import org.estatio.fixture.documents.DocumentTemplateForHelloGlobal;

import freemarker.template.TemplateException;

@Mixin
public class FixedAsset_demoCreateHelloDocument {


    private final FixedAsset<?> fixedAsset;

    public FixedAsset_demoCreateHelloDocument(final FixedAsset<?> fixedAsset) {
        this.fixedAsset = fixedAsset;
    }


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT, restrictTo = RestrictTo.PROTOTYPING)
    public FixedAsset<?> $$(
            @Parameter(optionality = Optionality.OPTIONAL, maxLength = DocumentsModule.JdoColumnLength.NAME)
            @ParameterLayout(named = "Document role")
            final String roleName
            ) throws IOException, TemplateException {

        final DocumentType documentType = lookupDocType();
        final DocumentTemplate documentTemplate = lookupTemplate(documentType);

        final HelloDocumentTemplateUserDataModel dataModel = (HelloDocumentTemplateUserDataModel) documentTemplate.instantiateDataModel();
        dataModel.setUser(fixedAsset.getName());

        final String documentName = fixedAsset.getName() + ".txt";
        final DocumentAbstract document = documentTemplate.render(dataModel, documentName);

        paperclipRepository.attach(document, roleName, fixedAsset);

        return fixedAsset;
    }

    public String disable$$() {
        final DocumentType documentType = lookupDocType();
        if(documentType == null) {
            return "Cannot find doc type";
        }
        if(lookupTemplate(documentType) == null) {
            return "Cannot find template";
        }
        return null;
    }

    private DocumentType lookupDocType() {
        return documentTypeRepository.findByReference(DocumentTemplateForHelloGlobal.TYPE_REF);
    }

    private DocumentTemplate lookupTemplate(final DocumentType documentType) {
        final List<DocumentTemplate> existing = documentTemplateRepository
                .findByTypeAndApplicableToAtPathAndCurrent(documentType, fixedAsset.getApplicationTenancy().getPath());
        return existing.isEmpty()? null: existing.get(0);
    }


    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    DocumentTemplateRepository documentTemplateRepository;

    @Inject
    PaperclipRepository paperclipRepository;
}
