package org.estatio.module.coda.dom.codadocument;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

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
    public List<CodaDocument> findByDocumentTypeAndCmpCodeAndDocCodeAndDocDate(
            final CodaDocumentType documentType,
            final String cmpCode,
            final String docCode,
            final LocalDate docDate
    ) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        CodaDocument.class,
                        "findByDocumentTypeAndCmpCodeAndDocCodeAndDocDate",
                        "documentType", documentType,
                        "cmpCode", cmpCode,
                        "docCode", docCode,
                        "docDate", docDate));
    }


    @Programmatic
    public CodaDocument findOrCreateForAmortisation(
            final CodaDocumentType documentType,
            final String cmpCode,
            final String docCode,
            final String codaPeriod,
            final LocalDate docDate,
            final String atPath){
        switch (documentType){
        case RECURRING_COVID_AMORTISATION:
        case INITIAL_COVID_AMORTISATION:
            final CodaDocument firstByTypeCmpCodeDocCodeDocDateIfAny = findByDocumentTypeAndCmpCodeAndDocCodeAndDocDate(
                    documentType, cmpCode, docCode, docDate).stream().findFirst().orElse(null);
            if (firstByTypeCmpCodeDocCodeDocDateIfAny == null) {
                return create(documentType, cmpCode, docCode, codaPeriod, docDate, atPath);
            } else {
                return firstByTypeCmpCodeDocCodeDocDateIfAny;
            }
        default:
            return null;
        }

    }

    private CodaDocument create(
            final CodaDocumentType documentType,
            final String cmpCode,
            final String docCode,
            final String codaPeriod, final LocalDate docDate, final String atPath) {
        CodaDocument document = new CodaDocument();
        document.setDocumentType(documentType);
        document.setCmpCode(cmpCode);
        document.setDocCode(docCode);
        document.setCodaPeriod(codaPeriod);
        document.setDocDate(docDate);
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
