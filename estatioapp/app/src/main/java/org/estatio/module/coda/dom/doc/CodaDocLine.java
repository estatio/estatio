package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;

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
import org.apache.isis.applib.annotation.Where;

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
                        + "WHERE docLine == :docLine ")
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
            final String supplierPartyRef,
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
        this.supplierPartyRef = supplierPartyRef;
        this.description = description;
        this.docValue = docValue;
        this.docSumTax = docSumTax;
        this.valueDate = valueDate;
        this.extRef3 = extRef3;
        this.extRef5 = extRef5;
        this.elmBankAccount = elmBankAccount;
        this.userRef1 = userRef1;
        this.userStatus = userStatus;

        this.supplierBankAccountValidationStatus = ValidationStatus.INVALID;
        this.extRefValidationStatus = ValidationStatus.INVALID;
        this.orderValidationStatus = ValidationStatus.INVALID;
        this.projectValidationStatus = ValidationStatus.INVALID;
        this.propertyValidationStatus = ValidationStatus.INVALID;
        this.workTypeValidationStatus = ValidationStatus.INVALID;
    }

    @Column(allowsNull = "false", name = "docHeadId")
    @Property(hidden = Where.PARENTED_TABLES)
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

    /**
     * Derived from the last portion of {@link #getAccountCode()}.
     */
    @Column(allowsNull = "true", length = 72)
    @Property()
    @Getter @Setter
    private String supplierPartyRef;

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
    private ValidationStatus extRefValidationStatus;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus propertyValidationStatus;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus supplierBankAccountValidationStatus;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus orderValidationStatus;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus projectValidationStatus;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus workTypeValidationStatus;


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
                ", extRef3='" + extRef3 + '\'' +
                ", extRef5='" + extRef5 + '\'' +
                ", extRef5='" + extRef5 + '\'' +
                '}';
    }

}
