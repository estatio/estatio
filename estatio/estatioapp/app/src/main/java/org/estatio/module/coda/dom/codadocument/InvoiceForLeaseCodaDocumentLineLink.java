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
        table = "InvoiceForLeaseCodaDocumentLineLink"
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
                        + "FROM org.estatio.module.coda.dom.codadocument.InvoiceForLeaseCodaDocumentLineLink "
                        + "WHERE codaDocumentLine == :codaDocumentLine "
                        + "   && invoice  == :invoice "),
        @Query(
                name = "findByInvoice", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.codadocument.InvoiceForLeaseCodaDocumentLineLink "
                        + "WHERE invoice == :invoice "),
        @Query(
                name = "findByDocumentLine", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.codadocument.InvoiceForLeaseCodaDocumentLineLink "
                        + "WHERE codaDocumentLine == :codaDocumentLine "),

})
@Unique(name = "InvoiceForLeaseCodaDocumentLineLink_codaDocumentLine_invoice_UNQ", members = { "codaDocumentLine", "invoice" })
@DomainObject(
        objectType = "codadocument.InvoiceForLeaseCodaDocumentLineLink",
        editing = Editing.DISABLED
)
public class InvoiceForLeaseCodaDocumentLineLink {

    public InvoiceForLeaseCodaDocumentLineLink(final InvoiceForLease invoice, final CodaDocumentLine codaDocumentLine){
        this.invoice = invoice;
        this.codaDocumentLine = codaDocumentLine;
    }

    @Column(allowsNull = "false", name = "invoiceId")
    @Getter @Setter
    private InvoiceForLease invoice;

    @Column(allowsNull = "false", name = "documentLineId")
    @Getter @Setter
    private CodaDocumentLine codaDocumentLine;


}
