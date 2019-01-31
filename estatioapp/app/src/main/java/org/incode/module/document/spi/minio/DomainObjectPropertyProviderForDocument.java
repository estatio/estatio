package org.incode.module.document.spi.minio;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.bookmark.BookmarkService2;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentRepository;
import org.incode.module.document.dom.impl.docs.DocumentSort;
import org.incode.module.document.dom.impl.docs.minio.Document_archived;
import org.incode.module.minio.dopserver.spi.DomainObjectProperty;
import org.incode.module.minio.dopserver.spi.DomainObjectPropertyProvider;

import static org.incode.module.minio.dopserver.spi.DomainObjectProperty.Type.BLOB;

@DomainService(nature = NatureOfService.DOMAIN)
public class DomainObjectPropertyProviderForDocument implements DomainObjectPropertyProvider {

    private int archiveAfterInWeeks;

    @PostConstruct
    public void init(Map<String,String> properties) {
        this.archiveAfterInWeeks =
                Config.read(properties, "estatio.documents.archiveAfterWeeks", Config.ARCHIVE_AFTER_IN_WEEKS_DEFAULT);
    }

    @Override
    public void findToArchive(
            final List<DomainObjectProperty> appendTo) {

        final List<Document> documents =
                documentRepository.findOldestBySortAndCreatedAtBeforeInWeeks(DocumentSort.BLOB, archiveAfterInWeeks);
        for (final Document document : documents) {
            final DomainObjectProperty dop = newDopFor(document);

            // populate the queryResultsCache since likely to be called immediately after in blobFor(...)
            final QueryResultsCache.Key cacheKey = keyFor(dop);
            queryResultsCache.put(cacheKey, document.getBlob());

            appendTo.add(dop);
        }
    }

    @Override
    public boolean supportsBlobFor(final Object domainObject, final DomainObjectProperty dop) {
        return domainObject instanceof Document && "blob".equals(dop.getProperty());
    }

    @Override
    public Blob blobFor(final Object domainObject, final DomainObjectProperty dop) {
        final Document document = (Document)domainObject;
        return queryResultsCache.execute(document::getBlob, keyFor(dop));
    }

    @Override
    public void blobArchived(
            final Object domainObject,
            final DomainObjectProperty dop,
            final String externalUrl) {

        final Document document = (Document)domainObject;
        final Document_archived mixin = mixin(document);
        if (mixin.hideAct()) {
            // ignore
            return;
        }
        mixin.act(externalUrl);
    }

    @Override
    public boolean supportsClobFor(final Object domainObject, final DomainObjectProperty dop) {
        return false;
    }

    @Override
    public Clob clobFor(final Object domainObject, final DomainObjectProperty dop) {
        throw new IllegalStateException("Not supported");
    }

    @Override
    public void clobArchived(
            final Object domainObject,
            final DomainObjectProperty dop,
            final String externalUrl) {
        throw new IllegalStateException("Not supported");
    }

    private DomainObjectProperty newDopFor(final Document document) {
        return new DomainObjectProperty(bookmarkService.bookmarkFor(document), "blob", BLOB);
    }

    private QueryResultsCache.Key keyFor(final DomainObjectProperty dop) {
        return new QueryResultsCache.Key(getClass(), "keyFor", dop);
    }

    private Document_archived mixin(final Document document) {
        return factoryService.mixin(Document_archived.class, document);
    }


    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    FactoryService factoryService;

    @Inject
    BookmarkService2 bookmarkService;

    @Inject
    DocumentRepository documentRepository;

}
