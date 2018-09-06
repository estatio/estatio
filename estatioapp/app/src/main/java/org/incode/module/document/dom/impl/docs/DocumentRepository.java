package org.incode.module.document.dom.impl.docs;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.document.dom.impl.types.DocumentType;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = DocumentAbstract.class
)
public class DocumentRepository {

    public String getId() {
        return "incodeDocuments.DocumentRepository";
    }


    @Programmatic
    public Document create(
            final DocumentType type,
            final String atPath,
            final String documentName,
            final String mimeType) {
        final DateTime createdAt = clockService.nowAsDateTime();
        final Document document = new Document(type, atPath, documentName, mimeType, createdAt);
        repositoryService.persist(document);
        return document;
    }

    @Programmatic
    public List<Document> findByTypeAndNameAndAtPath(
            final DocumentType type,
            final String atPath,
            final String name) {
        return repositoryService.allMatches(
                new QueryDefault<>(Document.class,
                        "findByTypeAndAtPathAndName",
                        "type", type,
                        "atPath", atPath,
                        "name", name
                        ));
    }

    @Programmatic
    public List<Document> findWithNoPaperclips() {
        return repositoryService.allMatches(new QueryDefault<>(Document.class, "findWithNoPaperclips"));
    }

    @Programmatic
    public List<Document> findOldestBySortAndCreatedAtBefore(final DocumentSort sort) {
        final DateTime threeMonthsAgo = clockService.nowAsDateTime().minusMonths(3);
        return repositoryService.allMatches(new QueryDefault<>(
                Document.class,
                "findOldestBySortAndCreatedAtBefore",
                "sort", sort,
                "before", threeMonthsAgo));
    }

    @Programmatic
    public List<Document> findBetween(final LocalDate startDate, final LocalDate endDateIfAny) {

        final DateTime startDateTime = startDate.toDateTimeAtStartOfDay();

        final QueryDefault<Document> query;
        if (endDateIfAny != null) {
            final DateTime endDateTime = endDateIfAny.plusDays(1).toDateTimeAtStartOfDay();
            query = new QueryDefault<>(Document.class,
                    "findByCreatedAtBetween",
                    "startDateTime", startDateTime,
                    "endDateTime", endDateTime);
        }
        else {
            query = new QueryDefault<>(Document.class,
                    "findByCreatedAtAfter",
                    "startDateTime", startDateTime);
        }

        return repositoryService.allMatches(query);
    }

    @Programmatic
    public List<DocumentAbstract> allDocuments() {
        return repositoryService.allInstances(DocumentAbstract.class);
    }


    @Programmatic
    public void delete(final Document document) {
        repositoryService.removeAndFlush(document);
    }


    @Inject
    RepositoryService repositoryService;
    @Inject
    ClockService clockService;


}
