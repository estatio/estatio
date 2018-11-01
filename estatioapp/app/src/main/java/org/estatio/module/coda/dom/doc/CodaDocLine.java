package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
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
                        + "   && lineNum == :lineNum "),

        @Query(
                name = "findByHandlingAndAccountCodeValidationStatus", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling                    == :handling "
                        + "   && accountCodeValidationStatus == :accountCodeValidationStatus "
        ),
        @Query(
                name = "findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl3ValidationStatus", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling                       == :handling "
                        + "   && accountCodeValidationStatus    == :accountCodeValidationStatus "
                        + "   && accountCodeEl3ValidationStatus == :accountCodeEl3ValidationStatus "
        ),
        @Query(
                name = "findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl3ValidationStatusAndAccountCodeEl3", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling                       == :handling "
                        + "   && accountCodeValidationStatus    == :accountCodeValidationStatus "
                        + "   && accountCodeEl3ValidationStatus == :accountCodeEl3ValidationStatus "
                        + "   && accountCodeEl3                 == :accountCodeEl3"
        ),
        @Query(
                name = "findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl5ValidationStatus", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling                       == :handling "
                        + "   && accountCodeValidationStatus    == :accountCodeValidationStatus "
                        + "   && accountCodeEl5ValidationStatus == :accountCodeEl5ValidationStatus "
        ),
        @Query(
                name = "findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl5ValidationStatusAndAccountCodeEl5", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling                       == :handling "
                        + "   && accountCodeValidationStatus    == :accountCodeValidationStatus "
                        + "   && accountCodeEl5ValidationStatus == :accountCodeEl5ValidationStatus "
                        + "   && accountCodeEl5                 == :accountCodeEl5"
        ),
        @Query(
                name = "findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl6ValidationStatus", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling                       == :handling "
                        + "   && accountCodeValidationStatus    == :accountCodeValidationStatus "
                        + "   && accountCodeEl6ValidationStatus == :accountCodeEl6ValidationStatus "
        ),
        @Query(
                name = "findByHandlingAndAccountCodeValidationStatusAndAccountCodeEl6ValidationStatusAndAccountCodeEl6", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling                       == :handling "
                        + "   && accountCodeValidationStatus    == :accountCodeValidationStatus "
                        + "   && accountCodeEl6ValidationStatus == :accountCodeEl6ValidationStatus "
                        + "   && accountCodeEl6                 == :accountCodeEl6"
        ),
        @Query(
                name = "findByHandlingAndAccountCodeValidationStatusAndSupplierBankAccountValidationStatus", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling                            == :handling "
                        + "   && accountCodeValidationStatus         == :accountCodeValidationStatus "
                        + "   && supplierBankAccountValidationStatus == :supplierBankAccountValidationStatus "
        ),
        @Query(
                name = "findByHandlingAndAccountCodeValidationStatusAndSupplierBankAccountValidationStatusAndElmBankAccount", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling                            == :handling "
                        + "   && accountCodeValidationStatus         == :accountCodeValidationStatus "
                        + "   && supplierBankAccountValidationStatus == :supplierBankAccountValidationStatus "
                        + "   && elmBankAccount                      == :elmBankAccount"
        ),

        @Query(
                name = "findByHandlingAndExtRefValidationStatus", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling               == :handling "
                        + "   && extRefValidationStatus == :extRefValidationStatus "
        ),

        @Query(
                name = "findByHandlingAndExtRefValidationStatusAndExtRefOrderValidationStatusAndExtRefOrder", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling                    == :handling "
                        + "   && extRefValidationStatus      == :extRefValidationStatus "
                        + "   && extRefOrderValidationStatus == :extRefOrderValidationStatus "
                        + "   && extRefOrder                 == :extRefOrder "
        ),
        @Query(
                name = "findByHandlingAndExtRefValidationStatusAndExtRefOrderValidationStatus", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling                    == :handling "
                        + "   && extRefValidationStatus      == :extRefValidationStatus "
                        + "   && extRefOrderValidationStatus == :extRefOrderValidationStatus "
        ),
        @Query(
                name = "findByHandlingAndExtRefValidationStatusAndExtRefProjectValidationStatusAndExtRefProject", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling                      == :handling "
                        + "   && extRefValidationStatus        == :extRefValidationStatus "
                        + "   && extRefProjectValidationStatus == :extRefProjectValidationStatus "
                        + "   && extRefProject                 == :extRefProject "
        ),
        @Query(
                name = "findByHandlingAndExtRefValidationStatusAndExtRefProjectValidationStatus", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling                      == :handling "
                        + "   && extRefValidationStatus        == :extRefValidationStatus "
                        + "   && extRefProjectValidationStatus == :extRefProjectValidationStatus "
        ),
        @Query(
                name = "findByHandlingAndExtRefValidationStatusAndExtRefWorkTypeValidationStatusAndExtRefWorkType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling                       == :handling "
                        + "   && extRefValidationStatus         == :extRefValidationStatus "
                        + "   && extRefWorkType                 == :extRefWorkType "
                        + "   && extRefWorkTypeValidationStatus == :extRefWorkTypeValidationStatus "
        ),
        @Query(
                name = "findByHandlingAndExtRefValidationStatusAndExtRefWorkTypeValidationStatus", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling                       == :handling "
                        + "   && extRefValidationStatus         == :extRefValidationStatus "
                        + "   && extRefWorkTypeValidationStatus == :extRefWorkTypeValidationStatus "
        ),
        @Query(
                name = "findByHandlingAndMediaCodeValidationStatus", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling                  == :handling "
                        + "   && mediaCodeValidationStatus == :mediaCodeValidationStatus "
        ),
        @Query(
                name = "findByHandlingAndMediaCodeValidationStatusAndMediaCode", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE handling                  == :handling "
                        + "   && mediaCodeValidationStatus == :mediaCodeValidationStatus "
                        + "   && mediaCode                 == :mediaCode "
        ),
})
@Uniques({
    @Unique(name = "CodaDocLine_docHead_lineNum_UNQ", members = { "docHead", "lineNum" }),
})
@Indices({
    @Index(name = "CodaDocLine_accountCodeValidation_IDX",
            members = { "handling", "accountCodeValidationStatus", "accountCode" }),
    @Index(name = "CodaDocLine_accountCodeEl3Validation_IDX",
            members = { "handling", "accountCodeValidationStatus", "accountCodeEl3ValidationStatus", "accountCodeEl3" }),
    @Index(name = "CodaDocLine_accountCodeEl5Validation_IDX",
            members = { "handling", "accountCodeValidationStatus", "accountCodeEl5ValidationStatus", "accountCodeEl5" }),
    @Index(name = "CodaDocLine_accountCodeEl6Validation_IDX",
            members = { "handling", "accountCodeValidationStatus", "accountCodeEl6ValidationStatus", "accountCodeEl6" }),

    @Index(name = "CodaDocLine_supplierBankAccountValidation_IDX",
            members = { "handling", "accountCodeValidationStatus", "supplierBankAccountValidationStatus", "elmBankAccount" }),

    @Index(name = "CodaDocLine_extRefValidation_IDX",
            members = { "handling", "extRefValidationStatus", "extRef3", "extRef5" }),

    @Index(name = "CodaDocLine_orderValidation_IDX",
            members = { "handling", "extRefValidationStatus", "extRefOrderValidationStatus", "extRefOrder" }),
    @Index(name = "CodaDocLine_projectValidation_IDX",
            members = { "handling", "extRefValidationStatus", "extRefProjectValidationStatus", "extRefProject" }),
    @Index(name = "CodaDocLine_workTypeValidation_IDX",
            members = { "handling", "extRefValidationStatus", "extRefWorkTypeValidationStatus", "extRefWorkType" }),

    @Index(name = "CodaDocLine_mediaCodeValidation_IDX",
            members = { "handling", "mediaCodeValidationStatus", "mediaCode" }),
})
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
            final String extRef4,
            final String extRef5,
            final String elmBankAccount,
            final String userRef1,
            final Character userStatus,
            final String mediaCode) {

        this.docHead = docHead;
        this.lineNum = lineNum;
        this.accountCode = accountCode;
        this.description = description;
        this.docValue = docValue;
        this.docSumTax = docSumTax;
        this.valueDate = valueDate;
        this.extRef3 = extRef3;
        this.extRef4 = extRef4;
        this.extRef5 = extRef5;
        this.elmBankAccount = elmBankAccount;
        this.userRef1 = userRef1;
        this.userStatus = userStatus;
        this.mediaCode = mediaCode;

        this.handling = Handling.INCLUDE;

        resetValidation();
    }

    @Programmatic
    public void resetValidation() {

        setAccountCodeValidationStatus(ValidationStatus.NOT_CHECKED);

        setAccountCodeEl3ValidationStatus(ValidationStatus.NOT_CHECKED);
        setAccountCodeEl3(null);

        setAccountCodeEl5ValidationStatus(ValidationStatus.NOT_CHECKED);
        setAccountCodeEl5(null);

        setAccountCodeEl6ValidationStatus(ValidationStatus.NOT_CHECKED);
        setAccountCodeEl6(null);

        setSupplierBankAccountValidationStatus(ValidationStatus.NOT_CHECKED);

        setExtRefValidationStatus(ValidationStatus.NOT_CHECKED);

        setExtRefOrderValidationStatus(ValidationStatus.NOT_CHECKED);
        setExtRefOrder(null);

        setExtRefProjectValidationStatus(ValidationStatus.NOT_CHECKED);
        setExtRefProject(null);

        setExtRefCostCentreValidationStatus(ValidationStatus.NOT_CHECKED);
        setExtRefCostCentre(null);

        setExtRefWorkTypeValidationStatus(ValidationStatus.NOT_CHECKED);
        setExtRefWorkType(null);

        setMediaCodeValidationStatus(ValidationStatus.NOT_CHECKED);
        setCodaPaymentMethod(null);

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
    private String extRef4;

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

    @Column(allowsNull = "true", length = 12)
    @Property()
    @Getter @Setter
    private String mediaCode;




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
    private ValidationStatus accountCodeEl3ValidationStatus;

    /**
     * Derived from the last portion of {@link #getAccountCode()}, only populated if
     * {@link #getAccountCodeValidationStatus()} is {@link ValidationStatus#VALID valid}
     */
    @Column(allowsNull = "true", length = 72)
    @Property()
    @Getter @Setter
    private String accountCodeEl3;



    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus accountCodeEl5ValidationStatus;

    /**
     * Derived from the last portion of {@link #getAccountCode()}, only populated if
     * {@link #getAccountCodeValidationStatus()} is {@link ValidationStatus#VALID valid}
     */
    @Column(allowsNull = "true", length = 72)
    @Property()
    @Getter @Setter
    private String accountCodeEl5;


    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus accountCodeEl6ValidationStatus;

    /**
     * Derived from the last portion of {@link #getAccountCode()}, only populated if
     * {@link #getAccountCodeValidationStatus()} is {@link ValidationStatus#VALID valid}
     */
    @Column(allowsNull = "true", length = 72)
    @Property()
    @Getter @Setter
    private String accountCodeEl6;

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
    private ValidationStatus extRefOrderValidationStatus;

    /**
     * As parsed from extRef3/extRef4/extRef5, only populated if {@link #getExtRefValidationStatus()} is {@link ValidationStatus#VALID valid}.
     */
    @Column(allowsNull = "true", length = 30)
    @Property()
    @Getter @Setter
    private String extRefOrder;



    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus extRefCostCentreValidationStatus;

    /**
     * As parsed from extRef3/extRef4/extRef5, only populated if {@link #getExtRefValidationStatus()} is {@link ValidationStatus#VALID valid}.
     */
    @Column(allowsNull = "true", length = 30)
    @Property()
    @Getter @Setter
    private String extRefCostCentre;



    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus extRefProjectValidationStatus;

    /**
     * As parsed from extRef3/extRef4/extRef5, only populated if {@link #getExtRefValidationStatus()} is {@link ValidationStatus#VALID valid}.
     */
    @Column(allowsNull = "true", length = 30)
    @Property()
    @Getter @Setter
    private String extRefProject;



    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus extRefWorkTypeValidationStatus;

    /**
     * As parsed from extRef3/extRef4/extRef5, only populated if {@link #getExtRefValidationStatus()} is {@link ValidationStatus#VALID valid}.
     */
    @Column(allowsNull = "true", length = 30)
    @Property()
    @Getter @Setter
    private String extRefWorkType;


    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus mediaCodeValidationStatus;

    @Column(allowsNull = "true", length = 12)
    @Property()
    @Getter @Setter
    private CodaPaymentMethod codaPaymentMethod;




    /**
     * Derived from parent {@link CodaDocHead#getHandling()}, for performance.
     */
    @Column(allowsNull = "false", length = 30)
    @Property()
    @Getter @Setter
    private Handling handling;


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
