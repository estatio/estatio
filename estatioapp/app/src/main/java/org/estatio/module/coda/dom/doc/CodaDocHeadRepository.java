package org.estatio.module.coda.dom.doc;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaDocHead.class
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
            final IncomingInvoice incomingInvoice) {
        return repositoryService.persist(
                new CodaDocHead(cmpCode, docCode, docNum, incomingInvoice));
    }

    /**
     * Similar to {@link #upsert(String, String, String, IncomingInvoice)}, but will NOT update
     * any fields if the {@link CodaDocHead} already exists.
     */
    @Programmatic
    public CodaDocHead findOrCreate(
            final String cmpCode,
            final String docCode,
            final String docNum,
            final IncomingInvoice incomingInvoice) {
        CodaDocHead codaDocHead = findByCmpCodeAndDocCodeAndDocNum(cmpCode, docCode, docNum);
        if (codaDocHead == null) {
            codaDocHead = create(cmpCode, docCode, docNum, incomingInvoice);
        }
        return codaDocHead;
    }

    /**
     * Similar to {@link #findOrCreate(String, String, String, IncomingInvoice)}, but will update
     * any non-key fields for an existing {@link CodaDocHead}.
     */
    @Programmatic
    public CodaDocHead upsert(
            final String cmpCode,
            final String docCode,
            final String docNum,
            final IncomingInvoice incomingInvoice) {
        CodaDocHead codaDocHead = findByCmpCodeAndDocCodeAndDocNum(cmpCode, docCode, docNum);
        if (codaDocHead == null) {
            codaDocHead = create(cmpCode, docCode, docNum, incomingInvoice);
        } else {
            codaDocHead.setIncomingInvoice(incomingInvoice);
        }
        return codaDocHead;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;
}
