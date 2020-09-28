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

import org.estatio.module.lease.dom.amortisation.AmortisationEntry;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "AmortisationEntryCodaDocumentLink"
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
                        + "FROM org.estatio.module.coda.dom.codadocument.AmortisationEntryCodaDocumentLink "
                        + "WHERE codaDocument == :codaDocument "
                        + "   && amortisationEntry  == :amortisationEntry "),
        @Query(
                name = "findByEntry", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.codadocument.AmortisationEntryCodaDocumentLink "
                        + "WHERE amortisationEntry == :amortisationEntry "),

})
@Unique(name = "AmortisationEntryCodaDocumentLink_codaDocument_amortisationEntry_UNQ", members = { "codaDocument", "amortisationEntry" })
@DomainObject(
        objectType = "codadocument.AmortisationEntryCodaDocumentLink",
        editing = Editing.DISABLED
)
public class AmortisationEntryCodaDocumentLink {

    public AmortisationEntryCodaDocumentLink(final AmortisationEntry amortisationEntry, final CodaDocument codaDocument){
        this.amortisationEntry = amortisationEntry;
        this.codaDocument = codaDocument;
    }

    @Column(allowsNull = "false", name = "entryId")
    @Getter @Setter
    private AmortisationEntry amortisationEntry;

    @Column(allowsNull = "false", name = "documentId")
    @Getter @Setter
    private CodaDocument codaDocument;
}
