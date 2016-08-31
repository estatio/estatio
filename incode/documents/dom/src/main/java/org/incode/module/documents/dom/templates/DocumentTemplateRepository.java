/*
 *  Copyright 2016 Dan Haywood
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
package org.incode.module.documents.dom.templates;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.documents.dom.rendering.RenderingStrategy;
import org.incode.module.documents.dom.types.DocumentType;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = DocumentTemplate.class
)
public class DocumentTemplateRepository {

    @Programmatic
    public DocumentTemplate createBlob(
            final DocumentType type,
            final String atPath,
            final Blob blob,
            final String dataModelClassName,
            final RenderingStrategy renderingStrategy) {
        final DocumentTemplate document = new DocumentTemplate(type, atPath, blob, renderingStrategy, dataModelClassName);
        repositoryService.persist(document);
        return document;
    }

    @Programmatic
    public DocumentTemplate createClob(
            final DocumentType type,
            final String atPath,
            final Clob clob,
            final String dataModelClassName,
            final RenderingStrategy renderingStrategy) {
        final DocumentTemplate document = new DocumentTemplate(type, atPath, clob, renderingStrategy, dataModelClassName);
        repositoryService.persist(document);
        return document;
    }

    @Programmatic
    public DocumentTemplate createText(
            final DocumentType type,
            final String atPath,
            final String name,
            final String mimeType,
            final String text,
            final String dataModelClassName,
            final RenderingStrategy renderingStrategy) {
        final DocumentTemplate document = new DocumentTemplate(type, atPath, name, mimeType, text, renderingStrategy, dataModelClassName);
        repositoryService.persist(document);
        return document;
    }

    @Programmatic
    public List<DocumentTemplate> findByTypeAndAtPath(final DocumentType documentType, final String atPath) {
        return repositoryService.allMatches(
                new QueryDefault<>(DocumentTemplate.class,
                        "findByTypeAndAtPath",
                        "type", documentType,
                        "atPath", atPath));
    }

    @Programmatic
    public DocumentTemplate findCurrentByTypeAndAtPath(final DocumentType documentType, final String atPath) {
        final LocalDate now = clockService.now();
        return repositoryService.firstMatch(
                new QueryDefault<>(DocumentTemplate.class,
                        "findCurrentByTypeAndAtPath",
                        "type", documentType,
                        "atPath", atPath,
                        "now", now));
    }

    @Programmatic
    public List<DocumentTemplate> findCurrentByType(final DocumentType documentType) {
        final LocalDate now = clockService.now();
        return repositoryService.allMatches(
                new QueryDefault<>(DocumentTemplate.class,
                        "findCurrentByType",
                        "type", documentType,
                        "now", now));
    }

    @Programmatic
    public List<DocumentTemplate> findCurrentByAtPath(final String atPath) {
        final LocalDate now = clockService.now();
        return repositoryService.allMatches(
                new QueryDefault<>(DocumentTemplate.class,
                        "findCurrentByAtPath",
                        "atPath", atPath,
                        "now", now));
    }



    @Programmatic
    public List<DocumentTemplate> allTemplates() {
        return repositoryService.allInstances(DocumentTemplate.class);
    }

    //region > injected services

    @Inject
    RepositoryService repositoryService;
    @Inject
    ClockService clockService;

    //endregion

}
