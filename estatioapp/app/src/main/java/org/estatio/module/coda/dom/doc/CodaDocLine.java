package org.estatio.module.coda.dom.doc;

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
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.ComparisonChain;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.services.title.TitleService;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "CodaDocLine"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByDocHeadAndLineNum", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE docHead == :docHead "
                        + "   && lineNum == :lineNum ")
})
@Unique(name = "CodaDocLine_docHead_lineNum_UNQ", members = { "docHead", "lineNum" })
@DomainObject(
        objectType = "coda.CodaDocLine",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class CodaDocLine implements Comparable<CodaDocLine> {

    public CodaDocLine(){}
    public CodaDocLine(
            final CodaDocHead docHead,
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

        this.docHead = docHead;
        this.lineNum = lineNum;
        this.accountCode = accountCode;
        this.description = description;
        this.docValue = docValue;
        this.docSumTax = docSumTax;
        this.valueDate = valueDate;
        this.extRef3 = extRef3;
        this.extRef5 = extRef5;
        this.elmBankAccount = elmBankAccount;
        this.userRef1 = userRef1;
        this.userStatus = userStatus;

        resetValidation();
    }

    @Programmatic
    public void resetValidation() {

        setAccountCodeValidationStatus(ValidationStatus.NOT_CHECKED);

        setSupplierPartyRefValidationStatus(ValidationStatus.NOT_CHECKED);
        setSupplierPartyRef(null);

        setSupplierBankAccountValidationStatus(ValidationStatus.NOT_CHECKED);

        setExtRefValidationStatus(ValidationStatus.NOT_CHECKED);

        setOrderValidationStatus(ValidationStatus.NOT_CHECKED);
        setOrderGlobalNumerator(null);

        setProjectValidationStatus(ValidationStatus.NOT_CHECKED);
        setProjectCode(null);

        setPropertyValidationStatus(ValidationStatus.NOT_CHECKED);
        setPropertyCode(null);

        setWorkTypeValidationStatus(ValidationStatus.NOT_CHECKED);
        setWorkType(null);

        setReasonInvalid(null);
    }

    public String title() {
        return String.format("%s | # %d", titleService.titleOf(getDocHead()), getLineNum());
    }

    @Inject
    TitleService titleService;

    @Column(allowsNull = "false", name = "docHeadId")
    @Property
    @Getter @Setter
    private CodaDocHead docHead;

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private int lineNum;

    @Column(allowsNull = "true", length = 72)
    @Property()
    @Getter @Setter
    private String accountCode;

    @Column(allowsNull = "true", length = 36)
    @Property()
    @Getter @Setter
    private String description;

    @Column(allowsNull = "true", scale = 2)
    @Property()
    @Getter @Setter
    private BigDecimal docValue;

    @Column(allowsNull = "true", scale = 2)
    @Property()
    @Getter @Setter
    private BigDecimal docSumTax;

    @Column(allowsNull = "true")
    @javax.jdo.annotations.Persistent
    @Property()
    @Getter @Setter
    private LocalDateTime valueDate;

    @Column(allowsNull = "true", length = 32)
    @Property()
    @Getter @Setter
    private String extRef3;

    @Column(allowsNull = "true", length = 32)
    @Property()
    @Getter @Setter
    private String extRef5;

    /**
     * Corresponds to barcode number
     */
    @Column(allowsNull = "true", length = 35)
    @Property()
    @Getter @Setter
    private String userRef1;

    /**
     * Encodes whether this has been paid.
     */
    @Column(allowsNull = "true")
    @Property()
    @Getter @Setter
    private Character userStatus;

    /**
     * Corresponds to IBAN
     */
    @Column(allowsNull = "true", length = 36)
    @Property()
    @Getter @Setter
    private String elmBankAccount;



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
    private ValidationStatus accountCodeValidationStatus;


    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus supplierPartyRefValidationStatus;

    /**
     * Derived from the last portion of {@link #getAccountCode()}, only populated if
     * {@link #getAccountCodeValidationStatus()} is {@link ValidationStatus#VALID valid}
     */
    @Column(allowsNull = "true", length = 72)
    @Property()
    @Getter @Setter
    private String supplierPartyRef;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus supplierBankAccountValidationStatus;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus extRefValidationStatus;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus orderValidationStatus;

    /**
     * As parsed from extRef3/extRef5, only populated if {@link #getExtRefValidationStatus()} is {@link ValidationStatus#VALID valid}.
     */
    @Column(allowsNull = "true", length = 30)
    @Property()
    @Getter @Setter
    private String orderGlobalNumerator;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus propertyValidationStatus;

    /**
     * As parsed from extRef3/extRef5, only populated if {@link #getExtRefValidationStatus()} is {@link ValidationStatus#VALID valid}.
     */
    @Column(allowsNull = "true", length = 30)
    @Property()
    @Getter @Setter
    private String propertyCode;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus projectValidationStatus;

    /**
     * As parsed from extRef3/extRef5, only populated if {@link #getExtRefValidationStatus()} is {@link ValidationStatus#VALID valid}.
     */
    @Column(allowsNull = "true", length = 30)
    @Property()
    @Getter @Setter
    private String projectCode;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus workTypeValidationStatus;

    /**
     * As parsed from extRef3/extRef5, only populated if {@link #getExtRefValidationStatus()} is {@link ValidationStatus#VALID valid}.
     */
    @Column(allowsNull = "true", length = 30)
    @Property()
    @Getter @Setter
    private String workType;


    @Override
    public int compareTo(final CodaDocLine other) {
        return ComparisonChain.start()
                .compare(getDocHead(), other.getDocHead())
                .compare(getLineNum(), other.getLineNum())
                .result();
    }

    @Override public String toString() {
        return "CodaDocLine{" +
                "docHead=" + docHead +
                ", lineNum=" + lineNum +
                ", accountCode='" + accountCode + '\'' +
                ", extRef3='" + extRef3 + '\'' +
                ", extRef5='" + extRef5 + '\'' +
                '}';
    }

}
