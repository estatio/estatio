package org.estatio.module.coda.dom.codalink;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.DateTime;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Property;

import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.module.capex.app.DocumentBarcodeService;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.invoice.dom.Invoice;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        objectType = "codalink.InvoiceCodaDocLink")
@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE, schema = "dbo")
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Queries({
        @Query(
                name = "findByCmpCodeAndDocCodeAndDocNum", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.codalink.CodaDocLink "
                        + "WHERE cmpCode == :cmpCode "
                        + "   && docCode == :docCode "
                        + "   && docNum  == :docNum "),
        @Query(
                name = "findByInvoice", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.codalink.CodaDocLink "
                        + "WHERE invoice == :invoice "
                        + "ORDER BY createdAt DESC "),
})
@Unique(name = "CodaDocLink_cmpCode_docCode_docNum_UNQ", members = { "cmpCode", "docCode", "docNum" })
public class CodaDocLink {

    public CodaDocLink(){}
    public CodaDocLink(
            final String cmpCode, final String docCode, final String docNum,
            final Invoice invoice,
            final DateTime createdAt) {
        this.cmpCode = cmpCode;
        this.docCode = docCode;
        this.docNum = docNum;
        this.invoice = invoice;
        this.createdAt = createdAt;
    }

    public String title() {
        return String.format("[%s,%s,%s] -> %s",
                getCmpCode(), getDocCode(), getDocNum(),
                titleOf(invoice)
                );
    }

    @Column(allowsNull = "false", length = 12)
    @Property()
    @Getter @Setter
    private String cmpCode;

    @Column(allowsNull = "false", length = 12)
    @Property()
    @Getter @Setter
    private String docCode;

    @Column(allowsNull = "false", length = 12)
    @Property()
    @Getter @Setter
    private String docNum;

    @Column(allowsNull = "false", name = "invoiceId")
    @Property
    @Getter @Setter
    private Invoice invoice;

    /**
     * So we can determine the most recent link.
     */
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property
    private DateTime createdAt;


    // all a bit hacky, but this entity is really just for us to diagnose sync issues.
    private String titleOf(final Invoice invoice) {
        if(invoice instanceof IncomingInvoice) {
            final IncomingInvoice incomingInvoice = (IncomingInvoice) invoice;
            return barcodeFor(incomingInvoice);
        } else {
            return invoice.getInvoiceNumber();
        }
    }

    private String barcodeFor(final IncomingInvoice incomingInvoice) {
        List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(incomingInvoice);
        for (final Paperclip paperclip : paperclips) {
            DocumentAbstract document = paperclip.getDocument();
            String name = document.getName();
            if(documentBarcodeService.isBarcode(name)) {
                return documentBarcodeService.barcodeFrom(name);
            }
        }
        return null;
    }

    @Inject
    DocumentBarcodeService documentBarcodeService;

    @Inject
    PaperclipRepository paperclipRepository;


}
