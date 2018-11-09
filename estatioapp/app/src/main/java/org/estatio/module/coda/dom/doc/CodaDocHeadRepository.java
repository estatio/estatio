package org.estatio.module.coda.dom.doc;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaDocHead.class,
        objectType = "coda.CodaDocHeadRepository"
)
public class CodaDocHeadRepository {

    @Programmatic
    public java.util.List<CodaDocHead> listAll() {
        return repositoryService.allInstances(CodaDocHead.class);
    }

    @Programmatic
    public CodaDocHead findByCmpCodeAndDocCodeAndDocNum(
            final String cmpCode,
            final String docCode,
            final String docNum
    ) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        CodaDocHead.class,
                        "findByCmpCodeAndDocCodeAndDocNum",
                        "cmpCode", cmpCode,
                        "docCode", docCode,
                        "docNum", docNum));
    }

    @Programmatic
    public CodaDocHead create(
            final String cmpCode,
            final String docCode,
            final String docNum,
            final LocalDate inputDate,
            final LocalDate docDate,
            final String codaPeriod,
            final String location) {
        return repositoryService.persist(
                new CodaDocHead(cmpCode, docCode, docNum, inputDate, docDate, codaPeriod, location));
    }

    @Programmatic
    public CodaDocHead replace(
            final String cmpCode,
            final String docCode,
            final String docNum,
            final LocalDate inputDate,
            final LocalDate docDate,
            final String codaPeriod,
            final String location) {
        CodaDocHead codaDocHead = findByCmpCodeAndDocCodeAndDocNum(cmpCode, docCode, docNum);
        if (codaDocHead != null) {
            delete(codaDocHead);
        }
        codaDocHead = create(cmpCode, docCode, docNum, inputDate, docDate, codaPeriod, location);
        return codaDocHead;
    }

    private void delete(final CodaDocHead codaDocHead) {
        repositoryService.removeAndFlush(codaDocHead);
    }

    @javax.inject.Inject
    RepositoryService repositoryService;
}
