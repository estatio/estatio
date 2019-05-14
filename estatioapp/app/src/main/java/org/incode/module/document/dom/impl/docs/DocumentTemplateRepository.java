package org.incode.module.document.dom.impl.docs;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.google.common.collect.Sets;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.impl.rendering.RenderingStrategy;
import org.incode.module.document.dom.impl.types.DocumentType;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = DocumentTemplate.class
)
public class DocumentTemplateRepository {

    public String getId() {
        return "incodeDocuments.DocumentTemplateRepository";
    }

    //region > createBlob, createClob, createText
    @Programmatic
    public DocumentTemplate createBlob(
            final DocumentType type,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final Blob blob,
            final String subjectText) {
        final DocumentTemplate document =
                new DocumentTemplate(
                        type, date, atPath,
                        fileSuffix, previewOnly, blob,
                        subjectText);
        repositoryService.persistAndFlush(document);
        return document;
    }

    @Programmatic
    public DocumentTemplate createClob(
            final DocumentType type,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final Clob clob,
            final String subjectText) {
        final DocumentTemplate document =
                new DocumentTemplate(
                        type, date, atPath,
                        fileSuffix, previewOnly, clob,
                        subjectText);
        repositoryService.persistAndFlush(document);
        return document;
    }

    @Programmatic
    public DocumentTemplate createText(
            final DocumentType type,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final String name,
            final String mimeType,
            final String text,
            final String subjectText) {
        final DocumentTemplate document =
                new DocumentTemplate(
                        type, date, atPath,
                        fileSuffix, previewOnly,
                        name, mimeType, text,
                        subjectText);
        repositoryService.persistAndFlush(document);
        return document;
    }
    //endregion

    //region > delete
    @Programmatic
    public void delete(final DocumentTemplate documentTemplate) {
        repositoryService.removeAndFlush(documentTemplate);
    }
    //endregion

    //region > findBy...

    /**
     * Returns all document templates for the specified {@link DocumentType}, ordered by type, then most specific to
     * provided application tenancy, and then by date (desc).
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
     * Returns all document templates for the specified {@link DocumentType}, ordered by type, then most specific to
     * provided application tenancy, and then by date (desc).
     */
    @Programmatic
    public DocumentTemplate findFirstByTypeAndApplicableToAtPath(final DocumentType documentType, final String atPath) {
        final List<DocumentTemplate> templates = findByTypeAndApplicableToAtPath(documentType, atPath);
        return templates.isEmpty() ? null : templates.get(0);
    }

    /**
     * Returns all document templates, ordered by most specific to provided application tenancy first, and then by date (desc).
     */
    public List<DocumentTemplate> findByApplicableToAtPath(final String atPath) {
        final List<DocumentTemplate> templates = repositoryService.allMatches(
                new QueryDefault<>(DocumentTemplate.class,
                        "findByApplicableToAtPath",
                        "atPath", atPath));
        removeTemplatesWithSameDocumentType(templates);
        return templates;
    }

    protected void removeTemplatesWithSameDocumentType(final List<DocumentTemplate> templates) {
        final Set<DocumentType> documentTypes = Sets.newHashSet();
        for (Iterator<DocumentTemplate> iterator = templates.iterator(); iterator.hasNext(); ) {
            final DocumentTemplate template = iterator.next();
            final DocumentType documentType = template.getType();
            if(documentTypes.contains(documentType)) {
                iterator.remove();
            } else {
                documentTypes.add(documentType);
            }
        }
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
     * Returns the document template, if any for the specified {@link DocumentType} and exact application tenancy, and exact date.
     */
    @Programmatic
    public DocumentTemplate findByTypeAndAtPathAndDate(final DocumentType documentType, final String atPath, final LocalDate date) {
        return repositoryService.firstMatch(
                new QueryDefault<>(DocumentTemplate.class,
                        "findByTypeAndAtPathAndDate",
                        "type", documentType,
                        "atPath", atPath,
                        "date", date));
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
    //endregion

    //region > allTemplates
    @Programmatic
    public List<DocumentTemplate> allTemplates() {
        return repositoryService.allInstances(DocumentTemplate.class);
    }
    //endregion

    //region > validate...
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
    public TranslatableString validateSortAndRenderingStrategyInputNature(
            final DocumentSort sort,
            final RenderingStrategy renderingStrategy) {
        final DocumentNature documentNature = renderingStrategy.getInputNature();
        if(sort.isBytes() && documentNature == DocumentNature.CHARACTERS) {
            return TranslatableString.tr("Must provide text or Clob template with a character-based rendering strategy");
        }
        if(sort.isCharacters() && documentNature == DocumentNature.BYTES) {
            return TranslatableString.tr("Must provide Blob template with a binary-based rendering strategy");
        }
        return null;
    }
    //endregion


    //region > injected services

    @Inject
    RepositoryService repositoryService;
    @Inject
    ClockService clockService;

    //endregion

}
