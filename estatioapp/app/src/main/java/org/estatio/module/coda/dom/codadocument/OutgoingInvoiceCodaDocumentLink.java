package org.estatio.module.coda.dom.codadocument;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;

import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "OutgoingInvoiceCodaDocumentLink"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.codadocument.OutgoingInvoiceCodaDocumentLink "
                        + "WHERE codaDocument == :codaDocument "
                        + "   && invoice  == :invoice "),
        @Query(
                name = "findByInvoice", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.codadocument.OutgoingInvoiceCodaDocumentLink "
                        + "WHERE invoice == :invoice "),

})
@Unique(name = "OutgoingInvoiceCodaDocumentLink_codaDocument_invoice_UNQ", members = { "codaDocument", "invoice" })
@DomainObject(
        objectType = "codadocument.OutgoingInvoiceCodaDocumentLink",
        editing = Editing.DISABLED
)
public class OutgoingInvoiceCodaDocumentLink {

    public OutgoingInvoiceCodaDocumentLink(final InvoiceForLease invoice, final CodaDocument codaDocument){
        this.invoice = invoice;
        this.codaDocument = codaDocument;
    }

    @Column(allowsNull = "false", name = "invoiceId")
    @Getter @Setter
    private InvoiceForLease invoice;

    @Column(allowsNull = "false", name = "documentId")
    @Getter @Setter
    private CodaDocument codaDocument;


}
