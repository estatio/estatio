package org.estatio.module.coda.dom.doc;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;

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
    public CodaDocHead findByCandidate(
            final CodaDocHead codaDocHead
    ) {
        final String cmpCode = codaDocHead.getCmpCode();
        final String docCode = codaDocHead.getDocCode();
        final String docNum = codaDocHead.getDocNum();
        return findByCmpCodeAndDocCodeAndDocNum(cmpCode, docCode, docNum);
    }

    @Programmatic
    public CodaDocHead persistAsReplacementIfRequired(final CodaDocHead codaDocHead) {
        // sanity check
        if(repositoryService.isPersistent(codaDocHead)) {
            throw new IllegalStateException(
                    String.format("CodaDocHead '%s' is already persistent", titleService.titleOf(codaDocHead)));
        }

        CodaDocHead existingCodaDocHead = findByCandidate(codaDocHead);
        if (existingCodaDocHead != null) {
            delete(existingCodaDocHead);
        }
        return repositoryService.persist(codaDocHead);
    }

    private void delete(final CodaDocHead codaDocHead) {
        repositoryService.removeAndFlush(codaDocHead);
    }

    @javax.inject.Inject
    RepositoryService repositoryService;
    @javax.inject.Inject
    TitleService titleService;
}