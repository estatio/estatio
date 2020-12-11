package org.estatio.module.coda.dom.codadocument;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.ComparisonChain;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;

import org.isisaddons.module.security.dom.tenancy.HasAtPath;

import org.estatio.module.coda.dom.LineSense;
import org.estatio.module.coda.dom.LineType;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "CodaDocumentLine"
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
                        + "FROM org.estatio.module.coda.dom.codadocument.CodaDocumentLine "
                        + "WHERE document == :document "
                        + "   && lineNumber == :lineNumber "),
})
@Uniques({
        @Unique(name = "CodaDocumentLine_document_lineNumber_UNQ", members = { "document", "lineNumber" }),
})
@DomainObject(
        objectType = "codadocument.CodaDocumentLine",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_CHILD
)
public class CodaDocumentLine implements Comparable<CodaDocumentLine>, HasAtPath {

    public String title() {
        return String.format("%s - %s ", getDocument().title(), getLineNumber());
    }

    @Getter @Setter
    @Column(allowsNull = "false", name = "documentId")
    private CodaDocument document;

    @Column(allowsNull = "false")
    @Getter @Setter
    private int lineNumber;

    @Column(allowsNull = "false", length = 8)
    @Getter @Setter
    private LineType lineType;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String element1;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String element2;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String element3;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String element4;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String element5;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String element6;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate valueDate;

    @Column(allowsNull = "true", scale = 2)
    @Getter @Setter
    private BigDecimal homeValue;

    @Column(allowsNull = "true", scale = 2)
    @Getter @Setter
    private BigDecimal docValue;

    @Column(allowsNull = "true")
    @Getter @Setter
    private LineSense lineSense;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String description;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String externalReference1;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String externalReference2;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String externalReference3;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String externalReference4;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String externalReference5;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String externalReference6;

    public void updateAttachedScheduleEntryIfAny(final LocalDateTime dateTime) {
        codaDocumentLinkRepository.findAmortisationEntryLinkByDocumentLine(this).forEach(l->{
            l.getAmortisationEntry().setDateReported(dateTime.toLocalDate());
            l.getAmortisationEntry().getSchedule().verifyOutstandingValue();
        });
    }

    @Override
    public int compareTo(final CodaDocumentLine o) {
        return ComparisonChain.start()
                .compare(getDocument(), o.getDocument())
                .compare(getLineNumber(), o.getLineNumber())
                .result();
    }

    @Override
    public String toString() {
        return "CodaDocumentLine{" +
                "document=" + document +
                ", lineNum=" + getLineNumber() +
                '}';
    }

    @Override
    public String getAtPath() {
        return getDocument().getAtPath();
    }

    @Inject CodaDocumentLinkRepository codaDocumentLinkRepository;
}
