package org.incode.module.document.dom.impl.types;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = DocumentType.class
)
public class DocumentTypeRepository {

    public String getId() {
        return "incodeDocuments.DocumentTypeRepository";
    }

    //region > create 
    @Programmatic
    public DocumentType create(
            final String reference,
            final String name) {
        final DocumentType documentType = new DocumentType(reference, name);
        repositoryService.persistAndFlush(documentType);
        return documentType;
    }
    //endregion

    //region > findByReference
    @Programmatic
    public DocumentType findByReference(
            final String reference) {
        return queryResultsCache.execute(
                () -> repositoryService.firstMatch(
                    new QueryDefault<>(DocumentType.class,
                            "findByReference",
                            "reference", reference)),
                DocumentTypeRepository.class,
                "findByReference", reference);

    }
    //endregion

    //region > allTypes
    @Programmatic
    public List<DocumentType> allTypes() {
        return repositoryService.allInstances(DocumentType.class);
    }
    //endregion


    //region > injected services
    @Inject
    RepositoryService repositoryService;
    @Inject
    QueryResultsCache queryResultsCache;
    //endregion

}
