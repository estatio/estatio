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

import org.estatio.module.lease.dom.amortisation.AmortisationSchedule;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "AmortisationScheduleCodaDocumentLink"
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
                        + "FROM org.estatio.module.coda.dom.codadocument.AmortisationScheduleCodaDocumentLink "
                        + "WHERE codaDocument == :codaDocument "
                        + "   && amortisationSchedule  == :amortisationSchedule "),

})
@Unique(name = "AmortisationScheduleCodaDocumentLink_codaDocument_amortisationSchedule_UNQ", members = { "codaDocument", "amortisationSchedule" })
@DomainObject(
        objectType = "codadocument.AmortisationScheduleCodaDocumentLink",
        editing = Editing.DISABLED
)
public class AmortisationScheduleCodaDocumentLink {

    public AmortisationScheduleCodaDocumentLink(final AmortisationSchedule amortisationSchedule, final CodaDocument codaDocument){
        this.amortisationSchedule = amortisationSchedule;
        this.codaDocument = codaDocument;
    }

    @Column(allowsNull = "false", name = "scheduleId")
    @Getter @Setter
    private AmortisationSchedule amortisationSchedule;

    @Column(allowsNull = "false", name = "documentId")
    @Getter @Setter
    private CodaDocument codaDocument;

}
