package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.ComparisonChain;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.eventbus.PropertyDomainEvent;
import org.apache.isis.applib.services.title.TitleService;

import org.isisaddons.module.security.dom.tenancy.HasAtPath;

import org.estatio.module.base.dom.apptenancy.ApplicationTenancyLevel;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.coda.dom.costcentre.CostCentre;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.party.dom.Organisation;

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
                name = "findByUserRef1", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE userRef1 == :userRef1"),
        @Query(
                name = "findByIncomingInvoiceItem", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE incomingInvoiceItem == :incomingInvoiceItem"),
        @Query(
                name = "findByDocHeadAndLineNum", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE docHead == :docHead "
                        + "   && lineNum == :lineNum "),
        @Query(
                name = "findByCodaPeriodQuarterAndHandlingAndValid", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE docHead.codaPeriodQuarter == :codaPeriodQuarter "
                        + "   && docHead.handling          == :handling "
                        + "   && docHead.reasonInvalid     == null "),
        @Query(
                name = "findByCodaPeriodQuarterAndHandlingAndNotValid", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE docHead.codaPeriodQuarter == :codaPeriodQuarter "
                        + "   && docHead.handling          == :handling "
                        + "   && docHead.reasonInvalid     != null "),
        @Query(
                name = "findByHandlingAndNotValid", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE docHead.handling          == :handling "
                        + "   && docHead.reasonInvalid     != null "),
        @Query(
                name = "findByCodaPeriodQuarterAndHandling", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocLine "
                        + "WHERE docHead.codaPeriodQuarter == :codaPeriodQuarter "
                        + "   && docHead.handling          == :handling "),
})
@Uniques({
        @Unique(name = "CodaDocLine_docHead_lineNum_UNQ", members = { "docHead", "lineNum" }),
})
@Indices({
        @Index(name = "CodaDocLine_incomingInvoiceItem_IDX", members = { "incomingInvoiceItem" }),
})
@DomainObject(
        objectType = "coda.CodaDocLine",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class CodaDocLine implements Comparable<CodaDocLine>, HasAtPath {

    public static final String USER_REF_SDI_ID_PREFIX = "S";

    /**
     * For properties that are only populated for summary lines.
     */
    public static class SummaryOnlyPropertyDomainEvent extends PropertyDomainEvent<CodaDocLine, Object> {
    }

    /**
     * For properties that are only populated for analysis lines.
     */
    public static class AnalysisOnlyPropertyDomainEvent extends PropertyDomainEvent<CodaDocLine, Object> {
    }

    public CodaDocLine() {
    }

    public CodaDocLine(
            final CodaDocHead docHead,
            final int lineNum,
            final LineType lineType,
            final String accountCode,
            final String description,
            final BigDecimal docValue,
            final BigDecimal docSumTax,
            final LocalDate dueDate,
            final LocalDate valueDate,
            final String extRef2,
            final String extRef3,
            final String extRef4,
            final String extRef5,
            final String elmBankAccount,
            final String userRef1,
            final Character userStatus,
            final String mediaCode) {

        this.docHead = docHead;
        this.lineNum = lineNum;
        this.lineType = lineType;
        this.accountCode = accountCode;
        this.description = description;
        this.docValue = docValue;
        this.docSumTax = docSumTax;
        this.dueDate = dueDate;
        this.valueDate = valueDate;
        this.extRef2 = extRef2;
        this.extRef3 = extRef3;
        this.extRef4 = extRef4;
        this.extRef5 = extRef5;
        this.elmBankAccount = elmBankAccount;
        this.userRef1 = userRef1;
        this.userStatus = userStatus;
        this.mediaCode = mediaCode;

        //
        // set all the 'derived' stuff to its initial value
        //
        resetValidationAndDerivations();

    }

    @Programmatic
    public void resetValidationAndDerivations() {

        // use setters so that DN is aware
        setHandling(Handling.INCLUDED);

        setAccountCodeValidationStatus(ValidationStatus.NOT_CHECKED);

        setAccountCodeEl3(null);
        setAccountCodeEl3ValidationStatus(ValidationStatus.NOT_CHECKED);

        setAccountCodeEl5(null);
        setAccountCodeEl5ValidationStatus(ValidationStatus.NOT_CHECKED);

        setAccountCodeEl6(null);
        setAccountCodeEl6ValidationStatus(ValidationStatus.NOT_CHECKED);
        setAccountCodeEl6Supplier(null);

        setSupplierBankAccountValidationStatus(ValidationStatus.NOT_CHECKED);

        setExtRefValidationStatus(ValidationStatus.NOT_CHECKED);

        setExtRef3Normalized(null);
        setExtRefOrderValidationStatus(ValidationStatus.NOT_CHECKED);

        setExtRefProjectReference(null);
        setExtRefProjectValidationStatus(ValidationStatus.NOT_CHECKED);

        setExtRefWorkTypeChargeReference(null);
        setExtRefWorkTypeValidationStatus(ValidationStatus.NOT_CHECKED);

        setUserRef1ValidationStatus(ValidationStatus.NOT_CHECKED);

        setMediaCodePaymentMethod(null);
        setMediaCodeValidationStatus(ValidationStatus.NOT_CHECKED);

        setReasonInvalid(null);
    }

    public String title() {
        return String.format("%s | # %d", titleService.titleOf(getDocHead()), getLineNum());
    }

    @Inject
    TitleService titleService;

    @Persistent(defaultFetchGroup = "true")
    @Column(allowsNull = "false", name = "docHeadId")
    @Property
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private CodaDocHead docHead;

    @Override
    public String getAtPath() {
        final CodaDocHead hasAtPath = getDocHead();
        return hasAtPath != null
                ? hasAtPath.getAtPath()
                : ApplicationTenancyLevel.ROOT.getPath();
    }

    @Column(allowsNull = "false")
    @Property()
    @Getter @Setter
    private int lineNum;

    @Column(allowsNull = "false", length = 8)
    @Property()
    @Getter @Setter
    private LineType lineType;

    @Column(allowsNull = "true", length = 72)
    @Property()
    @Getter @Setter
    private String accountCode;

    @Column(allowsNull = "true", length = 36)
    @Property()
    @Getter @Setter
    private String description;

    /**
     * Depending on line type, is either the GROSS or NET amount.
     *
     * <ul>
     *     <li>
     *          For summary lines, this is the value of the CODA's Line.docValue, which is the GROSS amount
     *     </li>
     *     <li>
     *          For analysis lines, it is also the value of CODA's Line.docValue, but is the NET amount.
     *     </li>
     * </ul>
     *
     */
    @Column(allowsNull = "true", scale = 2)
    @Property()
    @Getter @Setter
    private BigDecimal docValue;

    /**
     * The amount of VAT, though the derivation depends on line type.
     *
     * <ul>
     *     <li>
     *          For summary lines, this is just the value of the CODA's Line.docSumTax
     *     </li>
     *     <li>
     *          For analysis lines, it is the sum of Line.taxes.tax.value
     *     </li>
     * </ul>
     */
    @Column(allowsNull = "true", scale = 2)
    @Property()
    @Getter @Setter
    private BigDecimal docSumTax;

    @Column(allowsNull = "true")
    @javax.jdo.annotations.Persistent
    @Property()
    @Getter @Setter
    private LocalDate dueDate;

    @Column(allowsNull = "true")
    @javax.jdo.annotations.Persistent
    @Property()
    @Getter @Setter
    private LocalDate valueDate;

    /**
     * Supplier invoice number.
     */
    @Column(allowsNull = "true", length = 32)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private String extRef2;

    @Column(allowsNull = "true", length = 32)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private String extRef3;

    @Column(allowsNull = "true", length = 32)
    @Property(domainEvent = AnalysisOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private String extRef4;

    @Column(allowsNull = "true", length = 32)
    @Property(domainEvent = AnalysisOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private String extRef5;

    /**
     * Corresponds to barcode number
     */
    @Column(allowsNull = "true", length = 35)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private String userRef1;

    /**
     * Encodes the nature of the approval such that this Coda document is payable (Estatio uses 'X' or 'Z').
     */
    @Column(allowsNull = "true")
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private Character userStatus;

    /**
     * Corresponds to IBAN
     */
    @Column(allowsNull = "true", length = 36)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private String elmBankAccount;

    @Column(allowsNull = "true", length = 12)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
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
        if (reasonInvalid != null) {
            reasonInvalid += "\n";
        } else {
            reasonInvalid = "";
        }
        reasonInvalid += reason;
        setReasonInvalid(reasonInvalid);
    }

    @Programmatic
    public boolean isValid() {
        return getReasonInvalid() == null;
    }

    @Programmatic
    public boolean isInvalid() {
        return !isValid();
    }

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus accountCodeValidationStatus;

    @Column(allowsNull = "false", length = 20)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private ValidationStatus accountCodeEl3ValidationStatus;

    /**
     * Derived from the el3 portion of {@link #getAccountCode()}, only populated if
     * {@link #getAccountCodeValidationStatus()} is {@link ValidationStatus#VALID valid}
     */
    @Column(allowsNull = "true", length = 72)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private String accountCodeEl3;

    /**
     * Derived from certain characters of el3 of {@link #getAccountCode()}, only populated if
     * {@link #getAccountCodeValidationStatus()} is {@link ValidationStatus#VALID valid}
     */
    @Column(allowsNull = "true", name = "accountCodeEl3CostCentreId")
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @PropertyLayout(named = "Cost Centre")
    @Getter @Setter
    private CostCentre accountCodeEl3CostCentre;

    /**
     * Provided as a convenience.
     * <p>
     * Most {@link CostCentre}s correspond to a {@link org.estatio.module.asset.dom.Property property}; the exceptions are those that are {@link CostCentre#isGeneral() general} costs.
     */
    @Programmatic
    public org.estatio.module.asset.dom.Property getAccountEl3Property() {
        final CostCentre costCentre = getAccountCodeEl3CostCentre();
        return costCentre != null
                ? costCentre.getProperty()
                : null;
    }

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
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private ValidationStatus accountCodeEl6ValidationStatus;

    @Column(allowsNull = "true", name = "accountCodeEl6SupplierId")
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private Organisation accountCodeEl6Supplier;

    /**
     * Derived from the last portion of {@link #getAccountCode()}, only populated if
     * {@link #getAccountCodeValidationStatus()} is {@link ValidationStatus#VALID valid}
     */
    @Column(allowsNull = "true", length = 72)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private String accountCodeEl6;

    @Column(allowsNull = "false", length = 20)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private ValidationStatus supplierBankAccountValidationStatus;

    @Column(allowsNull = "true", name = "supplierBankAccountId")
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private BankAccount supplierBankAccount;

    @Column(allowsNull = "false", length = 20)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private ValidationStatus extRefValidationStatus;

    /**
     * indicates the validity of the order for summary lines, and the validity of the order item for analysis lines.
     */
    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus extRefOrderValidationStatus;

    @Column(allowsNull = "false", length = 20)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private ValidationStatus userRef1ValidationStatus;

    /**
     * As parsed from extRef3/extRef4/extRef5, only populated if {@link #getExtRefValidationStatus()} is {@link ValidationStatus#VALID valid}.
     * <p>
     * Is the normalized version of the extRef3.
     * For old style, is nn/CCC/ll/WWW   (nn and ll are not normalized, are verbatim)
     * For new style, is nn/CCC/PPP/WWW  (nn is not normalized).
     */
    @Column(allowsNull = "true", length = 30)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @PropertyLayout(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String extRef3Normalized;

    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @PropertyLayout(hidden = Where.ALL_TABLES)
    public Organisation getBuyer() {
        return this.getDocHead().getCmpCodeBuyer();
    }

    @Column(allowsNull = "true", length = 8)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @PropertyLayout(named = "Buyer Order Number")
    @Getter @Setter
    private BigInteger extRefOrderGlobalNumerator;

    @Column(allowsNull = "true", name = "extRefOrderId")
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @PropertyLayout(named = "Order")
    @Getter @Setter
    private Order extRefOrder;

    @Column(allowsNull = "true", name = "extRefOrderItemId")
    @Property(domainEvent = AnalysisOnlyPropertyDomainEvent.class)
    @PropertyLayout(named = "Order Item")
    @Getter @Setter
    private OrderItem extRefOrderItem;


    @Column(allowsNull = "false", length = 20)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private ValidationStatus extRefProjectValidationStatus;

    /**
     * As parsed from extRef3/extRef4/extRef5, normalized to 3 digits and prefixed with 'ITPR'.
     * <p>
     * Only populated if {@link #getExtRefValidationStatus()} is {@link ValidationStatus#VALID valid},
     */
    @Column(allowsNull = "true", length = 30)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private String extRefProjectReference;

    @Column(allowsNull = "true", name = "extRefProjectId")
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @PropertyLayout(named = "Project")
    @Getter @Setter
    private Project extRefProject;

    @Column(allowsNull = "false", length = 20)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private ValidationStatus extRefWorkTypeValidationStatus;

    @Column(allowsNull = "true", name = "extRefWorkTypeChargeId")
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @PropertyLayout(named = "Charge")
    @Getter @Setter
    private Charge extRefWorkTypeCharge;

    /**
     * As parsed from extRef3/extRef4/extRef5, normalized to 3 digits and prefixed with either 'ITWT'
     * (if was a new format, or an old format where the lookup from old charge to new charge could be performed) or
     * prefixed with 'OLD' (if was old format, and no lookup of new charge from old was possible)
     * <p>
     * Only populated if {@link #getExtRefValidationStatus()} is {@link ValidationStatus#VALID valid}.
     */
    @Column(allowsNull = "true", length = 30)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private String extRefWorkTypeChargeReference;

    @Column(allowsNull = "false", length = 20)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private ValidationStatus mediaCodeValidationStatus;

    @Column(allowsNull = "true", length = 12)
    @Property(domainEvent = SummaryOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private MediaCodePaymentMethod mediaCodePaymentMethod;

    /**
     * Populated only for analysis lines.
     */
    @Column(length = 50, allowsNull = "true")
    @Property(domainEvent = AnalysisOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private IncomingInvoiceType incomingInvoiceType;


    @Column(allowsNull = "true", name = "incomingInvoiceItemId")
    @Property(domainEvent = AnalysisOnlyPropertyDomainEvent.class)
    @Getter @Setter
    private IncomingInvoiceItem incomingInvoiceItem;

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

    @Override
    public String toString() {
        return "CodaDocLine{" +
                "docHead=" + docHead +
                ", lineNum=" + lineNum +
                ", accountCode='" + accountCode + '\'' +
                ", extRef3='" + extRef3 + '\'' +
                ", extRef5='" + extRef5 + '\'' +
                '}';
    }

    @Inject
    OrderRepository orderRepository;

    @DomainService
    public static class PropertyVisibilityAdvisor extends AbstractSubscriber {

        @EventHandler
        public void on(SummaryOnlyPropertyDomainEvent ev) {
            switch (ev.getEventPhase()) {
                case HIDE:
                    LineType lineType = ev.getSource().getLineType();
                    if (lineType == null) {
                        return;
                    }
                    switch (lineType) {
                        case SUMMARY:
                            break;
                        default:
                            ev.hide();
                            break;
                    }
                    break;
            }
        }

        @EventHandler
        public void on(AnalysisOnlyPropertyDomainEvent ev) {
            switch (ev.getEventPhase()) {
                case HIDE:
                    LineType lineType = ev.getSource().getLineType();
                    if (lineType == null) {
                        return;
                    }
                    switch (lineType) {
                        case ANALYSIS:
                            break;
                        default:
                            ev.hide();
                            break;
                    }
                    break;
            }
        }
    }

}
