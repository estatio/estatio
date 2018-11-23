package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.tablecol.TableColumnOrderService;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.coda.dom.doc.appsettings.ApplicationSettingKey;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.role.PartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeRepository;
import org.estatio.module.settings.dom.ApplicationSetting;
import org.estatio.module.settings.dom.ApplicationSettingsServiceRW;

import lombok.Data;
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
                        + "WHERE cmpCode == :cmpCode "
                        + "   && docCode == :docCode "
                        + "   && docNum  == :docNum "),
        @Query(
                name = "findByCodaPeriodQuarterAndHandling", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocHead "
                        + "WHERE codaPeriodQuarter == :codaPeriodQuarter "
                        + "   && handling          == :handling "),
        @Query(
                name = "findByCodaPeriodQuarterAndHandlingAndValid", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocHead "
                        + "WHERE codaPeriodQuarter == :codaPeriodQuarter "
                        + "   && handling          == :handling "
                        + "   && reasonInvalid     == null "),
        @Query(
                name = "findByCodaPeriodQuarterAndHandlingAndNotValid", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocHead "
                        + "WHERE codaPeriodQuarter == :codaPeriodQuarter "
                        + "   && handling          == :handling "
                        + "   && reasonInvalid     != null "
        )
})
@Unique(name = "CodaDocHead_cmpCode_docCode_docNum_UNQ", members = { "cmpCode", "docCode", "docNum" })
@Indices({
    @Index(name = "CodaDocHead_codaPeriodQuarter_handling_IDX", members = { "codaPeriodQuarter","handling" })
})
@DomainObject(
        objectType = "coda.CodaDocHead",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class CodaDocHead implements Comparable<CodaDocHead> {

    static final CodaPeriodParser parser = new CodaPeriodParser();

    public CodaDocHead() {
    }

    public CodaDocHead(
            final String cmpCode,
            final String docCode,
            final String docNum,
            final short codaTimeStamp,
            final LocalDate inputDate,
            final LocalDate docDate,
            final String codaPeriod,
            final String location) {

        this.cmpCode = cmpCode;
        this.docCode = docCode;
        this.docNum = docNum;
        this.codaTimeStamp = codaTimeStamp;
        this.inputDate = inputDate;
        this.docDate = docDate;
        this.codaPeriod = codaPeriod;
        this.location = location;

        this.numberOfLines = 0;

        resetValidationAndDerivations();
    }

    void resetValidationAndDerivations() {

        // use setters so that DN is aware
        setHandling(Handling.INCLUDED);

        setLineValidationStatus(ValidationStatus.NOT_CHECKED);

        setCmpCodeValidationStatus(ValidationStatus.NOT_CHECKED);
        setCmpCodeBuyer(null);

        setSummaryLineIsPresentValidationStatus(ValidationStatus.NOT_CHECKED);
        setAnalysisLineIsPresentValidationStatus(ValidationStatus.NOT_CHECKED);

        setReasonInvalid(null);

        setCodaPeriodQuarter(quarterFrom(getCodaPeriod()));

        Lists.newArrayList(getLines()).forEach(CodaDocLine::resetValidationAndDerivations);
    }

    static String quarterFrom(final String codaPeriod) {
        final CodaPeriodParser.Parsed parsed = parser.parse(codaPeriod);
        return parsed.asQuarter();
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
    private short codaTimeStamp;

    @Column(allowsNull = "true")
    @javax.jdo.annotations.Persistent
    @Property()
    @Getter @Setter
    private LocalDate inputDate;

    @Column(allowsNull = "true")
    @javax.jdo.annotations.Persistent
    @Property()
    @Getter @Setter
    private LocalDate docDate;

    /**
     * For example, '2019/1' meaning the first period of the financial year 2018-2019, ie July 2018
     * ("take away 6 months from the date you think it is").
     * <p>
     * The biggest we have seen in the DB is 2014/9999.  Clearly a hack, and none for 'FR-GEN', but anyway...
     */
    @Column(allowsNull = "false", length = 9)
    @javax.jdo.annotations.Persistent
    @Property()
    @Getter @Setter
    private String codaPeriod;

    @Column(allowsNull = "true", length = 8)
    @javax.jdo.annotations.Persistent
    @Property()
    @Getter @Setter
    private String codaPeriodQuarter;

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
        if (reasonInvalid != null) {
            reasonInvalid += "\n";
        } else {
            reasonInvalid = "";
        }
        reasonInvalid += reason;
        setReasonInvalid(reasonInvalid);
    }

    @Column(allowsNull = "false", length = 20)
    @Property()
    @PropertyLayout(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private ValidationStatus cmpCodeValidationStatus;

    /**
     * to avoid clutter; highly unlikely this will be invalid.
     */
    public boolean hideCmpCodeValidationStatus() {
        return getCmpCodeBuyer() != null;
    }

    @Column(allowsNull = "true", name = "cmpCodeBuyerId")
    @Property()
    @Getter @Setter
    private Organisation cmpCodeBuyer;

    @Column(allowsNull = "true", name = "incomingInvoiceId")
    @Property
    @Getter @Setter
    private IncomingInvoice incomingInvoice;

    @javax.jdo.annotations.Persistent(
            mappedBy = "docHead", defaultFetchGroup = "false", dependentElement = "true"
    )
    @CollectionLayout(defaultView = "table", paged = 999)
    @Getter @Setter
    private SortedSet<CodaDocLine> lines = new TreeSet<>();

    @Programmatic
    public CodaDocLine summaryDocLine(final LineCache lineCache) {
        return findFirstLineByType(LineType.SUMMARY, lineCache);
    }

    @Programmatic
    public CodaDocLine analysisDocLine(final LineCache lineCache) {
        return findFirstLineByType(LineType.ANALYSIS, lineCache);
    }

    CodaDocLine findFirstLineByType(final LineType lineType, final LineCache lineCache) {
        return Lists.newArrayList(linesFor(lineCache)).stream()
                .filter(x -> x.getLineType() == lineType)
                .findFirst()
                .orElse(null);
    }

    private SortedSet<CodaDocLine> linesFor() {
        return linesFor(LineCache.DEFAULT);
    }
    private SortedSet<CodaDocLine> linesFor(final LineCache lineCache) {
        return lineCache.linesFor(this);
    }

    @Column(allowsNull = "false", length = 20)
    @Property()
    @Getter @Setter
    private ValidationStatus lineValidationStatus;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @PropertyLayout(named = "Summary line present?", hidden = Where.ALL_TABLES)
    @Getter @Setter
    private ValidationStatus summaryLineIsPresentValidationStatus;

    @Column(allowsNull = "false", length = 20)
    @Property()
    @PropertyLayout(named = "Analysis line present?", hidden = Where.ALL_TABLES)
    @Getter @Setter
    private ValidationStatus analysisLineIsPresentValidationStatus;

    /**
     * How this document should be handled (override {@link #getLineValidationStatus() validation status}).
     */
    @Column(allowsNull = "false", length = 30)
    @Property()
    @PropertyLayout(hidden = Where.ALL_TABLES)
    @Getter @Setter
    private Handling handling;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public CodaDocHead handleAs(Handling handling) {
        // in case invoked directly
        if (this.getHandling() == Handling.SYNCED) {
            return this;
        }
        this.setHandling(handling);
        Lists.newArrayList(getLines()).forEach(codaDocLine -> codaDocLine.setHandling(handling));
        return this;
    }

    public String disableHandleAs() {
        return getHandling() == Handling.SYNCED
                ?
                "An Estatio Incoming Invoice has already been created, so the handling of this Coda document cannot be changed"
                :
                null;
    }

    public Handling default0HandleAs() {
        return getHandling();
    }

    public List<Handling> choices0HandleAs() {
        Handling currentHandling = getHandling();
        switch (currentHandling) {
        case SYNCED:
            return Collections.singletonList(Handling.SYNCED);
        case EXCLUDED:
        case INCLUDED:
        default:
            return Arrays.asList(Handling.INCLUDED, Handling.EXCLUDED);
        }
    }

    /**
     * Indicates the location of this document.
     */
    @Column(allowsNull = "true", length = 12)
    @Property()
    @Getter @Setter
    private String location;

    public enum SynchronizationPolicy {
        /**
         * When revalidating a {@link CodaDocHead}, if the document ends up as {@link CodaDocHead#isValid() valid},
         * then synchronize (ie create corresponding {@link IncomingInvoice Estatio invoice} and related objects.
         */
        SYNC_IF_VALID,
        /**
         * When revalidating a {@link CodaDocHead}, then even if the document ends up as
         * {@link CodaDocHead#isValid() valid},
         * do NOT synchronize.
         *
         * <p>
         * Note that if the {@link CodaDocHead} was valid previously and had been sync'd, then those Estatio
         * objects remain.
         * </p>
         */
        DONT_SYNC_EVEN_IF_VALID
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public CodaDocHead revalidate(final SynchronizationPolicy policy) {
        revalidateOnly();
        updateEstatioObjects(policy);
        return this;
    }
    public SynchronizationPolicy default0Revalidate() {
        return SynchronizationPolicy.SYNC_IF_VALID;
    }

    @Programmatic
    public CodaDocHead revalidateOnly() {
        resetValidationAndDerivations();
        if (getHandling() == Handling.EXCLUDED) {
            return this;
        }

        validateBuyer();
        validateLines();

        return this;
    }

    private boolean isAutosync() {
        ApplicationSetting autosyncSetting =
                ApplicationSettingKey.autosync.find(applicationSettingsServiceRW);
        return autosyncSetting != null && autosyncSetting.valueAsBoolean();
    }

    void validateBuyer() {

        final String buyerRef = getCmpCode();
        final Party buyerParty = partyRepository.findPartyByReference(buyerRef);
        final PartyRoleType ecpRoleType =
                IncomingInvoiceRoleTypeEnum.ECP.findUsing(partyRoleTypeRepository);
        if (buyerParty == null) {
            setCmpCodeValidationStatus(ValidationStatus.INVALID);
            appendInvalidReason("No buyer party found for cmpCode '%s'", buyerRef);
        } else if (!(buyerParty instanceof Organisation)) {
            setCmpCodeValidationStatus(ValidationStatus.INVALID);
            appendInvalidReason("Party found for cmpCode '%s' is not an Organisation", buyerRef);
        } else if (!buyerParty.hasPartyRoleType(ecpRoleType)) {
            setCmpCodeValidationStatus(ValidationStatus.INVALID);
            appendInvalidReason("Organisation '%s' does not have ECP role", buyerRef);
        }

        // if get this far and not yet invalid, then must be valid.
        if (getCmpCodeValidationStatus() != ValidationStatus.INVALID) {
            setCmpCodeValidationStatus(ValidationStatus.VALID);
            setCmpCodeBuyer((Organisation) buyerParty);
        }
    }

    void validateLines() {

        final CodaDocLine summaryDocLine = summaryDocLine(LineCache.DEFAULT);
        if (summaryDocLine != null) {
            lineValidator.validateSummaryDocLine(summaryDocLine);
        }

        final CodaDocLine analysisDocLine = analysisDocLine(LineCache.DEFAULT);
        if (analysisDocLine != null) {
            lineValidator.validateAnalysisDocLine(analysisDocLine);
        }

        updateInvalidReasonBasedOnLines();
    }

    void updateInvalidReasonBasedOnLines() {
        setLineValidationStatus(ValidationStatus.VALID);
        Lists.newArrayList(getLines()).stream()
                .filter(CodaDocLine::isInvalid)
                .forEach(codaDocLine -> {
                    setLineValidationStatus(ValidationStatus.INVALID);
                    appendInvalidReason(codaDocLine.getLineType() + ": " + codaDocLine.getReasonInvalid());
                });
    }

    void updateEstatioObjects(final SynchronizationPolicy syncPolicy) {

        final ErrorSet errors = new ErrorSet();
        errors.addIfNotEmpty(getReasonInvalid());

        final boolean syncIfNew = isValid() && syncPolicy == SynchronizationPolicy.SYNC_IF_VALID;

        final IncomingInvoice incomingInvoice = derivedObjectUpdater.upsertIncomingInvoice(this, syncIfNew);

        setIncomingInvoice(incomingInvoice); // if any

        derivedObjectUpdater.updateLinkToOrderItem(this, syncIfNew, errors);
        derivedObjectUpdater.updatePaperclip(this, syncIfNew, errors);
        derivedObjectUpdater.updatePendingTask(this, syncIfNew, errors);

        if (syncIfNew) {
            setHandling(Handling.SYNCED);
        }
    }

    @Programmatic
    public boolean isValid() {
        return getReasonInvalid() == null;
    }

    @Programmatic
    public boolean isInvalid() {
        return !isValid();
    }

    @Programmatic
    public String getSummaryLineAccountCode(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getAccountCode() : null;
    }

    @Programmatic
    public String getSummaryLineAccountCodeEl3(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getAccountCodeEl3() : null;
    }

    @Programmatic
    public String getSummaryLineAccountCodeEl5(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getAccountCodeEl5() : null;
    }

    @Programmatic
    public String getSummaryLineAccountCodeEl6(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getAccountCodeEl6() : null;
    }

    @Programmatic
    public Party getSummaryLineAccountCodeEl6Supplier(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getAccountCodeEl6Supplier() : null;
    }

    @Programmatic
    public String getSummaryLineExtRef2(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getExtRef2() : null;
    }

    @Programmatic
    public org.estatio.module.asset.dom.Property getSummaryLineAccountEl3Property(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getAccountEl3Property() : null;
    }

    @Programmatic
    public String getSummaryLineElmBankAccount(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getElmBankAccount() : null;
    }

    @Programmatic
    public BankAccount getSummaryLineSupplierBankAccount(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getSupplierBankAccount() : null;
    }

    @Programmatic
    public PaymentMethod getSummaryLinePaymentMethod(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        if (docLine == null) {
            return null;
        }
        final CodaPaymentMethod codaPaymentMethod = docLine.getCodaPaymentMethod();
        return codaPaymentMethod != null ? codaPaymentMethod.asPaymentMethod() : null;
    }

    @Programmatic
    public LocalDate getSummaryLineValueDate(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getValueDate() : null;
    }

    @Programmatic
    public LocalDate getSummaryLineDueDate(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getDueDate() : null;
    }

    @Programmatic
    public String getSummaryLineDescription(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getDescription() : null;
    }

    @Programmatic
    public String getSummaryLineExtRefWorkTypeChargeReference(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getExtRefWorkTypeChargeReference() : null;
    }

    @Programmatic
    public Charge getSummaryLineExtRefWorkTypeCharge(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getExtRefWorkTypeCharge() : null;
    }

    @Programmatic
    public String getSummaryLineExtRefProjectReference(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getExtRefProjectReference() : null;
    }

    @Programmatic
    public Project getSummaryLineExtRefProject(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getExtRefProject() : null;
    }

    @Programmatic
    public BigDecimal getSummaryLineDocValue(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getDocValue() : null;
    }

    @Programmatic
    public BigDecimal getSummaryLineDocSumTax(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getDocSumTax() : null;
    }

    @Programmatic
    public String getSummaryLineMediaCode(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getMediaCode() : null;
    }

    @Programmatic
    public String getSummaryLineExtRefCostCentre(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getExtRefCostCentre() : null;
    }

    @Programmatic
    public String getSummaryLineUserRef1(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getUserRef1() : null;
    }

    @Programmatic
    public String getSummaryLineDocumentName(final LineCache lineCache) {
        final String ref1 = getSummaryLineUserRef1(lineCache);
        return ref1 != null ? ref1 + ".pdf" : null;
    }

    @Programmatic
    public OrderItem getSummaryLineExtRefOrderItem(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getExtRefOrderItem() : null;
    }

    @Programmatic
    public String getSummaryLineExtRef3Normalized(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getExtRef3Normalized() : null;
    }

    @Programmatic
    public String getSummaryLineExtRefOrderGlobalNumerator(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getExtRefOrderGlobalNumerator() : null;
    }

    @Programmatic
    public String getAnalysisLineAccountCode(final LineCache lineCache) {
        final CodaDocLine docLine = analysisDocLine(lineCache);
        return docLine != null ? docLine.getAccountCode() : null;
    }

    @Programmatic
    public IncomingInvoiceType getAnalysisLineIncomingInvoiceType(final LineCache lineCache) {
        final CodaDocLine docLine = analysisDocLine(lineCache);
        return docLine != null ? docLine.getIncomingInvoiceType() : null;
    }

    @Data
    public static class Comparison {
        public enum Type {
            DIFFERS_INVALIDATING_APPROVALS,
            DIFFERS_RETAIN_APPROVALS,
            SAME,
            NO_PREVIOUS
        }

        private final Type type;
        /**
         * Only populated if {@link #getType()} is {@link Type#DIFFERS_INVALIDATING_APPROVALS}.
         */
        private final String reason;

        public static Comparison same() {
            return new Comparison(Comparison.Type.SAME, null);
        }

        public static Comparison invalidatesApprovals(final String reason) {
            return new Comparison(Type.DIFFERS_INVALIDATING_APPROVALS, reason);
        }

        public static Comparison retainsApprovals() {
            return new Comparison(Type.DIFFERS_RETAIN_APPROVALS, null);
        }

        public static Comparison noPrevious() {
            return new Comparison(Type.NO_PREVIOUS, null);
        }
    }

    @Programmatic
    public boolean isSameAs(final CodaDocHead other) {
        if (other == null) {
            return false;
        }
        return other == this || other.getCodaTimeStamp() == getCodaTimeStamp();
    }

    @Programmatic
    Comparison compareWithPrevious() {
        CodaDocHead existing = codaDocHeadRepository.findByCandidate(this);
        if (isSameAs(existing)) {
            return new Comparison(Comparison.Type.SAME, null);
        }
        return compareWith(existing);
    }

    @Programmatic
    public Comparison compareWith(final CodaDocHead existing) {
        if (existing == null) {
            return Comparison.noPrevious();
        }
        if (isSameAs(existing)) {
            return Comparison.same();
        }
        CodaDocLine summaryDocLine = summaryDocLine(LineCache.DEFAULT);
        CodaDocLine existingSummaryDocLine = existing.summaryDocLine(LineCache.DEFAULT);
        if (summaryDocLine != null && existingSummaryDocLine == null) {
            return Comparison.invalidatesApprovals("Previous had no summary doc line");
        }
        if (summaryDocLine == null && existingSummaryDocLine != null) {
            return Comparison.invalidatesApprovals("Replacement has no summary doc line");
        }
        if (summaryDocLine != null && existingSummaryDocLine != null) {
            if (!Objects
                    .equals(summaryDocLine.getSupplierBankAccount(), existingSummaryDocLine.getSupplierBankAccount())) {
                return Comparison.invalidatesApprovals("Supplier bank account has changed");
            }
            if (!Objects.equals(summaryDocLine.getDocValue(), existingSummaryDocLine.getDocValue())) {
                return Comparison.invalidatesApprovals("Gross amount has changed");
            }
            if (!Objects.equals(summaryDocLine.getDocSumTax(), existingSummaryDocLine.getDocSumTax())) {
                return Comparison.invalidatesApprovals("VAT amount has changed");
            }
        }

        return Comparison.retainsApprovals();
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
                "companyCode='" + getCmpCode() + '\'' +
                ", docCode='" + getDocCode() + '\'' +
                ", docNum='" + getDocNum() + '\'' +
                '}';
    }

    //endregion

    @NotPersistent
    @Inject
    CodaDocHeadRepository codaDocHeadRepository;

    @NotPersistent
    @Inject
    PartyRepository partyRepository;

    @NotPersistent
    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

    @NotPersistent
    @Inject
    LineValidator lineValidator;

    @NotPersistent
    @Inject
    DerivedObjectUpdater derivedObjectUpdater;

    @NotPersistent
    @Inject
    ApplicationSettingsServiceRW applicationSettingsServiceRW;

    @DomainService(nature = NatureOfService.DOMAIN, menuOrder = "100")
    public static class TableColumnService implements TableColumnOrderService {

        @Override
        public List<String> orderParented(
                final Object parent,
                final String collectionId,
                final Class<?> collectionType,
                final List<String> propertyIds) {
            if (parent instanceof CodaDocHead && "lines".equals(collectionId)) {
                return Arrays.asList(
                        "docHead"
                        , "lineNum"
                        , "accountCode"
                        , "description"
                        , "docValue"
                        , "docSumTax"
                        , "valueDate"
                        , "extRef3"
                        , "extRef4"
                        , "extRef5"
                        , "userRef1"
                        , "userStatus"
                        , "elmBankAccount"
                        , "mediaCode"
                        , "reasonInvalid"
                );
            }
            return null;
        }

        @Override
        public List<String> orderStandalone(final Class<?> collectionType, final List<String> propertyIds) {
            return null;
        }

    }
}
