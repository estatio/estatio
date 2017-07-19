package org.estatio.capex.dom.payment;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
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

import org.assertj.core.util.Lists;
import org.joda.time.DateTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CommandReification;
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
import org.apache.isis.applib.services.jaxb.JaxbService;
import org.apache.isis.applib.services.linking.DeepLinkService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.pdfbox.dom.service.PdfBoxService;
import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.HasAtPath;

import org.incode.module.communications.dom.mixins.DocumentConstants;

import org.estatio.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransition;
import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.state.Stateful;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonRepository;

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
                        + "FROM org.estatio.capex.dom.payment.PaymentBatch "
                        + "WHERE debtorBankAccount == :debtorBankAccount "
                        + "   && approvalState     == :approvalState "
                        + "ORDER BY createdOn DESC "
        ),
        @Query(
                name = "findByDebtorBankAccount", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.payment.PaymentBatch "
                        + "WHERE debtorBankAccount == :debtorBankAccount "
                        + "ORDER BY createdOn DESC "
        ),
        @Query(
                name = "findByCreatedOnBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.payment.PaymentBatch "
                        + "WHERE createdOn >= :startDate "
                        + "   && createdOn <= :endDate "
                        + "ORDER BY createdOn DESC "
        ),
        @Query(
                name = "findByApprovalState", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.payment.PaymentBatch "
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
            extends org.apache.isis.applib.services.eventbus.ObjectPersistedEvent <PaymentBatch> {
    }
    public static class ObjectPersistingEvent
            extends org.apache.isis.applib.services.eventbus.ObjectPersistingEvent <PaymentBatch> {
    }

    public PaymentBatch() {
        super("createdOn, debtorBankAccount");
    }

    public PaymentBatch(
            final DateTime createdOn,
            final BankAccount debtorBankAccount,
            final PaymentBatchApprovalState approvalState){
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
    public int getNumPayments() {
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
    @Getter @Setter
    private SortedSet<PaymentLine> lines = new TreeSet<>();


    @Programmatic
    public boolean contains(final IncomingInvoice invoice) {
        return lineIfAnyFor(invoice) != null;
    }

    @Programmatic
    public void addLineIfRequired(final IncomingInvoice incomingInvoice) {
        if(!getApprovalState().isModifiable()) {
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
        for(int i = 1; i<Integer.MAX_VALUE; i++) {
            if(!usedSequences.contains(i)) {
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



    @Mixin(method="act")
    public static class removeInvoice {
        private final PaymentBatch paymentBatch;
        public removeInvoice(final PaymentBatch paymentBatch) {
            this.paymentBatch = paymentBatch;
        }
        @Action(
                semantics = SemanticsOf.IDEMPOTENT,
                command = CommandReification.DISABLED,
                publishing = Publishing.DISABLED
        )
        public PaymentBatch act(
                final List<IncomingInvoice> incomingInvoices,
                @ParameterLayout(describedAs = "Whether the removed invoices should also be rejected")
                final boolean rejectAlso,
                @ParameterLayout(describedAs = "If rejecting, then explain why so that the error can be fixed")
                @Nullable
                final String rejectionReason) {
            for (IncomingInvoice incomingInvoice : incomingInvoices) {
                paymentBatch.removeLineFor(incomingInvoice);
                if(rejectAlso) {
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
            if(reason != null) {
                return reason;
            }
            return choices0Act().isEmpty() ? "No invoices to remove" : null;
        }

        @Inject
        StateTransitionService stateTransitionService;
    }


    @Programmatic
    public void removeLineFor(final IncomingInvoice incomingInvoice) {
        Optional<PaymentLine> paymentLineIfAny = lineIfAnyFor(incomingInvoice);
        paymentLineIfAny.ifPresent(paymentLine -> getLines().remove(paymentLine));
    }


    public List<CreditTransfer> getTransfers() {

        List<CreditTransfer> transfers = Lists.newArrayList();

        final AtomicInteger seq = new AtomicInteger(1);

        final Map<BankAccount, List<PaymentLine>> lineBySeller =
                Lists.newArrayList(getLines()).stream()
                        .sorted(Comparator.comparing(PaymentLine::getSequence))
                        .collect(groupingBy(PaymentLine::getCreditorBankAccount,
                                () -> new TreeMap<BankAccount, List<PaymentLine>>(),
                                toSortedList(Comparator.comparing(PaymentLine::getSequence))));

        for (Map.Entry<BankAccount, List<PaymentLine>> linesByBankAccount : lineBySeller.entrySet()) {
            final CreditTransfer creditTransfer = new CreditTransfer();
            creditTransfer.setBatch(this);

            final BankAccount bankAccount = linesByBankAccount.getKey();
            final List<PaymentLine> lines = linesByBankAccount.getValue();

            final String sequenceNums = extractAndJoin(lines, line -> ""+line.getSequence(), "-");
            final String endToEndId = String.format("%s-%s-%s",
                                            getCreatedOnYMD(), formattedSeq(seq), sequenceNums);
            creditTransfer.setEndToEndId(endToEndId);

            creditTransfer.setSellerBankAccount(bankAccount);

            creditTransfer.setLines(lines);

            final BigDecimal amount = lines.stream()
                                            .map(PaymentLine::getAmount)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
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

    private static <T> Collector<T,?,List<T>> toSortedList(Comparator<? super T> c) {
        return Collectors.collectingAndThen(
                Collectors.toCollection(ArrayList::new), l->{ l.sort(c); return l; } );
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
        if(stateTransitionClass == PaymentBatchApprovalStateTransition.class) {
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
        if(stateTransitionClass == PaymentBatchApprovalStateTransition.class) {
            setApprovalState( (PaymentBatchApprovalState) newState );
        }
    }

    @Programmatic
    public String reasonDisabledDueToState() {
        PaymentBatchApprovalState currentState = getApprovalState();
        return currentState.isModifiable()
                ? null
                : "Cannot modify because payment batch is in state of " + currentState;
    }

    @Mixin(method="act")
    public static class downloadPaymentFile {
        private final PaymentBatch paymentBatch;
        public downloadPaymentFile(final PaymentBatch paymentBatch) {
            this.paymentBatch = paymentBatch;
        }
        @Action(semantics = SemanticsOf.SAFE)
        @ActionLayout(contributed= Contributed.AS_ACTION)
        public Clob act(final String documentName) {
            Document document = paymentBatch.convertToXmlDocument();
            String xml = jaxbService.toXml(document);
            return new Clob(documentName, "text/xml", xml);
        }
        public String disableAct() {
            return paymentBatch.getApprovalState() == PaymentBatchApprovalState.NEW
                    ? "Batch is not yet complete"
                    : null;
        }
        public String default0Act() {
            return paymentBatch.fileNameWithSuffix("xml");
        }


        @Inject
        JaxbService jaxbService;

    }

    @Mixin(method="act")
    public static class downloadReviewPdf {
        private final PaymentBatch paymentBatch;
        public downloadReviewPdf(final PaymentBatch paymentBatch) {
            this.paymentBatch = paymentBatch;
        }
        @Action(semantics = SemanticsOf.SAFE)
        @ActionLayout(contributed= Contributed.AS_ACTION)
        public Blob act(final String documentName) throws IOException {

            // TODO: prepend an overview

            final List<byte[]> pdfBytes = Lists.newArrayList();
            final List<CreditTransfer> transfers = paymentBatch.getTransfers();
            for (CreditTransfer transfer : transfers) {
                final List<PaymentLine> lines = transfer.getLines();

                for (final PaymentLine line : lines) {
                    final IncomingInvoice invoice = line.getInvoice();

                    final BankAccount bankAccount = invoice.getBankAccount();

                    final Optional<org.incode.module.document.dom.impl.docs.Document> invoiceDocIfAny =
                            lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(invoice);

                    if(invoiceDocIfAny.isPresent()) {
                        final org.incode.module.document.dom.impl.docs.Document invoiceDoc = invoiceDocIfAny.get();
                        final byte[] invoiceDocBytes = invoiceDoc.asBytes();

                        final Optional<org.incode.module.document.dom.impl.docs.Document> ibanProofDocIfAny = lookupAttachedPdfService
                                .lookupIbanProofPdfFrom(bankAccount);

                        IncomingInvoiceApprovalStateTransition transitionIfAny =
                                stateTransitionRepository.findByDomainObjectAndToState(invoice,
                                        IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR);

                        List<String> leftLines = Lists.newArrayList();
                        leftLines.add("xfer id: " + transfer.getEndToEndId() + " / " + line.getSequence());
                        if(transitionIfAny != null) {
                            Task task = transitionIfAny.getTask();
                            if (task != null) {
                                Person personAssignedTo = task.getPersonAssignedTo();
                                if (personAssignedTo != null) {
                                    leftLines.add(String.format(
                                            "approved by: %s %s",
                                            personAssignedTo.getFirstName(), personAssignedTo.getLastName()));
                                }
                            }
                            leftLines.add("approved on: " + transitionIfAny.getCompletedOn().toString("dd-MMM-yyyy HH:mm"));
                        }

                        final List<String> rightLines = Lists.newArrayList();
                        rightLines.add(String.format("debtor IBAN: %s", line.getBatch().getDebtorBankAccount().getIban()));
                        rightLines.add(String.format("crdtor IBAN: %s", line.getCreditorBankAccount().getIban()));
                        rightLines.add(String.format("gross Amt  : %s", new DecimalFormat("0.00").format(line.getAmount())));

                        boolean attachProof = false;
                        final String proof;
                        if(ibanProofDocIfAny.isPresent()) {
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
                        final byte[] firstPageInvoiceDocBytes =
                                pdfStamper.firstPageWithStampOf(invoiceDocBytes, leftLines, rightLines, uri.toString());

                        pdfBytes.add(firstPageInvoiceDocBytes);

                        if(attachProof) {
                            final org.incode.module.document.dom.impl.docs.Document ibanProofDoc = ibanProofDocIfAny.get();
                            final byte[] ibanProofBytes = ibanProofDoc.asBytes();
                            final byte[] firstPageIbanProofDocBytes = pdfStamper.firstPageOf(ibanProofBytes);
                            pdfBytes.add(firstPageIbanProofDocBytes);
                        }
                    }
                }
            }

            byte[][] mergedBytes = pdfBytes.toArray(new byte[][] {});
            byte[] pdfMergedBytes = pdfBoxService.merge(mergedBytes);
            return new Blob(documentName, DocumentConstants.MIME_TYPE_APPLICATION_PDF, pdfMergedBytes);
        }

        public String default0Act() {
            return paymentBatch.fileNameWithSuffix("pdf");
        }

        @Inject
        PdfBoxService pdfBoxService;

        @Inject
        LookupAttachedPdfService lookupAttachedPdfService;

        @Inject
        IncomingInvoiceApprovalStateTransition.Repository stateTransitionRepository;

        @Inject
        DeepLinkService deepLinkService;

        @Inject
        PdfStamper pdfStamper;
    }

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

        final SortedSet<PaymentLine> paymentLines = getLines();

        Document document = new Document();
        CustomerCreditTransferInitiationV03 cstmrCdtTrfInitn = new CustomerCreditTransferInitiationV03();
        document.setCstmrCdtTrfInitn(cstmrCdtTrfInitn);

        GroupHeader32 grpHdr = new GroupHeader32();
        cstmrCdtTrfInitn.setGrpHdr(grpHdr);
        grpHdr.setMsgId(msgId());
        grpHdr.setCreDtTm(newDateTime(getCreatedOn()));
        grpHdr.setNbOfTxs("" + paymentLines.size());
        grpHdr.setCtrlSum(ctrlSum());
        grpHdr.setInitgPty(newPartyIdentification32ForDebtorOwner());

        List<PaymentInstructionInformation3> pmtInfList = cstmrCdtTrfInitn.getPmtInves();
        PaymentInstructionInformation3 pmtInf = new PaymentInstructionInformation3();
        pmtInfList.add(pmtInf);

        final AtomicInteger seq = new AtomicInteger(0);

        pmtInf.setPmtInfId(pmtInfIdFor(seq));
        pmtInf.setPmtMtd(PaymentMethod3Code.TRF);
        pmtInf.setBtchBookg(false);
        pmtInf.setReqdExctnDt(newDateTime(getRequestedExecutionDate()));
        pmtInf.setDbtr(newPartyIdentification32ForDebtorOwner());

        pmtInf.setDbtrAcct(cashAccountFor(getDebtorBankAccount()));
        pmtInf.setDbtrAgt(agentFor(getDebtorBankAccount()));

        final List<CreditTransferTransactionInformation10> cdtTrfTxInfList = pmtInf.getCdtTrfTxInves();
        cdtTrfTxInfList.addAll(getTransfers().stream().map(CreditTransfer::asXml).collect(Collectors.toList()));

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
        if(applicationTenancyPath.startsWith("/FRA")) { return "FR"; }
        if(applicationTenancyPath.startsWith("/ITA")) { return "IT"; }
        if(applicationTenancyPath.startsWith("/SWE")) { return "SW"; }
        if(applicationTenancyPath.startsWith("/NLD")) { return "NL"; }
        if(applicationTenancyPath.startsWith("/GBR")) { return "GB"; }
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
        if(dateTime == null) {
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


}
