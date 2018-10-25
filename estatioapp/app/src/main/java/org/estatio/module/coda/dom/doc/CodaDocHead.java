package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
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

import com.google.common.collect.ComparisonChain;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        // TODO: REVIEW: EST-1862: an alternative design would be to use the cmpCode/docCode/docNum as the unique (application) key.
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "CodaDocHead"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByCmpCodeAndDocCodeAndDocNum", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocHead "
                        + "WHERE cmpCode == :cmpCode && "
                        + "      docCode == :docCode && "
                        + "      docNum  == :docNum ")
})
@Unique(name = "CodaDocHead_cmpCode_docCode_docNum_UNQ", members = { "cmpCode", "docCode", "docNum" })
@DomainObject(
        objectType = "coda.CodaDocHead",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class CodaDocHead implements Comparable<CodaDocHead> {

    public CodaDocHead(){}
    public CodaDocHead(
            final String cmpCode,
            final String docCode,
            final String docNum) {

        this.cmpCode = cmpCode;
        this.docCode = docCode;
        this.docNum = docNum;

        this.validationStatus = ValidationStatus.NOT_CHECKED;
        this.handling = Handling.INCLUDE;
        this.numberOfLines = 0;
    }

    public String title() {
        return String.format("%s | %s | %s", getCmpCode(), getDocCode(), getDocNum());
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

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private int numberOfLines;



    @Column(allowsNull = "true", name="incomingInvoiceId")
    @Property
    @Getter @Setter
    private IncomingInvoice incomingInvoice;


    @javax.jdo.annotations.Persistent(mappedBy = "docHead", defaultFetchGroup = "true")
    @CollectionLayout(defaultView = "table", paged = 999)
    @Getter @Setter
    private SortedSet<CodaDocLine> lines = new TreeSet<>();

    @Programmatic
    public CodaDocLine upsertLine(
            final int lineNum,
            final String accountCode,
            final String description,
            final BigDecimal docValue,
            final BigDecimal docSumTax,
            final LocalDateTime valueDate,
            final String extRef3,
            final String extRef5,
            final String elmBankAccount,
            final String userRef1,
            final Character userStatus) {
        return lineRepository.upsert(this,
                    lineNum, accountCode, description,
                    docValue, docSumTax, valueDate, extRef3, extRef5, elmBankAccount, userRef1, userStatus);
    }

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus validationStatus;


    public enum Handling {
        /**
         * The document should not be ignored.
         *
         * If its {@link CodaDocHead#getValidationStatus() validation status} is {@link ValidationStatus#VALID}, then
         * there will be corresponding Estatio entities for the various implicitly referenced objects (property, project,
         * order, charge and incoming invoice).
         *
         * If its {@link CodaDocHead#getValidationStatus() validation status} is {@link ValidationStatus#INVALID}, then
         * there will be NO corresponding Estatio entities and the {@link CodaDocHead document} will be brought to the
         * users' attention as an exception.
         *
         *
         */
        INCLUDE,
        /**
         * The document corresponds to an archived project (so should be excluded from processing).
         */
        EXCLUDE_PROJECT_ARCHIVED,
        /**
         * The document should be excluded from processing for some other reason.
         */
        EXCLUDE_OTHER,
        ;
    }

    /**
     * How this document should be handled (override {@link #getValidationStatus() validation status}).
     */
    @Column(allowsNull = "false", length = 30)
    @Property()
    @Getter @Setter
    private Handling handling;

    //region > compareTo, toString
    @Override
    public int compareTo(final CodaDocHead other) {
        return ComparisonChain.start()
                .compare(getCmpCode(), other.getCmpCode())
                .compare(getDocCode(), other.getDocCode())
                .compare(getDocNum(), other.getDocNum())
                .result();
    }

    @Override
    public String toString() {
        return "CodaDocHead{" +
                "companyCode='" + getCmpCode()+ '\'' +
                ", docCode='" + getDocCode() + '\'' +
                ", docNum='" + getDocNum() + '\'' +
                '}';
    }

    //endregion

    @Inject
    CodaDocLineRepository lineRepository;
}
