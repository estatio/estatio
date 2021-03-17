package org.estatio.module.coda.dom.codadocument;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.coda.dom.LineType;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaDocumentLine.class,
        objectType = "codadocument.CodaDocumentLineRepository"
)
public class CodaDocumentLineRepository {

    @Programmatic
    public List<CodaDocumentLine> listAll() {
        return repositoryService.allInstances(CodaDocumentLine.class);
    }

    @Programmatic
    public CodaDocumentLine findUnique(
            final CodaDocument document,
            final int lineNumber
    ) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        CodaDocumentLine.class,
                        "findUnique",
                        "document", document,
                        "lineNumber", lineNumber));
    }


    // minimal props required
    @Programmatic
    public CodaDocumentLine create(
            final CodaDocument document,
            final int lineNumber,
            final LineType lineType
    ){
        CodaDocumentLine line = new CodaDocumentLine();
        line.setDocument(document);
        line.setLineNumber(lineNumber);
        line.setLineType(lineType);
        repositoryService.persistAndFlush(line);
        return line;
    }

    @Inject
    RepositoryService repositoryService;

}
