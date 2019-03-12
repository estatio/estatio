package org.estatio.module.capex.dom.payment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import org.joda.time.DateTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.jaxb.JaxbService;
import org.apache.isis.applib.services.linking.DeepLinkService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.pdfbox.dom.service.PdfBoxService;
import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.HasAtPath;

import org.incode.module.communications.dom.mixins.DocumentConstants;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.capex.app.credittransfer.CreditTransferExportLine;
import org.estatio.module.capex.app.credittransfer.CreditTransferExportService;
import org.estatio.module.capex.app.paymentline.PaymentLineForExcelExportV1;
import org.estatio.module.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.module.capex.dom.payment.approval.PaymentBatchApprovalStateTransition;
import org.estatio.module.capex.dom.payment.approval.triggers.PaymentBatch_complete;
import org.estatio.module.capex.dom.state.State;
import org.estatio.module.capex.dom.state.StateTransition;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.capex.dom.state.StateTransitionType;
import org.estatio.module.capex.dom.state.Stateful;
import org.estatio.module.capex.dom.util.InvoicePageRange;
import org.estatio.module.capex.platform.pdfmanipulator.ExtractSpec;
import org.estatio.module.capex.platform.pdfmanipulator.PdfManipulator;
import org.estatio.module.capex.platform.pdfmanipulator.Stamp;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;

import iso.std.iso._20022.tech.xsd.pain_001_001.AccountIdentification4Choice;
import iso.std.iso._20022.tech.xsd.pain_001_001.BranchAndFinancialInstitutionIdentification4;
import iso.std.iso._20022.tech.xsd.pain_001_001.CashAccount16;
import iso.std.iso._20022.tech.xsd.pain_001_001.CreditTransferTransactionInformation10;
import iso.std.iso._20022.tech.xsd.pain_001_001.CustomerCreditTransferInitiationV03;
import iso.std.iso._20022.tech.xsd.pain_001_001.Document;
import iso.std.iso._20022.tech.xsd.pain_001_001.FinancialInstitutionIdentification7;
import iso.std.iso._20022.tech.xsd.pain_001_001.GroupHeader32;
import iso.std.iso._20022.tech.xsd.pain_001_001.PartyIdentification32;
import iso.std.iso._20022.tech.xsd.pain_001_001.PaymentInstructionInformation3;
import iso.std.iso._20022.tech.xsd.pain_001_001.PaymentMethod3Code;
import static java.util.stream.Collectors.groupingBy;
import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "PaymentBatch"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByDebtorBankAccountAndApprovalState", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.payment.PaymentBatch "
                        + "WHERE debtorBankAccount == :debtorBankAccount "
                        + "   && approvalState     == :approvalState "
                        + "ORDER BY createdOn DESC "
        ),
        @Query(
                name = "findByDebtorBankAccount", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.payment.PaymentBatch "
                        + "WHERE debtorBankAccount == :debtorBankAccount "
                        + "ORDER BY createdOn DESC "
        ),
        @Query(
                name = "findByCreatedOnBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.payment.PaymentBatch "
                        + "WHERE createdOn >= :startDate "
                        + "   && createdOn <= :endDate "
                        + "ORDER BY createdOn DESC "
        ),
        @Query(
                name = "findByApprovalState", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.payment.PaymentBatch "
                        + "WHERE approvalState  == :approvalState "
                        + "ORDER BY createdOn DESC "
        )
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "payment.PaymentBatch",
        persistingLifecycleEvent = PaymentBatch.ObjectPersistingEvent.class,
        persistedLifecycleEvent = PaymentBatch.ObjectPersistedEvent.class
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class PaymentBatch extends UdoDomainObject2<PaymentBatch> implements Stateful {

    public static class ObjectPersistedEvent
            extends org.apache.isis.applib.services.eventbus.ObjectPersistedEvent<PaymentBatch> {
    }

    public static class ObjectPersistingEvent
            extends org.apache.isis.applib.services.eventbus.ObjectPersistingEvent<PaymentBatch> {
    }

    public PaymentBatch() {
        super("createdOn, debtorBankAccount");
    }

    public PaymentBatch(
            final DateTime createdOn,
            final BankAccount debtorBankAccount,
            final PaymentBatchApprovalState approvalState) {
        this();
        this.createdOn = createdOn;
        this.debtorBankAccount = debtorBankAccount;
        this.approvalState = approvalState;
    }

    public String title() {
        switch (getApprovalState()) {
            case NEW:
                return titleService.titleOf(getDebtorBankAccount()) + " (new " + getCreatedOnYMD() + ")";
            default:
                return titleService.titleOf(getDebtorBankAccount()) + " @ " + getRequestedExecutionDate().toString("yyyyMMdd-hhmm");
        }
    }

    /**
     * Document > PmtInf > DbtrAcct > Id > IBAN
     * Document > PmtInf > DbtrAgt > FinInstnId > BIC
     */
    @Column(allowsNull = "false", name = "debtorBankAccountId")
    @Getter @Setter
    private BankAccount debtorBankAccount;

    /**
     * Document > CstmrCdtTrfInitn > GrpHdr > CreDtTm
     */
    @Column(allowsNull = "false")
    @Getter @Setter
    private DateTime createdOn;

    /**
     * Document > PmtInf > ReqdExctnDt
     */
    @Column(allowsNull = "true")
    @Getter @Setter
    private DateTime requestedExecutionDate;

    @Property(notPersisted = true)
    public BigDecimal getTotalNetAmount() {
        return sum(IncomingInvoice::getNetAmount);
    }

    @Property(notPersisted = true, hidden = Where.ALL_TABLES)
    public BigDecimal getTotalVatAmount() {
        return sum(IncomingInvoice::getVatAmount);
    }

    @Property(notPersisted = true)
    public BigDecimal getTotalGrossAmount() {
        return sum(IncomingInvoice::getGrossAmount);
    }

    @Property(notPersisted = true)
    public int getNumTransfers() {
        return getTransfers().size();
    }

    @Property(notPersisted = true)
    public int getNumInvoices() {
        return getLines().size();
    }

    private BigDecimal sum(final Function<IncomingInvoice, BigDecimal> functionToObtainAmount) {
        return Lists.newArrayList(getLines()).stream()
                .map(PaymentLine::getInvoice)
                .map(functionToObtainAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getDebtorBankAccount().getApplicationTenancy();
    }

    /**
     * Document > PmtInf > CdtTrfTxInf (* many)
     */
    @Persistent(mappedBy = "batch", dependentElement = "true")
    @CollectionLayout(sortedBy = PaymentLine.CreditorBankAccountComparator.class)
    @Getter @Setter
    private SortedSet<PaymentLine> lines = new TreeSet<>();

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public PaymentBatch createAndCompleteUrgentPaymentBatch(
            final List<IncomingInvoice> urgentInvoices,
            final DateTime requestedExecutionDate,
            @Nullable final String comment) {
        final PaymentBatch urgentBatch = paymentBatchRepository.create(clockService.nowAsDateTime(), getDebtorBankAccount(), PaymentBatchApprovalState.NEW);
        urgentInvoices.forEach(invoice -> {
            urgentBatch.addLineIfRequired(invoice);
            this.removeLineFor(invoice);
        });

        final PaymentBatch_complete mixin = factoryService.mixin(PaymentBatch_complete.class, urgentBatch);

        return wrapperFactory.wrap(mixin).act(requestedExecutionDate, comment);
    }

    public List<IncomingInvoice> choices0CreateAndCompleteUrgentPaymentBatch() {
        return getLines()
                .stream()
                .map(PaymentLine::getInvoice)
                .collect(Collectors.toList());
    }

    public DateTime default1CreateAndCompleteUrgentPaymentBatch() {
        final PaymentBatch_complete mixin = factoryService.mixin(PaymentBatch_complete.class, this);
        return mixin.default0Act();
    }

    public boolean hideCreateAndCompleteUrgentPaymentBatch() {
        final PaymentBatch_complete mixin = factoryService.mixin(PaymentBatch_complete.class, this);
        return mixin.hideAct();
    }

    public String disableCreateAndCompleteUrgentPaymentBatch() {
        final PaymentBatch_complete mixin = factoryService.mixin(PaymentBatch_complete.class, this);
        return mixin.disableAct();
    }

    @Programmatic
    public void addLineIfRequired(final IncomingInvoice incomingInvoice) {
        if (!getApprovalState().isModifiable()) {
            // no-op if not modifiable.
            // this shouldn't happen because calling actions should have appropriate guards;
            // so this is just belt-n-braces
            return;
        }
        Optional<PaymentLine> lineIfAny = lineIfAnyFor(incomingInvoice);
        if (lineIfAny.isPresent()) {
            return;
        }
        final int sequenceToUse = findFreeSequence();
        BigDecimal transferAmount = coalesce(incomingInvoice.getGrossAmount(), BigDecimal.ZERO);
        final String remittanceInformation = incomingInvoice.getInvoiceNumber();
        final PaymentLine line =
                new PaymentLine(this, sequenceToUse, incomingInvoice, transferAmount, remittanceInformation);
        serviceRegistry2.injectServicesInto(lineIfAny);
        getLines().add(line);
    }

    private int findFreeSequence() {
        final List<Integer> usedSequences =
                Lists.newArrayList(getLines()).stream()
                        .map(PaymentLine::getSequence)
                        .collect(Collectors.toList());
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            if (!usedSequences.contains(i)) {
                return i;
            }
        }
        throw new IllegalStateException("Unable to locate free sequence number to use");
    }

    private static BigDecimal coalesce(final BigDecimal amount, final BigDecimal other) {
        return amount != null ? amount : other;
    }

    private Optional<PaymentLine> lineIfAnyFor(final IncomingInvoice invoice) {
        return Lists.newArrayList(getLines()).stream()
                .filter(line -> line.getInvoice() == invoice)
                .findFirst();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public boolean getUpstreamCreditNoteFound() {
        return Lists.newArrayList(getLines()).stream().filter(x -> x.getUpstreamCreditNoteFound()).findAny().isPresent();
    }

    /**
     * TODO: inline this mixin
     */
    @Mixin(method = "act")
    public static class removeInvoice {
        private final PaymentBatch paymentBatch;

        public removeInvoice(final PaymentBatch paymentBatch) {
            this.paymentBatch = paymentBatch;
        }

        @Action(
                semantics = SemanticsOf.IDEMPOTENT,
                publishing = Publishing.DISABLED
        )
        public PaymentBatch act(
                final List<IncomingInvoice> incomingInvoices,
                @ParameterLayout(describedAs = "Whether the removed invoices should also be rejected") final boolean rejectAlso,
                @ParameterLayout(describedAs = "If rejecting, then explain why so that the error can be fixed")
                @Nullable final String rejectionReason) {
            for (IncomingInvoice incomingInvoice : incomingInvoices) {
                paymentBatch.removeLineFor(incomingInvoice);
                if (rejectAlso) {
                    stateTransitionService.trigger(
                            incomingInvoice, IncomingInvoiceApprovalStateTransitionType.REJECT, null, rejectionReason);
                }
            }
            return paymentBatch;
        }

        public String validateAct(
                final List<IncomingInvoice> incomingInvoices,
                final boolean rejectAlso,
                final String rejectionReason
        ) {
            return rejectAlso && rejectionReason == null ? "Provide a reason if rejecting" : null;
        }

        public List<IncomingInvoice> choices0Act() {
            return Lists.newArrayList(paymentBatch.getLines()).stream()
                    .map(PaymentLine::getInvoice)
                    .collect(Collectors.toList());
        }

        public String disableAct() {
            final String reason = paymentBatch.reasonDisabledDueToState();
            if (reason != null) {
                return reason;
            }
            return paymentBatch.getLines().isEmpty() ? "No invoices to remove" : null;
        }

        @Inject
        StateTransitionService stateTransitionService;
    }

    @Programmatic
    public void remove() {
        // Clean up transitions
        approvalStateTransitionRepository.deleteFor(this);
        remove(this);
    }

    /**
     * TODO: inline this mixin
     */
    @Mixin(method = "act")
    public static class removeAll {
        private final PaymentBatch paymentBatch;

        public removeAll(final PaymentBatch paymentBatch) {
            this.paymentBatch = paymentBatch;
        }

        @Action(
                semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE,
                publishing = Publishing.DISABLED
        )
        @ActionLayout(cssClassFa = "fa-mail-reply", cssClass = "btn-warning")
        public PaymentBatch act() {
            paymentBatch.clearLines();
            return paymentBatch;
        }

        public String disableAct() {
            final String reason = paymentBatch.reasonDisabledDueToState();
            if (reason != null) {
                return reason;
            }
            return paymentBatch.getLines().isEmpty() ? "No invoices to remove" : null;
        }
    }

    @Programmatic
    public void clearLines() {
        getLines().clear();
    }

    @Programmatic
    public void removeLineFor(final IncomingInvoice incomingInvoice) {
        Optional<PaymentLine> paymentLineIfAny = lineIfAnyFor(incomingInvoice);
        paymentLineIfAny.ifPresent(paymentLine -> getLines().remove(paymentLine));
    }

    public List<CreditTransfer> getTransfers() {
        return queryResultsCache.execute(this::doGetCreditTransfers, getClass(), "getTransfers", this);
    }

    private List<CreditTransfer> doGetCreditTransfers() {
        List<CreditTransfer> transfers = Lists.newArrayList();

        final Map<BankAccount, List<PaymentLine>> lineBySeller =
                Lists.newArrayList(getLines()).stream()
                        .sorted(Comparator.comparing(PaymentLine::getSequence))
                        .collect(groupingBy(PaymentLine::getCreditorBankAccount,
                                TreeMap::new,
                                toSortedList(Comparator.comparing(PaymentLine::getSequence))));

        for (Map.Entry<BankAccount, List<PaymentLine>> linesByBankAccount : lineBySeller.entrySet()) {
            final CreditTransfer creditTransfer = new CreditTransfer();
            creditTransfer.setBatch(this);

            final BankAccount bankAccount = linesByBankAccount.getKey();
            final List<PaymentLine> lines = linesByBankAccount.getValue();

            final String sequenceNums = extractAndJoin(lines, line -> "" + line.getSequence(), "-");
            final String endToEndId = String.format("%s-%s", getId(), sequenceNums);
            creditTransfer.setEndToEndId(endToEndId);

            creditTransfer.setSellerBankAccount(bankAccount);

            creditTransfer.setLines(lines);

            final BigDecimal amount = lines.stream()
                    .map(PaymentLine::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (!lines.isEmpty()) {
                // should always be non-empty, just playing safe...
                final PaymentLine firstLine = lines.get(0);
                creditTransfer.setCurrency(firstLine.getCurrency());
            }
            creditTransfer.setAmount(amount);

            //  -PM-19229-12-2016-2-RO
            //  -2017-01-04-RO
            //  -L 17-01-302-RO
            //  -FC-1702CS1-0002-RO
            //  -AF1T2017ASL-RO
            final String remittanceInformation = extractAndJoin(lines, line -> line.getInvoice().getInvoiceNumber(), ";");
            creditTransfer.setRemittanceInformation(remittanceInformation);

            creditTransfer.setSeller(bankAccount.getOwner());
            creditTransfer.setSellerPostalAddressCountry(ctryFor(bankAccount.getOwner()));

            transfers.add(creditTransfer);
        }

        return transfers;
    }

    private static <T> Collector<T, ?, List<T>> toSortedList(Comparator<? super T> c) {
        return Collectors.collectingAndThen(
                Collectors.toCollection(ArrayList::new), l -> {
                    l.sort(c);
                    return l;
                });
    }

    String extractAndJoin(final List<PaymentLine> lines, final Function<PaymentLine, String> func, final String separator) {
        final List<String> invoiceNumbers = lines.stream().map(func)
                .collect(Collectors.toList());
        return Joiner.on(separator).join(invoiceNumbers);
    }

    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false")
    private PaymentBatchApprovalState approvalState;

    @Override
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > S getStateOf(
            final Class<ST> stateTransitionClass) {
        if (stateTransitionClass == PaymentBatchApprovalStateTransition.class) {
            return (S) approvalState;
        }
        return null;
    }

    @Override
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > void setStateOf(
            final Class<ST> stateTransitionClass, final S newState) {
        if (stateTransitionClass == PaymentBatchApprovalStateTransition.class) {
            setApprovalState((PaymentBatchApprovalState) newState);
        }
    }

    @Programmatic
    public String reasonDisabledDueToState() {
        PaymentBatchApprovalState currentState = getApprovalState();
        return currentState.isModifiable()
                ? null
                : "Cannot modify because payment batch is in state of " + currentState;
    }

    /**
     * TODO: inline this mixin
     */
    @Mixin(method = "act")
    public static class downloadPaymentFile {
        private final PaymentBatch paymentBatch;

        public downloadPaymentFile(final PaymentBatch paymentBatch) {
            this.paymentBatch = paymentBatch;
        }

        @Action(semantics = SemanticsOf.SAFE)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public Clob act(final String documentName) {
            Document document = paymentBatch.convertToXmlDocument();
            String xml = jaxbService.toXml(document);
            return new Clob(documentName, "text/xml", xml);
        }

        public String disableAct() {
            if (paymentBatch.getLines().isEmpty()) {
                return "No payment lines";
            }
            if (paymentBatch.getApprovalState() == PaymentBatchApprovalState.NEW) {
                return "Batch is not yet complete";
            }
            return null;
        }

        public String default0Act() {
            return paymentBatch.fileNameWithSuffix("xml");
        }

        @Inject
        JaxbService jaxbService;

    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Blob downloadReviewPdf(
            final String documentName,
            @ParameterLayout(named = "How many first pages of each invoice's PDF?") final Integer numFirstPages,
            @ParameterLayout(named = "How many final pages of each invoice's PDF?") final Integer numLastPages) throws IOException {

        final List<File> pdfFiles = Lists.newArrayList();

        final List<CreditTransfer> transfers = this.getTransfers();
        for (CreditTransfer transfer : transfers) {
            final List<PaymentLine> lines = transfer.getLines();

            for (final PaymentLine line : lines) {
                final IncomingInvoice invoice = line.getInvoice();

                final BankAccount bankAccount = invoice.getBankAccount();

                final Optional<org.incode.module.document.dom.impl.docs.Document> invoiceDocIfAny =
                        lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(invoice);

                if (invoiceDocIfAny.isPresent()) {
                    final org.incode.module.document.dom.impl.docs.Document invoiceDoc = invoiceDocIfAny.get();
                    final byte[] invoiceDocBytes = invoiceDoc.asBytes();

                    final Optional<org.incode.module.document.dom.impl.docs.Document> ibanProofDocIfAny = lookupAttachedPdfService
                            .lookupIbanProofPdfFrom(bankAccount);

                    List<String> leftLines = Lists.newArrayList();
                    leftLines.add("xfer id: " + transfer.getEndToEndId() + " / " + line.getSequence());
                    for (IncomingInvoice.ApprovalString approvalString : invoice.getApprovals()) {
                        leftLines.add(String.format(
                                "approved by: %s",
                                approvalString.getCompletedBy()));
                        leftLines.add("on: " + approvalString.getCompletedOn());
                    }

                    final List<String> rightLines = Lists.newArrayList();
                    rightLines.add(String.format("debtor IBAN: %s", line.getBatch().getDebtorBankAccount().getIban()));
                    rightLines.add(String.format("crdtor IBAN: %s", line.getCreditorBankAccount().getIban()));
                    rightLines.add(String.format("gross Amt  : %s", new DecimalFormat("0.00").format(line.getAmount())));

                    boolean attachProof = false;
                    final String proof;
                    if (ibanProofDocIfAny.isPresent()) {
                        final org.incode.module.document.dom.impl.docs.Document ibanProofDoc = ibanProofDocIfAny.get();
                        if (DocumentTypeData.IBAN_PROOF.isDocTypeFor(ibanProofDoc)) {
                            proof = "Separate IBAN proof (next page)";
                            attachProof = true;
                        } else {
                            proof = "Invoice used as IBAN proof";
                        }
                    } else {
                        proof = "No IBAN proof";
                    }
                    rightLines.add(proof);

                    URI uri = deepLinkService.deepLinkFor(invoice);

                    final Stamp stamp = new Stamp(leftLines, rightLines, uri.toString());
                    final byte[] extractedInvoiceDocBytes =
                            pdfManipulator.extractAndStamp(invoiceDocBytes, new ExtractSpec(numFirstPages, numLastPages), stamp);

                    appendTempFile(extractedInvoiceDocBytes, documentName, pdfFiles);

                    if (attachProof) {
                        final org.incode.module.document.dom.impl.docs.Document ibanProofDoc = ibanProofDocIfAny.get();
                        final byte[] ibanProofBytes = ibanProofDoc.asBytes();
                        final byte[] firstPageIbanProofDocBytes =
                                pdfManipulator.extract(ibanProofBytes, ExtractSpec.FIRST_PAGE_ONLY);

                        appendTempFile(firstPageIbanProofDocBytes, documentName, pdfFiles);
                    }
                }
            }
        }

        byte[] pdfMergedBytes = pdfBoxService.merge(pdfFiles);

        pdfFiles.stream().forEach(this::cleanup);

        return new Blob(documentName, DocumentConstants.MIME_TYPE_APPLICATION_PDF, pdfMergedBytes);
    }

    void cleanup(File tempFile) {
        if (tempFile != null) {
            try {
                Files.delete(tempFile.toPath());
                tempFile.delete();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    void appendTempFile(final byte[] pdfBytes, final String documentName, final List<File> pdfFiles)
            throws IOException {
        File tempFile = File.createTempFile(documentName, "pdf");

        final FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(pdfBytes);
        fos.close();

        pdfFiles.add(tempFile);
    }

    public String default0DownloadReviewPdf() {
        return this.fileNameWithSuffix("pdf");
    }

    public Integer default1DownloadReviewPdf() {
        return 1;
    }

    public List<Integer> choices1DownloadReviewPdf() {
        return InvoicePageRange.firstPageChoices();
    }

    public Integer default2DownloadReviewPdf() {
        return 0;
    }

    public List<Integer> choices2DownloadReviewPdf() {
        return InvoicePageRange.lastPageChoices();
    }

    public String disableDownloadReviewPdf() {
        if (this.getLines().isEmpty()) {
            return "No payment lines";
        }
        return null;
    }

    @Action(
            semantics = SemanticsOf.SAFE
    )
    public Blob downloadReviewSummary(
            @Nullable final String documentName) {
        List<CreditTransferExportLine> exportLines = new ArrayList<>();
        int lineNumber = 1;
        for (CreditTransfer transfer : getTransfers()) {
            boolean newTransfer = true;
            for (PaymentLine paymentLine : transfer.getLines()) {
                String firstUse = creditTransferExportService.isFirstUseBankAccount(transfer) ? "YES" : "no";
                String invoiceUrl;
                try {
                    invoiceUrl = deepLinkService.deepLinkFor(paymentLine.getInvoice()).toURL().toString();
                } catch (MalformedURLException e) {
                    invoiceUrl = "";
                }
                exportLines.add(
                        new CreditTransferExportLine(
                                lineNumber,
                                lineNumber == 1 ? getDebtorBankAccount().getIban() : null,
                                newTransfer ? transfer.getSellerBankAccount().getIban() : null,
                                newTransfer ? firstUse : null,
                                newTransfer ? transfer.getSeller().getName() : null,
                                paymentLine.getInvoice().getInvoiceNumber(),
                                paymentLine.getInvoice().getGrossAmount().setScale(2, RoundingMode.HALF_UP),
                                creditTransferExportService.getApprovalStateTransitionSummary(paymentLine.getInvoice()),
                                paymentLine.getInvoice().getDescriptionSummary(),
                                paymentLine.getInvoice().getType() == IncomingInvoiceType.CAPEX ?
                                        paymentLine.getInvoice().getType().name() + " (" + paymentLine.getInvoice().getProjectSummary() + ")" :
                                        paymentLine.getInvoice().getType().name(),
                                paymentLine.getInvoice().getPropertySummary(),
                                creditTransferExportService.getInvoiceDocumentName(paymentLine.getInvoice()),
                                invoiceUrl
                        )
                );
                newTransfer = false;
                lineNumber++;
            }
        }
        String name = documentName != null ? documentName.concat(".xlsx") : fileNameWithSuffix("xlsx");
        return excelService.toExcel(
                exportLines,
                CreditTransferExportLine.class,
                getRequestedExecutionDate() != null
                        ? getRequestedExecutionDate().toString("yyyyMMdd-HHmm")
                        : "DRAFT",
                name);

    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Blob downloadExcelExport() {
        return excelService.toExcel(paymentLinesForExcelExport(), PaymentLineForExcelExportV1.class, "export", title().concat("-export.xlsx"));
    }

    @Programmatic
    public List<PaymentLineForExcelExportV1> paymentLinesForExcelExport() {
        List<PaymentLineForExcelExportV1> lineVms = new ArrayList<>();
        for (PaymentLine line : getLines()) {
            lineVms.add(new PaymentLineForExcelExportV1(
                    getDebtorBankAccount().getIban(),
                    line.getInvoice().getInvoiceDate(),
                    null,
                    line.getInvoice().getSeller().getName(),
                    line.getInvoice().getSeller().getReference(),
                    line.getInvoice().getProperty() != null ? line.getInvoice().getProperty().getReference() : null,
                    line.getInvoice().getInvoiceNumber(),
                    line.getInvoice().getId(),
                    line.getInvoice().getNetAmount().negate(),
                    line.getInvoice().getVatAmount().negate(),
                    line.getInvoice().getGrossAmount().negate()
            ));
        }
        return lineVms;
    }

    @Inject
    PdfBoxService pdfBoxService;

    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;

    @Inject
    DeepLinkService deepLinkService;

    @Inject
    PdfManipulator pdfManipulator;

    @Programmatic
    public String fileNameWithSuffix(String suffix) {
        return String.format("%s-%s.%s",
                getDebtorBankAccount().getReference(),
                (getRequestedExecutionDate() != null
                        ? getRequestedExecutionDate().toString("yyyyMMdd-HHmm")
                        : "DRAFT"),
                suffix);
    }

    //region > convertToXmlDocument
    Document convertToXmlDocument() {

        final List<CreditTransfer> transfers = getTransfers();

        Document document = new Document();
        CustomerCreditTransferInitiationV03 cstmrCdtTrfInitn = new CustomerCreditTransferInitiationV03();
        document.setCstmrCdtTrfInitn(cstmrCdtTrfInitn);

        GroupHeader32 grpHdr = new GroupHeader32();
        cstmrCdtTrfInitn.setGrpHdr(grpHdr);
        grpHdr.setMsgId(msgId());
        grpHdr.setCreDtTm(newDateTime(getCreatedOn()));
        grpHdr.setNbOfTxs("" + transfers.size());
        grpHdr.setCtrlSum(ctrlSum());
        grpHdr.setInitgPty(newPartyIdentification32ForDebtorOwner());

        List<PaymentInstructionInformation3> pmtInfList = cstmrCdtTrfInitn.getPmtInves();
        PaymentInstructionInformation3 pmtInf = new PaymentInstructionInformation3();
        pmtInfList.add(pmtInf);

        pmtInf.setPmtInfId(getId());
        pmtInf.setPmtMtd(PaymentMethod3Code.TRF);
        pmtInf.setBtchBookg(false);
        pmtInf.setReqdExctnDt(newDateTime(getRequestedExecutionDate()));
        pmtInf.setDbtr(newPartyIdentification32ForDebtorOwner());

        pmtInf.setDbtrAcct(cashAccountFor(getDebtorBankAccount()));
        pmtInf.setDbtrAgt(agentFor(getDebtorBankAccount()));

        final List<CreditTransferTransactionInformation10> cdtTrfTxInfList = pmtInf.getCdtTrfTxInves();
        cdtTrfTxInfList.addAll(transfers.stream().map(CreditTransfer::asXml).collect(Collectors.toList()));

        return document;
    }

    static CashAccount16 cashAccountFor(final BankAccount bankAccount) {
        CashAccount16 cdtrAcct = new CashAccount16();
        AccountIdentification4Choice cdtrChoice = new AccountIdentification4Choice();
        cdtrAcct.setId(cdtrChoice);
        cdtrChoice.setIBAN(bankAccount.getIban());
        return cdtrAcct;
    }

    static BranchAndFinancialInstitutionIdentification4 agentFor(final BankAccount debtorBankAccount) {
        BranchAndFinancialInstitutionIdentification4 dbtrAgt = new BranchAndFinancialInstitutionIdentification4();
        dbtrAgt.setFinInstnId(financialInstitutionIdentificationFor(debtorBankAccount));
        return dbtrAgt;
    }

    static String ctryFor(final HasAtPath hasAtPath) {
        String applicationTenancyPath = hasAtPath.getAtPath();
        if (applicationTenancyPath.startsWith("/FRA")) {
            return "FR";
        }
        if (applicationTenancyPath.startsWith("/ITA")) {
            return "IT";
        }
        if (applicationTenancyPath.startsWith("/SWE")) {
            return "SW";
        }
        if (applicationTenancyPath.startsWith("/NLD")) {
            return "NL";
        }
        if (applicationTenancyPath.startsWith("/GBR")) {
            return "GB";
        }
        return "NL";
    }

    static FinancialInstitutionIdentification7 financialInstitutionIdentificationFor(final BankAccount bankAccount) {
        FinancialInstitutionIdentification7 cdtrAgtFinInstnId = new FinancialInstitutionIdentification7();
        cdtrAgtFinInstnId.setBIC(BankAccount.trimBic(bankAccount.getBic()));
        return cdtrAgtFinInstnId;
    }

    private BigDecimal ctrlSum() {
        return Lists.newArrayList(getLines()).stream()
                .map(PaymentLine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PartyIdentification32 newPartyIdentification32ForDebtorOwner() {
        return newPartyIdentification32(getDebtorBankAccount().getOwner().getName());
    }

    private PartyIdentification32 newPartyIdentification32(final String nm) {
        PartyIdentification32 initgPty = new PartyIdentification32();
        initgPty.setNm(nm);
        return initgPty;
    }

    // 2017-05-24-0000
    private String pmtInfIdFor(final AtomicInteger seq) {
        return String.format("%s-%s", getCreatedOnYMD(), formattedSeq(seq));
    }

    private static String formattedSeq(final AtomicInteger seq) {
        return String.format("%04d", seq.getAndIncrement());
    }

    private String getCreatedOnYMD() {
        return getCreatedOn().toString("yyyy-MM-dd");
    }

    private static XMLGregorianCalendar newDateTime(final DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        try {
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            return datatypeFactory.newXMLGregorianCalendar(dateTime.toGregorianCalendar());
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private String msgId() {
        Person meAsPerson = personRepository.me();
        String userName =
                meAsPerson != null
                        ? meAsPerson.getReference()
                        : meService.me().getUsername();

        return getDebtorBankAccount().getOwner().getReference() + "-" + userName;
    }
    //endregion

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    MeService meService;

    @Inject
    PersonRepository personRepository;

    @Inject
    TitleService titleService;

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    PaymentBatchApprovalStateTransition.Repository approvalStateTransitionRepository;

    @Inject
    ExcelService excelService;

    @Inject
    CreditTransferExportService creditTransferExportService;

    @Inject
    FactoryService factoryService;

    @Inject
    PaymentBatchRepository paymentBatchRepository;

    @Inject
    WrapperFactory wrapperFactory;

}
