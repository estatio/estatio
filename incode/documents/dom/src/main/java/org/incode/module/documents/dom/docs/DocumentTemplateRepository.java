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
package org.incode.module.documents.dom.docs;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.i18n.TranslatableString;
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
            final LocalDate date,
            final String atPath,
            final Blob blob,
            final String dataModelClassName,
            final RenderingStrategy renderingStrategy) {
        final LocalDateTime createdAt = clockService.nowAsLocalDateTime();
        final DocumentTemplate document = new DocumentTemplate(type, date, atPath, blob, renderingStrategy,
                dataModelClassName);
        repositoryService.persist(document);
        return document;
    }

    @Programmatic
    public DocumentTemplate createClob(
            final DocumentType type,
            final LocalDate date,
            final String atPath,
            final Clob clob,
            final String dataModelClassName,
            final RenderingStrategy renderingStrategy) {
        final LocalDateTime createdAt = clockService.nowAsLocalDateTime();
        final DocumentTemplate document = new DocumentTemplate(type, date, atPath, clob, renderingStrategy, dataModelClassName);
        repositoryService.persist(document);
        return document;
    }

    @Programmatic
    public DocumentTemplate createText(
            final DocumentType type,
            final LocalDate date,
            final String atPath,
            final String name,
            final String mimeType,
            final String text,
            final String dataModelClassName,
            final RenderingStrategy renderingStrategy) {
        final LocalDateTime createdAt = clockService.nowAsLocalDateTime();
        final DocumentTemplate document = new DocumentTemplate(type, date, atPath, name, mimeType, text,
                renderingStrategy, dataModelClassName);
        repositoryService.persist(document);
        return document;
    }

    /**
     * Returns all document templates for the specified {@link DocumentType}, ordered by most specific to provided
     * application tenancy first, and then by date (desc).
     */
    @Programmatic
    public List<DocumentTemplate> findByTypeAndApplicableToAtPath(final DocumentType documentType, final String atPath) {
        return repositoryService.allMatches(
                new QueryDefault<>(DocumentTemplate.class,
                        "findByTypeAndApplicableToAtPath",
                        "type", documentType,
                        "atPath", atPath));
    }

    /**
     * Returns all document templates for the specified {@link DocumentType} and exact application tenancy, ordered by date (desc).
     */
    @Programmatic
    public List<DocumentTemplate> findByTypeAndAtPath(final DocumentType documentType, final String atPath) {
        return repositoryService.allMatches(
                new QueryDefault<>(DocumentTemplate.class,
                        "findByTypeAndAtPath",
                        "type", documentType,
                        "atPath", atPath));
    }

    /**
     * As {@link #findByTypeAndApplicableToAtPath(DocumentType, String)}, but excludes any templates in the future.  Those returned
     * are ordered by most specific application tenancy first, and then by most recent first; so the first template returned
     * is usually the one to be used.
     */
    @Programmatic
    public List<DocumentTemplate> findByTypeAndApplicableToAtPathAndCurrent(final DocumentType documentType, final String atPath) {
        final LocalDate now = clockService.now();
        return repositoryService.allMatches(
                new QueryDefault<>(DocumentTemplate.class,
                        "findByTypeAndApplicableToAtPathAndCurrent",
                        "type", documentType,
                        "atPath", atPath,
                        "now", now));
    }

    /**
     * Returns all templates for a type, ordered by application tenancy and date desc.
     */
    @Programmatic
    public List<DocumentTemplate> findByType(final DocumentType documentType) {
        return repositoryService.allMatches(
                new QueryDefault<>(DocumentTemplate.class,
                        "findByType",
                        "type", documentType));
    }

    /**
     * Returns all templates available for a particular application tenancy, ordered by most specific tenancy first and
     * then within that the most recent first.
     */
    @Programmatic
    public List<DocumentTemplate> findByApplicableToAtPathAndCurrent(final String atPath) {
        final LocalDate now = clockService.now();
        return repositoryService.allMatches(
                new QueryDefault<>(DocumentTemplate.class,
                        "findByApplicableToAtPathAndCurrent",
                        "atPath", atPath,
                        "now", now));
    }


    @Programmatic
    public void delete(final DocumentTemplate documentTemplate) {
        repositoryService.removeAndFlush(documentTemplate);
    }


    @Programmatic
    public TranslatableString validateApplicationTenancyAndDate(
            final DocumentType proposedType,
            final String proposedAtPath,
            final LocalDate proposedDate,
            final DocumentTemplate ignore) {

        final List<DocumentTemplate> existingTemplates =
                findByTypeAndAtPath(proposedType, proposedAtPath);
        for (DocumentTemplate existingTemplate : existingTemplates) {
            if(existingTemplate == ignore) {
                continue;
            }
            if(java.util.Objects.equals(existingTemplate.getDate(), proposedDate)) {
                return TranslatableString.tr("A template already exists for this date");
            }
            if (proposedDate == null && existingTemplate.getDate() != null) {
                return TranslatableString.tr(
                        "Must provide a date (there are existing templates that already have a date specified)");
            }
        }
        return null;
    }

    @Programmatic
    public TranslatableString validateSortAndRenderingStrategy(
            final DocumentSort sort,
            final RenderingStrategy renderingStrategy) {
        final DocumentNature documentNature = renderingStrategy.getDocumentNature();
        if(sort.isBytes() && documentNature == DocumentNature.CHARACTERS) {
            return TranslatableString.tr("Must provide text or Clob template with a character-based rendering strategy");
        }
        if(sort.isCharacters() && documentNature == DocumentNature.BYTES) {
            return TranslatableString.tr("Must provide Blob template with a binary-based rendering strategy");
        }
        return null;
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
