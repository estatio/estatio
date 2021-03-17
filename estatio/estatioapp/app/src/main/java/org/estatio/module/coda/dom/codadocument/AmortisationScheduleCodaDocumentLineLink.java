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
        table = "AmortisationScheduleCodaDocumentLineLink"
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
                        + "FROM org.estatio.module.coda.dom.codadocument.AmortisationScheduleCodaDocumentLineLink "
                        + "WHERE codaDocumentLine == :codaDocumentLine "
                        + "   && amortisationSchedule  == :amortisationSchedule "),
        @Query(
                name = "findByAmortisationSchedule", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.codadocument.AmortisationScheduleCodaDocumentLineLink "
                        + "WHERE amortisationSchedule  == :amortisationSchedule "),
        @Query(
                name = "findByDocumentLine", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.codadocument.AmortisationScheduleCodaDocumentLineLink "
                        + "WHERE codaDocumentLine  == :codaDocumentLine "),

})
@Unique(name = "AmortisationScheduleCodaDocumentLineLink_codaDocumentLine_amortisationSchedule_UNQ", members = { "codaDocumentLine", "amortisationSchedule" })
@DomainObject(
        objectType = "codadocument.AmortisationScheduleCodaDocumentLineLink",
        editing = Editing.DISABLED
)
public class AmortisationScheduleCodaDocumentLineLink {

    public AmortisationScheduleCodaDocumentLineLink(final AmortisationSchedule amortisationSchedule, final CodaDocumentLine codaDocumentLine){
        this.amortisationSchedule = amortisationSchedule;
        this.codaDocumentLine = codaDocumentLine;
    }

    @Column(allowsNull = "false", name = "scheduleId")
    @Getter @Setter
    private AmortisationSchedule amortisationSchedule;

    @Column(allowsNull = "false", name = "documentLineId")
    @Getter @Setter
    private CodaDocumentLine codaDocumentLine;

}
