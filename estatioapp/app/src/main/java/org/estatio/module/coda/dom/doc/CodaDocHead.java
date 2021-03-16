package org.estatio.module.coda.dom.doc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.estatio.module.base.dom.apptenancy.ApplicationTenancyLevel;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRoleTypeEnum;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.capex.dom.invoice.approval.tasks.IncomingInvoice_checkApprovalState;
import org.estatio.module.capex.dom.invoice.approval.triggers.IncomingInvoice_reject;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.coda.dom.LineType;
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
import org.estatio.module.task.dom.state.StateTransitionService;
import org.estatio.module.task.dom.task.Task;
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
import org.apache.isis.applib.services.background.BackgroundService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.scratchpad.Scratchpad;
import org.apache.isis.applib.services.tablecol.TableColumnOrderService;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.HasAtPath;

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
                name = "findByIncomingInvoice", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocHead "
                        + "WHERE incomingInvoice == :incomingInvoice "),
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
        ),
        @Query(
                name = "findByHandlingAndStatPayNotEqualToAndNotValid", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocHead "
                        + "WHERE handling          == :handling "
                        + "   && statPay           != :statPay "
                        + "   && reasonInvalid     != null "
        ),
        @Query(
                name = "findByStatPay", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocHead "
                        + "WHERE statPay          == :statPay "
        ),
        @Query(
                name = "findByCmpCodeAndIncomingInvoiceApprovalStateIsNotFinal", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocHead "
                        + "WHERE cmpCode    == :cmpCode "
                        + "   && (incomingInvoice.approvalState != 'PAID' && "
                        + "       incomingInvoice.approvalState != 'DISCARDED') "
        ),
        @Query(
                name = "findByIncomingInvoice", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocHead "
                        + "WHERE incomingInvoice == :incomingInvoice "
        ),
        @Query(
                name = "findByIncomingInvoiceAtPathPrefixAndApprovalState", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocHead "
                        + "WHERE incomingInvoice.approvalState == :approvalState "
                        + "   && incomingInvoice.applicationTenancyPath.startsWith(:atPathPrefix) "
        ),
        @Query(
                name = "findByIncomingInvoiceAtPathPrefixAndApprovalStateAndPaymentMethod", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.coda.dom.doc.CodaDocHead "
                        + "WHERE incomingInvoice.approvalState == :approvalState "
                        + "   && incomingInvoice.paymentMethod == :paymentMethod "
                        + "   && incomingInvoice.applicationTenancyPath.startsWith(:atPathPrefix) "
        )
})
@Unique(name = "CodaDocHead_cmpCode_docCode_docNum_UNQ", members = { "cmpCode", "docCode", "docNum" })
@Indices({
        @Index(name = "CodaDocHead_codaPeriodQuarter_handling_IDX", members = { "codaPeriodQuarter", "handling" })
})
@DomainObject(
        objectType = "coda.CodaDocHead",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class CodaDocHead implements Comparable<CodaDocHead>, HasAtPath {

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
            final String location,
            final String sha256,
            final String statPay) {

        this.cmpCode = cmpCode;
        this.docCode = docCode;
        this.docNum = docNum;
        this.codaTimeStamp = codaTimeStamp;
        this.inputDate = inputDate;
        this.docDate = docDate;
        this.codaPeriod = codaPeriod;
        this.location = location;
        this.sha256 = sha256;
        this.statPay = statPay;

        this.numberOfLines = 0;

        this.handling = Handling.INCLUDED;

        resetValidationAndDerivations();
    }

    void resetValidationAndDerivations() {

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

    @Column(allowsNull = "false", length = 64)
    @Property()
    @Getter @Setter
    private String sha256;

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

    @Persistent(defaultFetchGroup = "true")
    @Column(allowsNull = "true", name = "cmpCodeBuyerId")
    @Property()
    @Getter @Setter
    private Organisation cmpCodeBuyer;

    @Persistent(defaultFetchGroup = "true")
    @Column(allowsNull = "true", name = "incomingInvoiceId")
    @Property
    @Getter @Setter
    private IncomingInvoice incomingInvoice;

    /**
     * Derived persisted property in order to speed up homepage rendering.
     */
    @Column(allowsNull = "true")
    @PropertyLayout(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private Character userStatus;

    @javax.jdo.annotations.Persistent(
            mappedBy = "docHead", defaultFetchGroup = "false", dependentElement = "true"
    )
    @CollectionLayout(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private SortedSet<CodaDocLine> lines = new TreeSet<>();
    public SortedSet<CodaDocLine> getLines() {
        return lines;
    }


    @javax.jdo.annotations.NotPersistent
    @CollectionLayout(defaultView = "table", paged = 999)
    public List<CodaDocLine> getSummaryLines() {
        return linesOf(LineType.SUMMARY);
    }

    @javax.jdo.annotations.NotPersistent
    @CollectionLayout(defaultView = "table", paged = 999)
    public List<CodaDocLine> getAnalysisLines() {
        return linesOf(LineType.ANALYSIS);
    }

    private List<CodaDocLine> linesOf(final LineType lineType) {
        return Lists.newArrayList(getLines()).stream()
                .filter(x -> x.getLineType() == lineType)
                .collect(Collectors.toList());
    }

    @Programmatic
    public CodaDocLine summaryDocLine(final LineCache lineCache) {
        return findFirstLineByType(LineType.SUMMARY, lineCache);
    }

    @Programmatic
    public CodaDocLine firstAnalysisDocLine(final LineCache lineCache) {
        return findFirstLineByType(LineType.ANALYSIS, lineCache);
    }

    CodaDocLine findFirstLineByType(final LineType lineType, final LineCache lineCache) {
        return Lists.newArrayList(linesFor(lineCache)).stream()
                .filter(x -> x.getLineType() == lineType)
                .findFirst()
                .orElse(null);
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
    @PropertyLayout(named = "Analysis line(s) present?", hidden = Where.ALL_TABLES)
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
     *
     * <p>
     * Corresponds to typeCtDocUsed, typeCtDocPost, typeCtPayPostDest and similar enums in Coda WSDL,
     * max length is 8 ("anywhere")
     * </p>
     */
    @Column(allowsNull = "true", length = 12)
    @Property()
    @Getter @Setter
    private String location;

    /**
     * Indicates whether this document is paid.
     *
     * <p>
     * Corresponds to <code>typeCtStatPayDocLine</code> enum in Coda WSDL,
     * max length is 15 (&quot;draft_available&quot;)
     * </p>
     */
    @Column(allowsNull = "true", length = 20)
    @Property()
    @Getter @Setter
    private String statPay;

    /**
     * The date that the {@link #getStatPay()} is first changed to the value &quot;paid&quot;
     */
    @Column(allowsNull = "true")
    @javax.jdo.annotations.Persistent
    @Property()
    @Getter @Setter
    private LocalDate statPayPaidDate;

    public boolean isPaid() {
        return statPay != null && "paid".equals(statPay);
    }

    @Override
    public String getAtPath() {
        final Organisation cmpCodeBuyer = getCmpCodeBuyer();
        if (cmpCodeBuyer != null) {
            return cmpCodeBuyer.getApplicationTenancyPath();
        }
        if (getCmpCode() != null) {
            if (getCmpCode().startsWith("IT")) {
                return ApplicationTenancyLevel.ITALY.getPath();
            }
            if (getCmpCode().startsWith("FR")) {
                return ApplicationTenancyLevel.FRANCE.getPath();
            }
        }
        return ApplicationTenancyLevel.ROOT.getPath();
    }

    @Programmatic
    public CodaDocHead revalidate() {

        final ErrorSet errors = new ErrorSet();
        final boolean syncIfValid = false;

        revalidateAndKickEstatioObjectsIfAny(errors, syncIfValid);

        return this;
    }

    @Programmatic
    public CodaDocHead kick() {

        final ErrorSet errors = new ErrorSet();
        final boolean syncIfValid = true;

        revalidateAndKickEstatioObjectsIfAny(errors, syncIfValid);

        return this;
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

        final CodaDocLine summaryDocLineIfAny = summaryDocLine(LineCache.DEFAULT);
        if (summaryDocLineIfAny != null) {
            lineValidator.validateSummaryDocLine(summaryDocLineIfAny);

            for (final CodaDocLine analysisDocLine : getAnalysisLines()) {
                lineValidator.validateAnalysisDocLine(summaryDocLineIfAny, analysisDocLine);
            }
        }


        updateInvalidReasonBasedOnLines();
    }

    void updateInvalidReasonBasedOnLines() {
        setLineValidationStatus(ValidationStatus.VALID);
        Lists.newArrayList(getLines()).stream()
                .filter(CodaDocLine::isInvalid)
                .forEach(codaDocLine -> {
                    setLineValidationStatus(ValidationStatus.INVALID);
                    appendInvalidReason(
                            String.format("Line #%d (%s): %s", codaDocLine.getLineNum(), codaDocLine.getLineType(), codaDocLine.getReasonInvalid()));
                });
    }

    private void revalidateAndKickEstatioObjectsIfAny(
            final ErrorSet errors,
            final boolean createIfValid) {

        //
        // (re)validate this DocHead
        //
        revalidateOnly();

        final Memento existing = new Memento(this, derivedObjectLookup);
        kickEstatioObjectsIfAny(
                existing,
                errors, createIfValid);

    }


    @Programmatic
    public Map<Integer, LineData> getAnalysisLineDataByLineNumber() {

        if(isLegacyAnalysisLineWithNullDocValue()) {
            final HashMap<Integer, LineData> analysisLineDataByLineNumber = Maps.newHashMap();

            // get hold of the summary line and extract its project and charge
            final CodaDocLine summaryDocLine = summaryDocLine(LineCache.DEFAULT);

            if(summaryDocLine != null) {

                final Optional<Project> projectIfAny = Optional.ofNullable(
                        summaryDocLine.getExtRefProject());
                final Optional<Charge> chargeIfAny = Optional.ofNullable(
                        summaryDocLine.getExtRefWorkTypeCharge());

                // we also need to get hold of the invoice item from the parent invoice
                final IncomingInvoice incomingInvoice = getIncomingInvoice();
                final Optional<IncomingInvoiceItem> incomingInvoiceItemIfAny =
                        incomingInvoice != null ? incomingInvoice.firstItemIfAny() : Optional.empty();

                // ... and treat the summary line as if it's the analysisLine = 2
                analysisLineDataByLineNumber.put(2,
                        new LineData(incomingInvoiceItemIfAny, projectIfAny, chargeIfAny));
            }

            return analysisLineDataByLineNumber;
        }

        // create memento for existing analysis lines so can compare with their replacement.
        return getAnalysisLines().stream()
                .collect(Collectors.toMap(CodaDocLine::getLineNum, LineData::new));
    }

    /**
     * Originally we interpreted the CodaDoc XML by creating only a single summary line and a single analysis line,
     * where the summary line was used for the values and the project/work item, while the analysis line merely for
     * the el6 accode.
     *
     * However, we NOW create the XML from the analysis lines only.
     *
     * This method allows us to process the original summary line-based CodaDocHeads correctly with respect to the
     * new algorithm.
     */
    boolean isLegacyAnalysisLineWithNullDocValue() {
        CodaDocLine codaDocLine = firstAnalysisDocLine(LineCache.DEFAULT);
        if(codaDocLine == null) {
            return false;
        }
        final BigDecimal docValue = codaDocLine.getDocValue();
        return docValue == null;
    }

    /**
     * specifically, is an FR-ART17, with an analysis line that has a non-zero docSumTax
     */
    boolean isLegacyAnalysisLineForFrArt17WithNonZeroTax() {
        CodaDocLine codaDocLine = firstAnalysisDocLine(LineCache.DEFAULT);
        if (codaDocLine == null) {
            return false;
        }
        if (!getDocCode().equals("FR-ART17")) {
            return false;
        }
        if (codaDocLine.getDocSumTax() == null || codaDocLine.getDocSumTax().equals(BigDecimal.ZERO)) {
            return false;
        }
        return true;
    }

    /**
     * Expects that the doc has been {@link #revalidateOnly() validated} previously.
     */
    @Programmatic
    public void kickEstatioObjectsIfAny(
            final Memento previous,
            final ErrorSet errors,
            final boolean createIfValid) {


        final boolean createInvoice = createIfValid && isValid();

        // an invalid CodaDocHead are only soft errors (could just be book-keeping problems).
        // see Comparison (above) for hard errors.
        errors.add(this.getReasonInvalid());


        //
        // the various updates will only ever do an update of the derived objects, never create new stuff
        //
        final IncomingInvoice incomingInvoice =
                derivedObjectUpdater.upsertIncomingInvoice(this, previous, createInvoice);


        this.setIncomingInvoice(incomingInvoice);

        //
        // we'll create associated documents if an incoming invoice exists,
        // even if the DocHead is now invalid
        //
        final boolean createIfDoesNotExist = incomingInvoice != null;

        derivedObjectUpdater.updatePaperclip(
                this,
                previous,
                createIfDoesNotExist, errors);

        //
        // do the transitions here so that we update the appropriate pending task at the end
        //
        tryPullBackApprovalsIfRequired(incomingInvoice, errors);
        tryTransitionToPayableWhenInCodaBooks(incomingInvoice);
        tryTransitionToPaidWhenPaidInCoda(incomingInvoice, this);

        derivedObjectUpdater.tryUpdatePendingTaskIfRequired(
                this,
                errors);

        if (incomingInvoice != null) {
            setHandling(Handling.SYNCED);
        }
    }

    private void tryPullBackApprovalsIfRequired(final IncomingInvoice incomingInvoice, final ErrorSet errors) {
        //
        // reject if required (and possible)
        //
        if (errors.isNotEmpty() && incomingInvoice != null) {

            final IncomingInvoiceApprovalState approvalState = incomingInvoice.getApprovalState();

            // if has moved on from NEW ...
            if (approvalState != IncomingInvoiceApprovalState.NEW) {

                // ... and can reject (ie pull back to new), then do so.
                //
                // nb #1: we DON'T wrap the reject action, we want this always succeed.
                //        Otherwise we start to have to set up PROJECT_MANAGER roles etc for the system.
                //
                // nb #2: there's an subscriber specific to /ITA that
                // removes the ability to discard Italian IncomingInvoices
                //
                if (approvalState.isNotFinal()) {

                    final IncomingInvoice_reject mixin =
                            factoryService.mixin(IncomingInvoice_reject.class, incomingInvoice);
                    try {
                        // hmm, seems like anyone can reject (no special roles required).
                        mixin.act(
                                mixin.default0Act(), mixin.default1Act(mixin.default0Act()), errors.getText());

                    } catch (Exception ex) {
                        // best effort... being the issues to someone's attention, at least...
                        final IncomingInvoiceApprovalStateTransition pendingTransition =
                                stateTransitionService.pendingTransitionOf(incomingInvoice,
                                        IncomingInvoiceApprovalStateTransition.class);
                        Task task = pendingTransition.getTask();
                        if (task != null) {
                            task.setDescription(errors.getText());
                        }
                    }
                } else {
                    // nothing we can do here; the invoice is in a final state, eg paid.
                }
            } else {
                if (incomingInvoice.getPaymentMethod() != PaymentMethod.BANK_TRANSFER) {
                    final IncomingInvoice_checkApprovalState mixin =
                            factoryService.mixin(IncomingInvoice_checkApprovalState.class, incomingInvoice);

                    mixin.act();
                }
                // no need to do anything, hasn't yet been completed.
            }
        }
    }

    private void tryTransitionToPayableWhenInCodaBooks(final IncomingInvoice incomingInvoice) {

        if (incomingInvoice == null) {
            return;
        }

        final IncomingInvoiceApprovalState incomingInvoiceApprovalState = incomingInvoice.getApprovalState();
        switch (incomingInvoiceApprovalState) {

            case PENDING_CODA_BOOKS_CHECK:
                if (incomingInvoice.isPostedToCodaBooks()) {

                    stateTransitionService.trigger(incomingInvoice,
                            IncomingInvoiceApprovalStateTransitionType.CONFIRM_IN_CODA_BOOKS,
                            null, // currentTaskCommentIfAny
                            null  // nextTaskDescriptionIfAny
                    );
                }
                break;
        }
    }

    private void tryTransitionToPaidWhenPaidInCoda(final IncomingInvoice incomingInvoice, final CodaDocHead docHead) {

        if (incomingInvoice == null) {
            return;
        }

        final IncomingInvoiceApprovalState incomingInvoiceApprovalState = incomingInvoice.getApprovalState();
        switch (incomingInvoiceApprovalState) {

            case NEW:
            case SUSPENDED:
            case COMPLETED:
            case PENDING_ADVISE:
            case ADVISE_POSITIVE:
            case APPROVED_BY_CENTER_MANAGER:
            case APPROVED:
            case APPROVED_BY_COUNTRY_DIRECTOR:
            case PENDING_CODA_BOOKS_CHECK:
            case PAYABLE:
            case PAYABLE_BYPASSING_APPROVAL:
                if (docHead.isPaid()) {

                    stateTransitionService.trigger(incomingInvoice,
                            IncomingInvoiceApprovalStateTransitionType.PAID_IN_CODA,
                            null, // currentTaskCommentIfAny
                            null  // nextTaskDescriptionIfAny
                    );
                }
                break;
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

    @NotPersistent
    @Property(notPersisted = true)
    @PropertyLayout(hidden = Where.OBJECT_FORMS)
    public PaymentMethod getPaymentMethod() {
        final LineCache lineCache = (LineCache) scratchpad.get(LineCache.class);
        if (lineCache == null) {
            return null;
        }
        return getSummaryLinePaymentMethod(LineCache.DEFAULT);
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
        final MediaCodePaymentMethod mediaCodePaymentMethod = docLine.getMediaCodePaymentMethod();
        return mediaCodePaymentMethod != null ? mediaCodePaymentMethod.asPaymentMethod() : null;
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
    public String getSummaryLineUserRef1(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getUserRef1() : null;
    }

    @Programmatic
    public String getSummaryLineDocumentName(final LineCache lineCache) {
        final String ref1 = getSummaryLineUserRef1(lineCache);
        return !Strings.isNullOrEmpty(ref1) ? ref1 + ".pdf" : null;
    }

    @Programmatic
    public Order getSummaryLineExtRefOrder(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getExtRefOrder() : null;
    }

    @Programmatic
    public String getSummaryLineExtRef3Normalized(final LineCache lineCache) {
        final CodaDocLine docLine = summaryDocLine(lineCache);
        return docLine != null ? docLine.getExtRef3Normalized() : null;
    }

    /**
     * The {@link IncomingInvoiceType} of the first analysis line is used for routing the approval
     * of the entire * {@link IncomingInvoice}.
     */
    @Programmatic
    public IncomingInvoiceType getFirstAnalysisLineIncomingInvoiceType(final LineCache lineCache) {
        final CodaDocLine docLine = firstAnalysisDocLine(lineCache);
        return docLine != null ? docLine.getIncomingInvoiceType() : null;
    }

    @Programmatic
    public void saveAndThenSyncInIfNewAndValid() {
        if (!isAutosync()) {
            return;
        }
        if (!isValid()) {
            return;
        }
        if (getIncomingInvoice() != null) {
            // nothing to do, an invoice already exists.
            return;
        }

        if (!repositoryService.isPersistent(this)) {
            transactionService.flushTransaction();
        }

        // instead of queuing a background command, we simply complete this transaction and continue on.
        transactionService.nextTransaction();

        syncIfValid();
    }

    @Property
    public String getExtRef3() {
        final CodaDocLine docLine = summaryDocLine(LineCache.DEFAULT);
        return docLine != null ? docLine.getExtRef3() : null;
    }

    @Property
    public String getExtRef4() {
        final CodaDocLine docLine = summaryDocLine(LineCache.DEFAULT);
        return docLine != null ? docLine.getExtRef4() : null;
    }

    @Property
    public String getExtRef5() {
        final CodaDocLine docLine = summaryDocLine(LineCache.DEFAULT);
        return docLine != null ? docLine.getExtRef5() : null;
    }

    private void syncIfValid() {
        kick();
    }

    private boolean isAutosync() {
        ApplicationSetting autosyncSetting =
                ApplicationSettingKey.autosync.find(applicationSettingsServiceRW);
        return autosyncSetting != null && autosyncSetting.valueAsBoolean();
    }

    @Programmatic
    IncomingInvoiceType getIncomingInvoiceType() {
        final CodaDocLine line = isLegacyAnalysisLineWithNullDocValue() ? summaryDocLine(LineCache.DEFAULT) : firstAnalysisDocLine(LineCache.DEFAULT);
        return Optional.ofNullable(line)
                       .map(CodaDocLine::getIncomingInvoiceType)
                       .orElse(null);
    }

    @Programmatic
    public void updateDerivedUserStatus() {
        final CodaDocLine docLine = summaryDocLine(LineCache.DEFAULT);
        setUserStatus(docLine != null ? docLine.getUserStatus() : null);
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
        public static Comparison invalidatesApprovals(final String reasonFormatString, final Object... args) {
            return new Comparison(Type.DIFFERS_INVALIDATING_APPROVALS, String.format(reasonFormatString, args));
        }

        public static Comparison retainsApprovals() {
            return new Comparison(Type.DIFFERS_RETAIN_APPROVALS, null);
        }

        public static Comparison noOther() {
            return new Comparison(Type.NO_PREVIOUS, null);
        }
    }

    @Programmatic
    public boolean isSameAs(final CodaDocHead other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if(legacyAnalysisLineWithNullDocValue_StateDiffers(this, other) ||
           legacyAnalysisLineWithNullDocValue_StateDiffers(other, this)) {
            return false;
        }
        return Objects.equals(getSha256(), other.getSha256()) &&
               Objects.equals(getStatPay(), other.getStatPay());
    }

    private boolean legacyAnalysisLineWithNullDocValue_StateDiffers(final CodaDocHead one, final CodaDocHead other) {
        return one.isLegacyAnalysisLineWithNullDocValue() && !other.isLegacyAnalysisLineWithNullDocValue();
    }

    @Programmatic
    public Comparison compareWith(final CodaDocHead other) {
        if (other == null) {
            return Comparison.noOther();
        }
        if (isSameAs(other)) {
            return Comparison.same();
        }
        CodaDocLine summaryLine = summaryDocLine(LineCache.DEFAULT);
        CodaDocLine otherSummaryLine = other.summaryDocLine(LineCache.DEFAULT);
        if (summaryLine != null && otherSummaryLine == null) {
            return Comparison.invalidatesApprovals("Previous had no summary doc line");
        }
        if (summaryLine == null && otherSummaryLine != null) {
            return Comparison.invalidatesApprovals("Replacement has no summary doc line");
        }
        if (summaryLine != null) {
            if (summaryLine.getSupplierBankAccountValidationStatus() != ValidationStatus.NOT_CHECKED &&
                    otherSummaryLine.getSupplierBankAccountValidationStatus() != ValidationStatus.NOT_CHECKED &&
                    // ECP-979: because payable invoices cannot be rejected (and paid invoice codadocheasd / lines are not evaluated anymore) this code in fact disables
                    // every check on a different IBAN on the summary line
                    other.getIncomingInvoice()!=null &&
                    Arrays.asList(IncomingInvoiceApprovalState.PAYABLE, IncomingInvoiceApprovalState.PAID).contains(other.getIncomingInvoice().getApprovalState()) &&
                    // end ECP-979
                    !Objects.equals(summaryLine.getSupplierBankAccount(), otherSummaryLine.getSupplierBankAccount())) {
                return Comparison.invalidatesApprovals("Line #%d (%s): %s",
                        summaryLine.getLineNum(), summaryLine.getLineType(), "Supplier bank account has changed");
            }
            if (!Objects.equals(summaryLine.getDocValue(), otherSummaryLine.getDocValue())) {
                return Comparison.invalidatesApprovals("Line #%d (%s): %s",
                        summaryLine.getLineNum(), summaryLine.getLineType(), "Doc value (gross amount) has changed");
            }
            if (!Objects.equals(summaryLine.getDocSumTax(), otherSummaryLine.getDocSumTax())) {
                return Comparison.invalidatesApprovals("Line #%d (%s): %s",
                        summaryLine.getLineNum(), summaryLine.getLineType(), "Doc sum tax (VAT amount) has changed");
            }
            if (summaryLine.getMediaCodeValidationStatus() != ValidationStatus.NOT_CHECKED &&
                    otherSummaryLine.getMediaCodeValidationStatus() != ValidationStatus.NOT_CHECKED &&
                    !Objects.equals(summaryLine.getMediaCode(), otherSummaryLine.getMediaCode())) {
                return Comparison.invalidatesApprovals("Line #%d (%s): %s",
                        summaryLine.getLineNum(), summaryLine.getLineType(), "Media code (payment method) has changed");
            }
            if (!Objects.equals(summaryLine.getDueDate(), otherSummaryLine.getDueDate())) {
                return Comparison.invalidatesApprovals("Line #%d (%s): %s",
                        summaryLine.getLineNum(), summaryLine.getLineType(), "Due date has changed");
            }
            if (!Objects.equals(summaryLine.getValueDate(), otherSummaryLine.getValueDate())) {
                return Comparison.invalidatesApprovals("Line #%d (%s): %s",
                        summaryLine.getLineNum(), summaryLine.getLineType(), "Value date has changed");
            }
            if (!Strings.isNullOrEmpty(otherSummaryLine.getUserRef1()) &&
                    !Objects.equals(summaryLine.getUserRef1(), otherSummaryLine.getUserRef1())) {
                return Comparison.invalidatesApprovals("Line #%d (%s): %s",
                        summaryLine.getLineNum(), summaryLine.getLineType(), "User Ref 1 (bar code) has changed");
            }
            if (otherSummaryLine.getDescription() != null &&
                    !Objects.equals(summaryLine.getDescription(), otherSummaryLine.getDescription())) {
                return Comparison.invalidatesApprovals("Line #%d (%s): %s",
                        summaryLine.getLineNum(), summaryLine.getLineType(), "Description has changed");
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
    PartyRepository partyRepository;

    @NotPersistent
    @Inject
    PartyRoleTypeRepository partyRoleTypeRepository;

    @NotPersistent
    @Inject
    LineValidator lineValidator;

    @NotPersistent
    @Inject
    DerivedObjectLookup derivedObjectLookup;

    @NotPersistent
    @Inject
    DerivedObjectUpdater derivedObjectUpdater;

    @NotPersistent
    @Inject
    ApplicationSettingsServiceRW applicationSettingsServiceRW;

    @NotPersistent
    @Inject
    Scratchpad scratchpad;

    @NotPersistent
    @Inject
    BackgroundService backgroundService;

    @NotPersistent
    @Inject
    RepositoryService repositoryService;

    @NotPersistent
    @Inject
    TransactionService transactionService;

    @NotPersistent
    @Inject
    StateTransitionService stateTransitionService;

    @NotPersistent
    @Inject
    FactoryService factoryService;

    @NotPersistent
    @Inject
    OrderItemInvoiceItemLinkRepository linkRepository;

    @DomainService(nature = NatureOfService.DOMAIN, menuOrder = "100")
    public static class TableColumnService implements TableColumnOrderService {

        @Override
        public List<String> orderParented(
                final Object parent,
                final String collectionId,
                final Class<?> collectionType,
                final List<String> propertyIds) {
            if (parent instanceof CodaDocHead && "summaryLines".equals(collectionId)) {
                return Arrays.asList(
                        "docHead"
                        , "lineNum"
                        , "accountCode"
                        , "accountCodeEl3CostCentre"
                        , "description"
                        , "docValue"
                        , "docSumTax"
                        , "valueDate"
                        , "extRef3"
                        , "extRefOrder"
                        , "userRef1"
                        , "userStatus"
                        , "elmBankAccount"
                        , "mediaCode"
                        , "reasonInvalid"
                );
            }
            if (parent instanceof CodaDocHead && "analysisLines".equals(collectionId)) {
                return Arrays.asList(
                        "docHead"
                        , "lineNum"
                        , "accountCode"
                        , "description"
                        , "docValue"
                        , "docSumTax"
                        , "valueDate"
                        , "extRef4"
                        , "extRef5"
                        , "incomingInvoiceType"
                        , "incomingInvoiceItem"
                        , "extRefWorkTypeCharge"
                        , "extRefProject"
                        , "extRefOrderItem"
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
