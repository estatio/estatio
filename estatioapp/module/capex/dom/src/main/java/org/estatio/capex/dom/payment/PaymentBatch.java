package org.estatio.capex.dom.payment;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

import org.assertj.core.util.Lists;
import org.joda.time.DateTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.services.jaxb.JaxbService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.value.Clob;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.HasAtPath;

import org.estatio.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransition;
import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.state.Stateful;
import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.asset.Property;
import org.estatio.dom.assetfinancial.FixedAssetFinancialAccount;
import org.estatio.dom.assetfinancial.FixedAssetFinancialAccountRepository;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.bankaccount.BankAccount;

import iso.std.iso._20022.tech.xsd.pain_001_001.AccountIdentification4Choice;
import iso.std.iso._20022.tech.xsd.pain_001_001.ActiveOrHistoricCurrencyAndAmount;
import iso.std.iso._20022.tech.xsd.pain_001_001.AmountType3Choice;
import iso.std.iso._20022.tech.xsd.pain_001_001.BranchAndFinancialInstitutionIdentification4;
import iso.std.iso._20022.tech.xsd.pain_001_001.CashAccount16;
import iso.std.iso._20022.tech.xsd.pain_001_001.CreditTransferTransactionInformation10;
import iso.std.iso._20022.tech.xsd.pain_001_001.CustomerCreditTransferInitiationV03;
import iso.std.iso._20022.tech.xsd.pain_001_001.Document;
import iso.std.iso._20022.tech.xsd.pain_001_001.FinancialInstitutionIdentification7;
import iso.std.iso._20022.tech.xsd.pain_001_001.GroupHeader32;
import iso.std.iso._20022.tech.xsd.pain_001_001.PartyIdentification32;
import iso.std.iso._20022.tech.xsd.pain_001_001.PaymentIdentification1;
import iso.std.iso._20022.tech.xsd.pain_001_001.PaymentInstructionInformation3;
import iso.std.iso._20022.tech.xsd.pain_001_001.PaymentMethod3Code;
import iso.std.iso._20022.tech.xsd.pain_001_001.PostalAddress6;
import iso.std.iso._20022.tech.xsd.pain_001_001.RemittanceInformation5;
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


    // TODO: derive somehow...
    // Document > PmtInf > PmtInfId

    /**
     * Document > PmtInf > DbtrAcct > Id > IBAN
     * Document > PmtInf > DbtrAgt > FinInstnId > BIC
     */
    @Title(sequence = "1")
    @Column(allowsNull = "false", name = "debtorBankAccountId")
    @Getter @Setter
    private BankAccount debtorBankAccount;


    /**
     * Document > CstmrCdtTrfInitn > GrpHdr > CreDtTm
     */
    @Title(sequence = "2", prepend = "@")
    @Column(allowsNull = "false")
    @Getter @Setter
    private DateTime createdOn;

    /**
     * Document > PmtInf > ReqdExctnDt
     */
    @Column(allowsNull = "true")
    @Getter @Setter
    private DateTime requestedExecutionDate;

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
    public boolean hasUniqueAccountToPay(final IncomingInvoice invoice) {
        Property propertyIfAny = invoice.getProperty();
        List<FinancialAccount> matchingAccounts = financialAccountsFor(propertyIfAny);
        return matchingAccounts.size() == 1;
    }

    private List<FinancialAccount> financialAccountsFor(final Property propertyIfAny) {
        if(propertyIfAny == null) {
            return Collections.emptyList();
        }
        List<FixedAssetFinancialAccount> fafrList = fixedAssetFinancialAccountRepository.findByFixedAsset(propertyIfAny);
        return fafrList.stream()
                .map(FixedAssetFinancialAccount::getFinancialAccount)
                .filter(fa -> fa == getDebtorBankAccount())
                .collect(Collectors.toList());
    }

    @Programmatic
    public boolean contains(final IncomingInvoice invoice) {
        return lineFor(invoice) != null;
    }

    @Programmatic
    public void updateFor(
            final IncomingInvoice incomingInvoice,
            final BigDecimal transferAmount,
            final String remittanceInformation) {
        PaymentLine line = lineFor(incomingInvoice);
        if (line == null) {
            final int nextSequence = getLines().size() + 1;
            line = new PaymentLine(this, nextSequence, incomingInvoice, transferAmount, remittanceInformation);
            serviceRegistry2.injectServicesInto(line);
            getLines().add(line);
        }
    }

    private PaymentLine lineFor(final IncomingInvoice invoice) {
        for (final PaymentLine line : getLines()) {
            if(line.getCreditorBankAccount() == invoice.getBankAccount()) {
                return line;
            }
        }
        return null;
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
        return currentState == PaymentBatchApprovalState.NEW ?
                null :
                "Cannot modify because payment batch is in state of " + currentState;
    }


    @Action(semantics = SemanticsOf.SAFE)
    public Clob downloadClob(String documentName) {
        Document document = convertToXmlDocument();
        String xml = jaxbService.toXml(document);
        return new Clob(documentName, "application/pdf", xml);
    }
    public String default0DownloadClob() {
        return getDebtorBankAccount().getAccountNumber();
    }

    @Programmatic
    public Document convertToXmlDocument() {

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
        grpHdr.setInitgPty(newPartyIdentification32ForEstatio());

        List<PaymentInstructionInformation3> pmtInfList = cstmrCdtTrfInitn.getPmtInves();
        PaymentInstructionInformation3 pmtInf = new PaymentInstructionInformation3();
        pmtInfList.add(pmtInf);

        final AtomicInteger seq = new AtomicInteger(0);

        pmtInf.setPmtInfId(pmtInfIdFor(seq));
        pmtInf.setPmtMtd(PaymentMethod3Code.TRF);
        pmtInf.setBtchBookg(false);
        pmtInf.setReqdExctnDt(newDateTime(getRequestedExecutionDate()));
        pmtInf.setDbtr(newPartyIdentification32ForEstatio());

        pmtInf.setDbtrAcct(cashAccountFor(getDebtorBankAccount()));
        pmtInf.setDbtrAgt(agentFor(getDebtorBankAccount()));

        List<CreditTransferTransactionInformation10> cdtTrfTxInfList = pmtInf.getCdtTrfTxInves();
        for (PaymentLine paymentLine : paymentLines) {
            CreditTransferTransactionInformation10 cdtTrfTxInf = new CreditTransferTransactionInformation10();
            cdtTrfTxInfList.add(cdtTrfTxInf);

            PaymentIdentification1 pmtId = new PaymentIdentification1();
            cdtTrfTxInf.setPmtId(pmtId);
            pmtId.setEndToEndId(endToEndId(seq, paymentLine));

            AmountType3Choice amt = new AmountType3Choice();
            cdtTrfTxInf.setAmt(amt);
            ActiveOrHistoricCurrencyAndAmount instdAmt = new ActiveOrHistoricCurrencyAndAmount();
            amt.setInstdAmt(instdAmt);
            instdAmt.setCcy("EUR");
            instdAmt.setValue(paymentLine.getAmount());

            BankAccount creditorBankAccount = paymentLine.getCreditorBankAccount();

            cdtTrfTxInf.setCdtrAgt(agentFor(creditorBankAccount));
            cdtTrfTxInf.setCdtrAcct(cashAccountFor(creditorBankAccount));

            PartyIdentification32 cdtr = new PartyIdentification32();
            cdtTrfTxInf.setCdtr(cdtr);
            cdtr.setNm(paymentLine.getCreditor().getName());
            PostalAddress6 pstlAdr = new PostalAddress6();
            cdtr.setPstlAdr(pstlAdr);
            pstlAdr.setCtry(ctryFor(paymentLine.getCreditor()));

            RemittanceInformation5 rmtInf = new RemittanceInformation5();
            cdtTrfTxInf.setRmtInf(rmtInf);
            List<String> ustrdList = rmtInf.getUstrds();
            ustrdList.add(ustrdFor(paymentLine));
        }
        return document;
    }

    //  -PM-19229-12-2016-2-RO
    //  -2017-01-04-RO
    //  -L 17-01-302-RO
    //  -FC-1702CS1-0002-RO
    //  -AF1T2017ASL-RO
    private String ustrdFor(final PaymentLine paymentLine) {
        return paymentLine.getRemittanceInformation();
    }

    private CashAccount16 cashAccountFor(final BankAccount bankAccount) {
        CashAccount16 cdtrAcct = new CashAccount16();
        AccountIdentification4Choice cdtrChoice = new AccountIdentification4Choice();
        cdtrAcct.setId(cdtrChoice);
        cdtrChoice.setIBAN(bankAccount.getIban());
        return cdtrAcct;
    }

    private BranchAndFinancialInstitutionIdentification4 agentFor(final BankAccount debtorBankAccount) {
        BranchAndFinancialInstitutionIdentification4 dbtrAgt = new BranchAndFinancialInstitutionIdentification4();
        dbtrAgt.setFinInstnId(financialInstitutionIdentificationFor(debtorBankAccount));
        return dbtrAgt;
    }

    private static String ctryFor(final HasAtPath hasAtPath) {
        String applicationTenancyPath = hasAtPath.getAtPath();
        if(applicationTenancyPath.startsWith("/FRA")) { return "FR"; }
        if(applicationTenancyPath.startsWith("/ITA")) { return "IT"; }
        if(applicationTenancyPath.startsWith("/SWE")) { return "SW"; }
        if(applicationTenancyPath.startsWith("/NLD")) { return "NL"; }
        if(applicationTenancyPath.startsWith("/GBR")) { return "GB"; }
        return "NL";
    }

    private FinancialInstitutionIdentification7 financialInstitutionIdentificationFor(final BankAccount bankAccount1) {
        FinancialInstitutionIdentification7 cdtrAgtFinInstnId = new FinancialInstitutionIdentification7();
        cdtrAgtFinInstnId.setBIC(bankAccount1.getBic());
        return cdtrAgtFinInstnId;
    }

    private BigDecimal ctrlSum() {
        return Lists.newArrayList(getLines()).stream()
                .map(PaymentLine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PartyIdentification32 newPartyIdentification32ForEstatio() {
        return newPartyIdentification32("Estatio");
    }

    private PartyIdentification32 newPartyIdentification32(final String nm) {
        PartyIdentification32 initgPty = new PartyIdentification32();
        initgPty.setNm(nm);
        return initgPty;
    }

    // 2017-05-24-0000
    String pmtInfIdFor(final AtomicInteger seq) {
        return String.format("%s-%s", getCreatedOnYMD(), formattedSeq(seq));
    }

    private static String formattedSeq(final AtomicInteger seq) {
        return String.format("%04d", seq.getAndIncrement());
    }

    private String getCreatedOnYMD() {
        return getCreatedOn().toString("yyyy-MM-dd");
    }

    // in sample XML, was : 2017-05-24-0004-2405201711100200001
    // this method instead: 2017-05-24-0004-11100200001   (if there is an attached doc)
    //                  or: 2017-05-24-0004-X123456       (if there is no attached doc; use the internal invoice Id)
    private String endToEndId(final AtomicInteger seq, final PaymentLine paymentLine) {
        IncomingInvoice invoice = paymentLine.getInvoice();
        Optional<org.incode.module.document.dom.impl.docs.Document> document = lookupAttachedPdfService
                .lookupIncomingInvoicePdfFrom(invoice);
        return document.isPresent()
                ? String.format(
                        "%s-%s-%s", getCreatedOnYMD(), formattedSeq(seq),
                        stripPdfSuffixIfAny(document.get().getName()))
                : String.format(
                        "%s-%s-X%s", getCreatedOnYMD(), formattedSeq(seq),
                invoice.getId());
    }

    private static String stripPdfSuffixIfAny(final String docName) {
        int i = docName.lastIndexOf(".pdf");
        return i > 0 ? docName.substring(0, i): docName;
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
        // TODO
        return "FRLSM-P-SCT01-FR-AA-INSTALL";
    }

    @Inject
    JaxbService jaxbService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;

    @Inject
    FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

}
