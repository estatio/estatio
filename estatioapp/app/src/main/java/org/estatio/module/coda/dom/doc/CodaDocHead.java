package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;
import java.util.Optional;
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
import com.google.common.collect.Lists;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.party.dom.Organisation;

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

        this.numberOfLines = 0;

        //
        // and set all the 'derived' stuff to its initial value
        //

        this.handling = Handling.ATTENTION;

        this.lineValidationStatus = ValidationStatus.NOT_CHECKED;

        this.cmpCodeValidationStatus = ValidationStatus.NOT_CHECKED;
        this.cmpCodeBuyer = null;

        summaryLineIsPresentValidationStatus = ValidationStatus.NOT_CHECKED;
        analysisLineIsPresentValidationStatus = ValidationStatus.NOT_CHECKED;

        this.reasonInvalid = null;
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



    @Column(allowsNull = "true", length = 4000)
    @Property()
    @PropertyLayout(multiLine = 5)
    @Getter @Setter
    private String reasonInvalid;

    @Programmatic
    public void appendInvalidReason(final String reasonFormat, Object... args) {
        final String reason = String.format(reasonFormat, args);
        String reasonInvalid = getReasonInvalid();
        if(reasonInvalid != null) {
            reasonInvalid += "\n";
        } else {
            reasonInvalid = "";
        }
        reasonInvalid += reason;
        setReasonInvalid(reasonInvalid);
    }

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus cmpCodeValidationStatus;

    @Column(allowsNull = "true", name="cmpCodeBuyerId")
    @Property()
    @Getter @Setter
    private Organisation cmpCodeBuyer;



    @Column(allowsNull = "true", name="incomingInvoiceId")
    @Property
    @Getter @Setter
    private IncomingInvoice incomingInvoice;

    /**
     * Cascade delete of all {@link CodaDocLine}s if the parent {@link CodaDocHead} is deleted.
     */
    @javax.jdo.annotations.Persistent(mappedBy = "docHead", defaultFetchGroup = "true", dependent = "true")
    @CollectionLayout(defaultView = "table", paged = 999)
    @Getter @Setter
    private SortedSet<CodaDocLine> lines = new TreeSet<>();

    @Programmatic
    public CodaDocLine summaryLine() {
        return findFirstLineByType(LineType.SUMMARY);
    }

    @Programmatic
    public CodaDocLine analysisLine() {
        return findFirstLineByType(LineType.ANALYSIS);
    }

    CodaDocLine findFirstLineByType(final LineType lineType) {
        return Lists.newArrayList(getLines()).stream()
                .filter(x -> x.getLineType() == lineType)
                .findFirst()
                .orElse(null);
    }

    @Programmatic
    public CodaDocLine upsertLine(
            final int lineNum,
            final LineType lineType,
            final String accountCode,
            final String description,
            final BigDecimal docValue,
            final BigDecimal docSumTax,
            final LocalDateTime valueDate,
            final String extRef2,
            final String extRef3,
            final String extRef4,
            final String extRef5,
            final String elmBankAccount,
            final String userRef1,
            final Character userStatus,
            final String mediaCode) {
        return lineRepository.upsert(this,
                    lineNum, lineType, accountCode, description,
                    docValue, docSumTax, valueDate,
                    extRef2, extRef3, extRef4, extRef5,
                    elmBankAccount, userRef1, userStatus, mediaCode);
    }

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus lineValidationStatus;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus summaryLineIsPresentValidationStatus;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus analysisLineIsPresentValidationStatus;

    /**
     * How this document should be handled (override {@link #getLineValidationStatus() validation status}).
     */
    @Column(allowsNull = "false", length = 30)
    @Property()
    @Getter @Setter
    private Handling handling;

    @Programmatic
    public void handleAs(Handling handling) {
        this.setHandling(handling);
        Lists.newArrayList(getLines()).forEach(codaDocLine -> codaDocLine.setHandling(handling));
    }

    /**
     * Indicates the location of this document.
     */
    @Column(allowsNull = "true", length = 12)
    @Property()
    @Getter @Setter
    private Location location;


    @Programmatic
    public void validateLines() {
        final Optional<CodaDocLine> invalidLines = Lists.newArrayList(getLines()).stream()
                .filter(codaDocLine -> codaDocLine.getReasonInvalid() != null)
                .findAny();

        if (!invalidLines.isPresent()) {
            setLineValidationStatus(ValidationStatus.VALID);
        } else {
            setLineValidationStatus(ValidationStatus.INVALID);
            appendInvalidReason("Lines are invalid");
        }
    }

    @Programmatic
    public boolean isValid() {
        return getCmpCodeValidationStatus() == ValidationStatus.VALID &&
               getLineValidationStatus() == ValidationStatus.VALID;
    }

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
