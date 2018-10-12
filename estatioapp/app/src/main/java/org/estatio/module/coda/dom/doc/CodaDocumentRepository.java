package org.estatio.module.coda.dom.doc;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.value.TimeStamp;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CodaDocument.class
)
public class CodaDocumentRepository {

    @Programmatic
    public java.util.List<CodaDocument> listAll() {
        return repositoryService.allInstances(CodaDocument.class);
    }

    @Programmatic
    public CodaDocument findByCompanyCodeAndDocCodeAndDocNum(
            final String companyCode,
            final String docCode,
            final String docNum
    ) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        CodaDocument.class,
                        "findByCompanyCodeAndDocCodeAndDocNum",
                        "companyCode", companyCode,
                        "docCode", docCode,
                        "docNum", docNum));
    }

    @Programmatic
    public TimeStamp findHighWaterMark() {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        TimeStamp.class,
                        "findHighWaterMark"));
    }

    @Programmatic
    public CodaDocument createDocument(
            final String companyCode,
            final String docCode,
            final String docNum,
            final TimeStamp modifyDate,
            final String extRef3,
            final String extRef4,
            final String extRef5,
            final IncomingInvoice incomingInvoice) {
        final CodaDocument codaDocument = new CodaDocument();
        serviceRegistry2.injectServicesInto(codaDocument);
        codaDocument.setCompanyCode(companyCode);
        codaDocument.setDocCode(docCode);
        codaDocument.setDocNum(docNum);
        codaDocument.setModifyDate(modifyDate);
        codaDocument.setExtRef3(extRef3);
        codaDocument.setExtRef4(extRef4);
        codaDocument.setExtRef5(extRef5);
        codaDocument.setIncomingInvoice(incomingInvoice);
        repositoryService.persist(codaDocument);
        return codaDocument;
    }

    @Programmatic
    public CodaDocument upsertDocument(
            final String companyCode,
            final String docCode,
            final String docNum,
            final TimeStamp modifyDate,
            final String extRef3,
            final String extRef4,
            final String extRef5,
            final IncomingInvoice incomingInvoice) {
        CodaDocument codaDocument = findByCompanyCodeAndDocCodeAndDocNum(companyCode, docCode, docNum);
        if (codaDocument == null) {
            codaDocument = createDocument(companyCode, docCode, docNum, modifyDate, extRef3, extRef4, extRef5, incomingInvoice);
        } else {
            codaDocument.setExtRef3(extRef3);
            codaDocument.setExtRef4(extRef4);
            codaDocument.setExtRef5(extRef5);
            codaDocument.setModifyDate(modifyDate);
        }
        return codaDocument;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;

    @javax.inject.Inject
    ServiceRegistry2 serviceRegistry2;
}
