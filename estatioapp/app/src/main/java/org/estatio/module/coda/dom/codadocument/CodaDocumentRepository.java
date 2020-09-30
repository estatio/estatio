package org.estatio.module.coda.dom.codadocument;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.coda.dom.CodaCurrency;
import org.estatio.module.coda.dom.CodaDocumentType;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaDocument.class,
        objectType = "codadocument.CodaDocumentRepository"
)
public class CodaDocumentRepository {

    @Programmatic
    public List<CodaDocument> listAll() {
        return repositoryService.allInstances(CodaDocument.class);
    }

    @Programmatic
    public List<CodaDocument> findByCmpCodeAndDocCodeAndDocNum(
            final String cmpCode,
            final String docCode,
            final String docNum
    ) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocument.class,
                        "findByCmpCodeAndDocCodeAndDocNum",
                        "cmpCode", cmpCode,
                        "docCode", docCode,
                        "docNum", docNum));
    }


    @Programmatic
    public CodaDocument create(
            final CodaDocumentType documentType,
            final String cmpCode,
            final String docCode,
            final String codaPeriod,
            final String atPath
    ){
        CodaDocument document = new CodaDocument();
        document.setDocumentType(documentType);
        document.setCmpCode(cmpCode);
        document.setDocCode(docCode);
        document.setCodaPeriod(codaPeriod);
        document.setAtPath(atPath);
        document.setCurrency(CodaCurrency.EUR);
        document.setCreatedAt(clockService.nowAsLocalDateTime());
        repositoryService.persistAndFlush(document);
        return document;
    }


    @Inject
    RepositoryService repositoryService;

    @Inject
    ClockService clockService;

}
