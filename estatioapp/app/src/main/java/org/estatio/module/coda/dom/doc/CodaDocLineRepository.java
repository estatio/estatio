package org.estatio.module.coda.dom.doc;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaDocLine.class
)
public class CodaDocLineRepository {

    @Programmatic
    public java.util.List<CodaDocLine> listAll() {
        return repositoryService.allInstances(CodaDocLine.class);
    }

    @Programmatic
    public CodaDocLine findByDocHeadAndLineNum(
            final CodaDocHead docHead,
            final int lineNum
    ) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        CodaDocLine.class,
                        "findByDocHeadAndLineNum",
                        "docHead", docHead,
                        "lineNum", lineNum));
    }

    @Programmatic
    public CodaDocLine create(
            final CodaDocHead docHead,
            final int lineNum,
            final String extRef3,
            final String extRef5) {
        return repositoryService.persist(
                new CodaDocLine(docHead, lineNum, extRef3, extRef5));
    }

    @Programmatic
    public CodaDocHead delete(
            final CodaDocHead docHead,
            final int lineNum) {
        final CodaDocLine docLine = findByDocHeadAndLineNum(docHead, lineNum);
        if(docLine != null) {
            repositoryService.removeAndFlush(docLine);
        }
        return docHead;
    }

    @Programmatic
    public CodaDocHead upsert(
            final CodaDocHead docHead,
            final int lineNum,
            final String extRef3,
            final String extRef5) {

        final CodaDocLine docLine = findByDocHeadAndLineNum(docHead, lineNum);
        if(docLine == null) {
            create(docHead, lineNum, extRef3, extRef5);
        } else {
            docLine.setExtRef3(extRef3);
            docLine.setExtRef5(extRef5);
        }
        return docHead;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;

}
