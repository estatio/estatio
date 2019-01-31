package org.incode.module.document.spi.minio;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.factory.FactoryService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentRepository;
import org.incode.module.document.dom.impl.docs.minio.Document_purgeBlob;

@DomainService(nature = NatureOfService.DOMAIN)
public class DocumentBlobPurgeService {

    private int purgeAfterInWeeks;

    @PostConstruct
    public void init(Map<String,String> properties) {
        this.purgeAfterInWeeks =
                Config.read(properties, "estatio.documents.purgeAfterWeeks", Config.PURGE_AFTER_IN_WEEKS_DEFAULT);
    }

    /**
     * Intended to be called daily from Quartz or similar.
     */
    @Programmatic
    public void purge() {

        final List<Document> documents =
                documentRepository.findOldestWithPurgeableBlogsAndCreatedAtBeforeInWeeks(purgeAfterInWeeks);
        for (final Document document : documents) {
            final Document_purgeBlob mixin = factoryService.mixin(Document_purgeBlob.class, document);
            if(!mixin.hideAct()) {
                mixin.act();
            }
        }
    }


    @Inject
    DocumentRepository documentRepository;
    @Inject
    FactoryService factoryService;

}
