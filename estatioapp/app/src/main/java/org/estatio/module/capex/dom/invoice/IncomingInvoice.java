package org.estatio.module.capex.dom.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.validation.constraints.Digits;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PromptStyle;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.metamodel.MetaModelService2;
import org.apache.isis.applib.services.metamodel.MetaModelService3;
import org.apache.isis.applib.util.TitleBuffer;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.base.platform.applib.ReasonBuffer2;
import org.estatio.module.budget.dom.budgetitem.BudgetItem;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.capex.dom.documents.BudgetItemChooser;
import org.estatio.module.capex.dom.documents.BuyerFinder;
import org.estatio.module.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.capex.dom.order.OrderItemRepository;
import org.estatio.module.capex.dom.order.approval.OrderApprovalState;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.module.capex.dom.payment.PaymentLine;
import org.estatio.module.capex.dom.payment.PaymentLineRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.capex.dom.state.State;
import org.estatio.module.capex.dom.state.StateTransition;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.capex.dom.state.StateTransitionType;
import org.estatio.module.capex.dom.state.Stateful;
import org.estatio.module.capex.dom.util.CountryUtil;
import org.estatio.module.capex.dom.util.PeriodUtil;
import org.estatio.module.charge.dom.Applicability;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.role.PartyRoleRepository;
import org.estatio.module.tax.dom.Tax;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE
        // unused since rolled-up to superclass:
        //,schema = "dbo"
        //,table = "IncomingInvoice"
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(
        "incomingInvoice.IncomingInvoice"
)
@Queries({
        @Query(
                name = "findByApprovalState", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.invoice.IncomingInvoice "
                        + "WHERE approvalState == :approvalState "),
        @Query(
                name = "findByAtPathPrefixAndApprovalState", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.invoice.IncomingInvoice "
                        + "WHERE approvalState          == :approvalState "
                        + "   && applicationTenancyPath.startsWith(:atPathPrefix) "),
        @Query(
                name = "findByApprovalStateAndPaymentMethod", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.invoice.IncomingInvoice "
                        + "WHERE approvalState == :approvalState "
                        + "   && paymentMethod == :paymentMethod "),
        @Query(
                name = "findByAtPathPrefixAndApprovalStateAndPaymentMethod", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.invoice.IncomingInvoice "
                        + "WHERE approvalState == :approvalState "
                        + "   && paymentMethod == :paymentMethod "
                        + "   && applicationTenancyPath.startsWith(:atPathPrefix) "),
        @Query(
                name = "findByInvoiceNumber", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.invoice.IncomingInvoice "
                        + "WHERE invoiceNumber == :invoiceNumber "),
        @Query(
                name = "findByInvoiceNumberAndSeller", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.invoice.IncomingInvoice "
                        + "WHERE invoiceNumber == :invoiceNumber "
                        + " && seller == :seller "),
        @Query(
                name = "findByInvoiceNumberAndSellerAndInvoiceDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.invoice.IncomingInvoice "
                        + "WHERE invoiceNumber == :invoiceNumber "
                        + "   && seller == :seller "
                        + "   && invoiceDate == :invoiceDate "),
        @Query(
                name = "findByInvoiceDateBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.invoice.IncomingInvoice "
                        + "WHERE invoiceDate >= :fromDate "
                        + "   && invoiceDate <= :toDate "),
        @Query(
                name = "findCompletedOrLaterWithItemsByReportedDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.invoice.IncomingInvoice "
                        + "WHERE items.contains(ii) "
                        + "   && (ii.reportedDate == :reportedDate) "
                        + "   && (approvalState != 'NEW' && approvalState != 'DISCARDED') "
                        + "VARIABLES org.estatio.module.capex.dom.invoice.IncomingInvoiceItem ii "
        ),
        @Query(
                name = "findByDueDateBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.invoice.IncomingInvoice "
                        + "WHERE dueDate >= :fromDate "
                        + "   && dueDate <= :toDate "),
        @Query(
                name = "findByPropertyAndDateReceivedBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.invoice.IncomingInvoice "
                        + "WHERE property == :property "
                        + "   && dateReceived >= :fromDate "
                        + "   && dateReceived <= :toDate "),
        @Query(
                name = "findNotInAnyPaymentBatchByApprovalStateAndPaymentMethod", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.invoice.IncomingInvoice "
                        + "WHERE !(SELECT invoice "
                        + "  FROM org.estatio.module.capex.dom.payment.PaymentLine).contains(this) "
                        + "   && approvalState == :approvalState "
                        + "   && paymentMethod == :paymentMethod "
                        + "ORDER BY invoiceDate ASC " // oldest first
        ),
        @Query(
                name = "findNotInAnyPaymentBatchByAtPathPrefixAndApprovalStateAndPaymentMethod", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.invoice.IncomingInvoice "
                        + "WHERE !(SELECT invoice "
                        + "  FROM org.estatio.module.capex.dom.payment.PaymentLine).contains(this) "
                        + "   && approvalState == :approvalState "
                        + "   && paymentMethod == :paymentMethod "
                        + "   && applicationTenancyPath.startsWith(:atPathPrefix) "
                        + "ORDER BY invoiceDate ASC " // oldest first
        ),
        @Query(
                name = "findByBankAccount", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.invoice.IncomingInvoice "
                        + "WHERE bankAccount == :bankAccount "
                        + "ORDER BY invoiceDate DESC " // newest first
        ),
        @Query(
                name = "findPayableByBankTransferAndDueDateBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.invoice.IncomingInvoice "
                        + "WHERE dueDate >= :fromDueDate "
                        + "   && dueDate <= :toDueDate "
                        + "   && approvalState == 'PAYABLE' "
                        + "   && paymentMethod == 'BANK_TRANSFER'"
        )
})
@FetchGroup(
        name = "seller_buyer_property_bankAccount",
        members = {
                @Persistent(name = "seller"),
                @Persistent(name = "buyer"),
                @Persistent(name = "property"),
                @Persistent(name = "bankAccount")
        })
@Indices({
        @Index(name = "IncomingInvoice_approvalState_IDX", members = { "approvalState" }),
        @Index(name = "IncomingInvoice_atPath_approvalState_IDX", members = { "applicationTenancyPath", "approvalState" }),
        @Index(name = "IncomingInvoice_approvalState_atPath_IDX", members = { "approvalState", "applicationTenancyPath" })
})
// unused, since rolled-up
//@Unique(name = "IncomingInvoice_invoiceNumber_UNQ", members = { "invoiceNumber" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "incomingInvoice.IncomingInvoice",
        persistingLifecycleEvent = IncomingInvoice.ObjectPersistingEvent.class,
        persistedLifecycleEvent = IncomingInvoice.ObjectPersistedEvent.class,
        removingLifecycleEvent = IncomingInvoice.ObjectRemovingEvent.class
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class IncomingInvoice extends Invoice<IncomingInvoice> implements SellerBankAccountCreator, Stateful {

    public static class ApprovalInvalidatedEvent extends java.util.EventObject {
        public ApprovalInvalidatedEvent(final Object source) {
            super(source);
        }

        public IncomingInvoice getIncomingInvoice() {
            return (IncomingInvoice) getSource();
        }
    }

    public static class ObjectPersistingEvent
            extends org.apache.isis.applib.services.eventbus.ObjectPersistingEvent<IncomingInvoice> {
    }

    public static class ObjectPersistedEvent
            extends org.apache.isis.applib.services.eventbus.ObjectPersistedEvent<IncomingInvoice> {
    }

    public static class ObjectRemovingEvent
            extends org.apache.isis.applib.services.eventbus.ObjectRemovingEvent<IncomingInvoice> {
    }

    public IncomingInvoice() {
        super("seller,invoiceNumber");
    }

    public IncomingInvoice(
            final IncomingInvoiceType typeIfAny,
            final String invoiceNumber,
            final Property property,
            final String atPath,
            final Party buyer,
            final Party seller,
            final LocalDate invoiceDate,
            final LocalDate dueDate,
            final PaymentMethod paymentMethod,
            final InvoiceStatus invoiceStatus,
            final LocalDate dateReceived,
            final BankAccount bankAccount,
            final IncomingInvoiceApprovalState approvalState) {
        super("invoiceNumber");
        setType(typeIfAny);
        setInvoiceNumber(invoiceNumber);
        setProperty(property);
        setApplicationTenancyPath(atPath);
        setBuyer(buyer);
        setSeller(seller);
        setInvoiceDate(invoiceDate);
        setDueDate(dueDate);
        setPaymentMethod(paymentMethod);
        setStatus(invoiceStatus);
        setDateReceived(dateReceived);
        setBankAccount(bankAccount);
        setApprovalState(approvalState);
    }

    public String title() {
        final TitleBuffer buf = new TitleBuffer();

        buf.append(getBarcode());

        final Party seller = getSeller();
        if (seller != null) {
            buf.append(": ", seller);
        }

        final String invoiceNumber = getInvoiceNumber();
        if (invoiceNumber != null) {
            buf.append(", ", invoiceNumber);
        }

        return buf.toString();
    }

    // ////////////////////////////////////////

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public IncomingInvoice completeInvoice(
            final IncomingInvoiceType incomingInvoiceType,
            final Party seller,
            final @Nullable Boolean createRoleIfRequired,
            final @Nullable BankAccount bankAccount,
            final String invoiceNumber,
            final LocalDate dateReceived,
            final LocalDate invoiceDate,
            final LocalDate dueDate,
            final PaymentMethod paymentMethod,
            final Currency currency) {
        IncomingInvoiceType previousType = getType();
        setType(incomingInvoiceType);
        setInvoiceNumber(invoiceNumber);
        setSeller(seller);

        if (createRoleIfRequired != null && createRoleIfRequired) {
            partyRoleRepository.findOrCreate(seller, IncomingInvoiceRoleTypeEnum.SUPPLIER);
        }

        setInvoiceDate(invoiceDate);
        setDueDate(dueDate);
        setPaymentMethod(paymentMethod);
        setCurrency(currency);
        setDateReceived(dateReceived);
        setBankAccount(bankAccount);

        // if changed the type, then we need to re-evaluate the state machine
        if (previousType != incomingInvoiceType) {
            stateTransitionService.trigger(this, IncomingInvoiceApprovalStateTransition.class, null, null, null);
        }

        return this;
    }

    public String validateCompleteInvoice(
            final IncomingInvoiceType incomingInvoiceType,
            final Party seller,
            final Boolean createRoleIfRequired,
            final BankAccount bankAccount,
            final String invoiceNumber,
            final LocalDate dateReceived,
            final LocalDate invoiceDate,
            final LocalDate dueDate,
            final PaymentMethod paymentMethod,
            final Currency currency) {
        // validate seller
        final String sellerValidation = partyRoleRepository.validateThat(seller, IncomingInvoiceRoleTypeEnum.SUPPLIER);
        if ((createRoleIfRequired == null || !createRoleIfRequired) && sellerValidation != null) {
            return sellerValidation;
        }

        if (bankAccount != null && !bankAccount.getOwner().equals(seller))
            return "Bank account needs to be updated when supplier changes"; // default returns current bank account, if supplier is updated without bank account then block

        if (paymentMethod == PaymentMethod.BANK_TRANSFER && bankAccount == null)
            return "Bank account is mandatory if payment method is set to bank transfer";

        return validateChangePaymentMethod(paymentMethod);
    }

    public boolean hideCompleteInvoice() {
        if (CountryUtil.isItalian(this))
            return true;

        Optional<Document> documentIfAny = lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(this);
        return !documentIfAny.isPresent();
    }

    public String disableCompleteInvoice() {
        return reasonDisabledDueToState(this);
    }

    public IncomingInvoiceType default0CompleteInvoice() {
        return getType();
    }

    public Party default1CompleteInvoice() {
        return getSeller();
    }

    public List<Party> autoComplete1CompleteInvoice(final String search) {
        return partyRepository.autoCompleteSupplier(search, getAtPath());
    }

    public BankAccount default3CompleteInvoice(
            final IncomingInvoiceType incomingInvoiceType,
            final Party seller) {
        return getBankAccount() != null ? getBankAccount() : bankAccountRepository.getFirstBankAccountOfPartyOrNull(seller);
    }

    public List<BankAccount> choices3CompleteInvoice(
            final IncomingInvoiceType incomingInvoiceType,
            final Party seller) {
        return bankAccountRepository.findBankAccountsByOwner(seller);
    }

    public String default4CompleteInvoice() {
        return getInvoiceNumber();
    }

    public LocalDate default5CompleteInvoice() {
        return getDateReceived() == null ? dateReceivedDerivedFromDocument() : getDateReceived();
    }

    public LocalDate default6CompleteInvoice() {
        return getInvoiceDate();
    }

    public LocalDate default7CompleteInvoice() {
        return getDueDate() == null && getInvoiceDate() != null ? getInvoiceDate().plusMonths(1) : getDueDate();
    }

    public PaymentMethod default8CompleteInvoice() {
        return getPaymentMethod();
    }

    public Currency default9CompleteInvoice() {
        return getCurrency();
    }

    @Programmatic
    private LocalDate dateReceivedDerivedFromDocument() {
        Optional<Document> documentIfAny = lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(this);
        return documentIfAny.get().getCreatedAt().toLocalDate(); // guaranteed to return, hidden if none
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Complete Invoice Item", promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public IncomingInvoice completeInvoiceItemWithBudgetItem(
            final @Nullable OrderItem orderItem,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final Tax tax,
            final BigDecimal grossAmount,
            final Charge charge,
            final BudgetItem budgetItem,
            final String period) {
        return completeInvoiceItem(orderItem, description, netAmount, vatAmount, tax, grossAmount, charge, null, budgetItem, period);
    }

    public OrderItem default0CompleteInvoiceItemWithBudgetItem() {
        return defaultOrderItemCompleteInvoiceItem();
    }

    public List<OrderItem> choices0CompleteInvoiceItemWithBudgetItem() {
        return choicesOrderItemCompleteInvoiceItem(getSeller());
    }

    public String default1CompleteInvoiceItemWithBudgetItem() {
        return defaultDescriptionCompleteInvoiceItem();
    }

    public BigDecimal default2CompleteInvoiceItemWithBudgetItem() {
        return defaultNetAmountCompleteInvoiceItem();
    }

    public BigDecimal default3CompleteInvoiceItemWithBudgetItem() {
        return defaultVatAmountCompleteInvoiceItem();
    }

    public Tax default4CompleteInvoiceItemWithBudgetItem() {
        return defaultTaxCompleteInvoiceItem();
    }

    public BigDecimal default5CompleteInvoiceItemWithBudgetItem() {
        return defaultGrossAmountCompleteInvoiceItem();
    }

    public Charge default6CompleteInvoiceItemWithBudgetItem() {
        return defaultChargeCompleteInvoiceItem();
    }

    public List<Charge> autoComplete6CompleteInvoiceItemWithBudgetItem(@MinLength(value = 3) String search) {
        return autoCompleteChargeCompleteInvoiceItem(search);
    }

    public BudgetItem default7CompleteInvoiceItemWithBudgetItem() {
        return defaultOrderItemCompleteInvoiceItem() == null ? null : defaultOrderItemCompleteInvoiceItem().getBudgetItem();
    }

    public List<BudgetItem> choices7CompleteInvoiceItemWithBudgetItem(
            final OrderItem orderItem,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final Tax tax,
            final BigDecimal grossAmount,
            final Charge charge) {
        return budgetItemChooser.choicesBudgetItemFor(getProperty(), charge);
    }

    public String default8CompleteInvoiceItemWithBudgetItem() {
        return defaultPeriodCompleteInvoiceItem();
    }

    public boolean hideCompleteInvoiceItemWithBudgetItem() {
        if (CountryUtil.isItalian(this))
            return true;

        return getType() != IncomingInvoiceType.SERVICE_CHARGES && getType() != IncomingInvoiceType.ITA_RECOVERABLE;
    }

    public String disableCompleteInvoiceItemWithBudgetItem() {
        return disableCompleteInvoiceItemProgrammatic();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Complete Invoice Item", promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public IncomingInvoice completeInvoiceItemWithProject(
            final @Nullable OrderItem orderItem,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final Tax tax,
            final BigDecimal grossAmount,
            final Charge charge,
            final Project project,
            final String period) {
        return completeInvoiceItem(orderItem, description, netAmount, vatAmount, tax, grossAmount, charge, project, null, period);
    }

    public OrderItem default0CompleteInvoiceItemWithProject() {
        return defaultOrderItemCompleteInvoiceItem();
    }

    public List<OrderItem> choices0CompleteInvoiceItemWithProject() {
        return choicesOrderItemCompleteInvoiceItem(getSeller());
    }

    public String default1CompleteInvoiceItemWithProject() {
        return defaultDescriptionCompleteInvoiceItem();
    }

    public BigDecimal default2CompleteInvoiceItemWithProject() {
        return defaultNetAmountCompleteInvoiceItem();
    }

    public BigDecimal default3CompleteInvoiceItemWithProject() {
        return defaultVatAmountCompleteInvoiceItem();
    }

    public Tax default4CompleteInvoiceItemWithProject() {
        return defaultTaxCompleteInvoiceItem();
    }

    public BigDecimal default5CompleteInvoiceItemWithProject() {
        return defaultGrossAmountCompleteInvoiceItem();
    }

    public Charge default6CompleteInvoiceItemWithProject() {
        return defaultChargeCompleteInvoiceItem();
    }

    public List<Charge> autoComplete6CompleteInvoiceItemWithProject(@MinLength(value = 3) String search) {
        return autoCompleteChargeCompleteInvoiceItem(search);
    }

    public Project default7CompleteInvoiceItemWithProject() {
        return defaultOrderItemCompleteInvoiceItem() == null ? null : defaultOrderItemCompleteInvoiceItem().getProject();
    }

    public List<Project> choices7CompleteInvoiceItemWithProject(
            final OrderItem orderItem,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final Tax tax,
            final BigDecimal grossAmount,
            final Charge charge,
            final Project project,
            final String period) {
        return getProperty() == null ?
                projectRepository.listAll()
                : projectRepository.findByFixedAsset(getProperty())
                .stream()
                .filter(x -> !x.isParentProject())
                .filter(x -> x.getEndDate() == null || !x.getEndDate().isBefore(PeriodUtil.endDateFromPeriod(period) != null ? PeriodUtil.endDateFromPeriod(period) : LocalDate.now()))
                .collect(Collectors.toList());
    }

    public String default8CompleteInvoiceItemWithProject() {
        return defaultPeriodCompleteInvoiceItem();
    }

    public boolean hideCompleteInvoiceItemWithProject() {
        if (CountryUtil.isItalian(this))
            return true;

        return getType() != IncomingInvoiceType.CAPEX;
    }

    public String disableCompleteInvoiceItemWithProject() {
        return disableCompleteInvoiceItemProgrammatic();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Complete Invoice Item", promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public IncomingInvoice completeInvoiceItem(
            final @Nullable OrderItem orderItem,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final Tax tax,
            final BigDecimal grossAmount,
            final Charge charge,
            final String period) {
        return completeInvoiceItem(orderItem, description, netAmount, vatAmount, tax, grossAmount, charge, null, null, period);
    }

    public OrderItem default0CompleteInvoiceItem() {
        return defaultOrderItemCompleteInvoiceItem();
    }

    public List<OrderItem> choices0CompleteInvoiceItem() {
        return choicesOrderItemCompleteInvoiceItem(getSeller());
    }

    public String default1CompleteInvoiceItem() {
        return defaultDescriptionCompleteInvoiceItem();
    }

    public BigDecimal default2CompleteInvoiceItem() {
        return defaultNetAmountCompleteInvoiceItem();
    }

    public BigDecimal default3CompleteInvoiceItem() {
        return defaultVatAmountCompleteInvoiceItem();
    }

    public Tax default4CompleteInvoiceItem() {
        return defaultTaxCompleteInvoiceItem();
    }

    public BigDecimal default5CompleteInvoiceItem() {
        return defaultGrossAmountCompleteInvoiceItem();
    }

    public Charge default6CompleteInvoiceItem() {
        return defaultChargeCompleteInvoiceItem();
    }

    public List<Charge> autoComplete6CompleteInvoiceItem(@MinLength(value = 3) String search) {
        return autoCompleteChargeCompleteInvoiceItem(search);
    }

    public String default7CompleteInvoiceItem() {
        return defaultPeriodCompleteInvoiceItem();
    }

    public boolean hideCompleteInvoiceItem() {
        if (CountryUtil.isItalian(this))
            return true;

        return getType() == IncomingInvoiceType.SERVICE_CHARGES || getType() == IncomingInvoiceType.ITA_RECOVERABLE || getType() == IncomingInvoiceType.CAPEX;
    }

    public String disableCompleteInvoiceItem() {
        return disableCompleteInvoiceItemProgrammatic();
    }

    // TODO: in switched view amounts are determined based on tax/other amounts. How to achieve this in a single action?
    @Programmatic
    private IncomingInvoice completeInvoiceItem(
            final OrderItem orderItem,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final Tax tax,
            final BigDecimal grossAmount,
            final Charge charge,
            final Project project,
            final BudgetItem budgetItem,
            final String period) {
        // upsert invoice item
        // this will also update the parent header's property with that from the first item
        Optional<IncomingInvoiceItem> firstItemIfAny = getFirstItemIfAny();
        IncomingInvoiceItem firstItem;

        if (firstItemIfAny.isPresent()) {
            IncomingInvoiceItem item = firstItemIfAny.get();
            item.setIncomingInvoiceType(getType());
            item.setCharge(charge);
            item.setDescription(description);
            item.setNetAmount(netAmount);
            item.setVatAmount(vatAmount);
            item.setGrossAmount(grossAmount);
            item.setTax(tax);
            item.setStartDate(PeriodUtil.startDateFromPeriod(period));
            item.setEndDate(PeriodUtil.endDateFromPeriod(period));
            item.setFixedAsset(getProperty());
            item.setProject(project);
            item.setBudgetItem(budgetItem);

            firstItem = item;
        } else {
            firstItem = addItemToThis(getType(), charge, description, netAmount, vatAmount, grossAmount, tax, getDueDate(), period, getProperty(), project, budgetItem);
        }

        if (orderItem != null) {
            Order order = orderItem.getOrdr();
            Charge chargeFromWrapper = orderItem.getCharge();
            OrderItem orderItemToLink = orderItemRepository.findUnique(order, chargeFromWrapper, 0);
            orderItemInvoiceItemLinkRepository.findOrCreateLink(orderItemToLink, firstItem, firstItem.getNetAmount());
        } else {
            // remove all (or the one and only) link.
            final Optional<OrderItemInvoiceItemLink> links = orderItemInvoiceItemLinkRepository.findByInvoiceItem(firstItem);
            links.ifPresent(OrderItemInvoiceItemLink::remove);
        }

        // also set amounts on invoice
        this.setNetAmount(netAmount);
        this.setGrossAmount(grossAmount);

        return this;
    }

    @Programmatic
    private String disableCompleteInvoiceItemProgrammatic() {
        final List<IncomingInvoiceItem> items = getItems().stream()
                .filter(IncomingInvoiceItem.class::isInstance)
                .map(IncomingInvoiceItem.class::cast)
                .collect(Collectors.toList());

        switch (items.size()) {
            case 0:
            case 1:
                return null;
            default:
                return "Can only complete invoice item for invoices with a single item";
        }
    }

    @Programmatic
    private OrderItem defaultOrderItemCompleteInvoiceItem() {
        final List<OrderItemInvoiceItemLink> linksForInvoice = orderItemInvoiceItemLinkRepository.findByInvoice(this);
        return linksForInvoice.isEmpty() ? null : linksForInvoice.get(0).getOrderItem();
    }

    @Programmatic
    private List<OrderItem> choicesOrderItemCompleteInvoiceItem(final Party seller) {
        final org.estatio.module.asset.dom.Property property = getProperty();
        final List<OrderItem> orderItems;
        if (property == null) {
            orderItems = orderItemRepository.findBySeller(seller);
        } else {
            orderItems = orderItemRepository.findBySellerAndProperty(seller, property);
        }

        orderItemInvoiceItemLinkRepository.findByInvoice(this)
                .stream()
                .map(OrderItemInvoiceItemLink::getOrderItem)
                .filter(oi -> !orderItems.contains(oi))
                .forEach(orderItems::add);

        return orderItems
                .stream()
                .filter(x -> x.getOrdr().getApprovalState() == null || x.getOrdr().getApprovalState() != OrderApprovalState.DISCARDED)
                .collect(Collectors.toList());
    }

    @Programmatic
    private String defaultDescriptionCompleteInvoiceItem() {
        return defaultOrderItemCompleteInvoiceItem() == null ? null : defaultOrderItemCompleteInvoiceItem().getDescription();
    }

    @Programmatic
    private BigDecimal defaultNetAmountCompleteInvoiceItem() {
        return defaultOrderItemCompleteInvoiceItem() == null ? null : defaultOrderItemCompleteInvoiceItem().getNetAmount();
    }

    @Programmatic
    private BigDecimal defaultVatAmountCompleteInvoiceItem() {
        return defaultOrderItemCompleteInvoiceItem() == null ? null : defaultOrderItemCompleteInvoiceItem().getVatAmount();
    }

    @Programmatic
    private Tax defaultTaxCompleteInvoiceItem() {
        return defaultOrderItemCompleteInvoiceItem() == null ? null : defaultOrderItemCompleteInvoiceItem().getTax();
    }

    @Programmatic
    private BigDecimal defaultGrossAmountCompleteInvoiceItem() {
        return defaultOrderItemCompleteInvoiceItem() == null ? null : defaultOrderItemCompleteInvoiceItem().getGrossAmount();
    }

    @Programmatic
    private Charge defaultChargeCompleteInvoiceItem() {
        return defaultOrderItemCompleteInvoiceItem() == null ? null : defaultOrderItemCompleteInvoiceItem().getCharge();
    }

    @Programmatic
    private List<Charge> autoCompleteChargeCompleteInvoiceItem(final String search) {
        return chargeRepository.findByApplicabilityAndMatchOnReferenceOrName(search, Applicability.INCOMING);
    }

    @Programmatic
    private String defaultPeriodCompleteInvoiceItem() {
        return defaultOrderItemCompleteInvoiceItem() == null ? null : defaultOrderItemCompleteInvoiceItem().getPeriod();
    }

    @Programmatic
    private Optional<IncomingInvoiceItem> getFirstItemIfAny() {
        SortedSet<InvoiceItem> items = getItems();
        return Lists.newArrayList(items).stream()
                .filter(IncomingInvoiceItem.class::isInstance)
                .map(IncomingInvoiceItem.class::cast)
                .findFirst();
    }

    // ////////////////////////////////////////

    @MemberOrder(name = "items", sequence = "1")
    public IncomingInvoice addItem(
            final IncomingInvoiceType type,
            final Charge charge,
            final String description,
            @Digits(integer = 13, fraction = 2) final BigDecimal netAmount,
            @Nullable
            @Digits(integer = 13, fraction = 2) final BigDecimal vatAmount,
            @Digits(integer = 13, fraction = 2) final BigDecimal grossAmount,
            @Nullable final Tax tax,
            @Nullable final LocalDate dueDate,
            @Nullable final String period,
            @Nullable final Property property,
            @Nullable final Project project,
            @Nullable final BudgetItem budgetItem) {

        addItemToThis(
                type, charge, description, netAmount, vatAmount, grossAmount, tax, dueDate,
                period, property, project, budgetItem);
        return this;
    }

    public String disableAddItem() {
        final ReasonBuffer2 buf = ReasonBuffer2.forSingle("Cannot add item because");

        buf.append(amountsCoveredByAmountsItems(), "invoice amounts are covered");
        final Object viewContext = this;
        reasonDisabledDueToApprovalStateIfAny(viewContext, buf);

        return buf.getReason();
    }

    public IncomingInvoiceType default0AddItem() {
        return getType();
    }

    public LocalDate default7AddItem() {
        return ofFirstItem(IncomingInvoiceItem::getDueDate);
    }

    public String default8AddItem() {
        return ofFirstItem(IncomingInvoiceItem::getStartDate) != null ? PeriodUtil.periodFromInterval(new LocalDateInterval(ofFirstItem(IncomingInvoiceItem::getStartDate), ofFirstItem(IncomingInvoiceItem::getEndDate))) : null;
    }

    public Property default9AddItem() {
        return getProperty();
    }

    public Project default10AddItem() {
        return ofFirstItem(IncomingInvoiceItem::getProject);
    }

    public List<Charge> choices1AddItem() {
        return chargeRepository.allIncoming();
    }

    public List<BudgetItem> choices11AddItem(
            final IncomingInvoiceType type,
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate dueDate,
            final String period,
            final Property property,
            final Project project,
            final BudgetItem budgetItem) {

        return budgetItemChooser.choicesBudgetItemFor(property, charge);
    }

    public String validateAddItem(
            final IncomingInvoiceType type,
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate dueDate,
            final String period,
            final Property property,
            final Project project,
            final BudgetItem budgetItem) {
        if (period == null)
            return null; // period is optional
        return PeriodUtil.reasonInvalidPeriod(period);
    }

    boolean amountsCoveredByAmountsItems() {
        if ((getNetAmount() != null && getTotalNetAmount().compareTo(getNetAmount()) >= 0)
                || (getGrossAmount() != null && getTotalGrossAmount().compareTo(getGrossAmount()) >= 0)
        ) {
            return true;
        }

        return false;

    }

    @Action
    @MemberOrder(name = "items", sequence = "4")
    public IncomingInvoice reverseItem(final IncomingInvoiceItem itemToReverse) {

        final IncomingInvoiceItem reversal = copyWithLinks(itemToReverse, Sort.REVERSAL);
        final IncomingInvoiceItem correction = copyWithLinks(itemToReverse, Sort.CORRECTION);

        return this;
    }

    @Programmatic
    public void reverseReportedItemsNoCorrection() {
        for (IncomingInvoiceItem itemToReverse : itemsToReverse()) {
            copyWithLinks(itemToReverse, Sort.REVERSAL);
        }
    }

    @Programmatic
    public List<IncomingInvoiceItem> itemsToReverse() {
        return reportedItemsIgnoringReversals();
    }

    @Programmatic
    public List<IncomingInvoiceItem> reportedItemsIgnoringReversals() {
        return Lists.newArrayList(getItems()).stream().
                filter(IncomingInvoiceItem.class::isInstance)
                .map(IncomingInvoiceItem.class::cast)
                .filter(x -> x.isReported())
                .filter(x -> !x.isReversal())
                .filter(x -> !reversedItems().contains(x)) // also ignore reported items that are already reversed
                .collect(Collectors.toList());
    }

    List<IncomingInvoiceItem> reversedItems() {
        return reversals().stream().map(x -> x.getReversalOf()).collect(Collectors.toList());
    }

    List<IncomingInvoiceItem> reversals() {
        return Lists.newArrayList(getItems()).stream().
                filter(IncomingInvoiceItem.class::isInstance)
                .map(IncomingInvoiceItem.class::cast)
                .filter(x -> x.isReversal())
                .collect(Collectors.toList());
    }

    @Programmatic
    public List<IncomingInvoiceItem> unreportedItemsIgnoringReversals() {
        return Lists.newArrayList(getItems()).stream().
                filter(IncomingInvoiceItem.class::isInstance)
                .map(IncomingInvoiceItem.class::cast)
                .filter(x -> !x.isReported())
                .filter(x -> !x.isReversal())
                .collect(Collectors.toList());
    }

    enum Sort {
        REVERSAL {
            @Override
            BigDecimal adjust(final BigDecimal amount) {
                return Sort.negate(amount);
            }
        },
        CORRECTION {
            @Override
            BigDecimal adjust(final BigDecimal amount) {
                return amount;
            }
        };

        String prefixTo(String description) {
            return name() + " of " + description;
        }

        ;

        abstract BigDecimal adjust(BigDecimal amount);

        private static BigDecimal negate(@Nullable final BigDecimal amount) {
            return amount == null ? amount : BigDecimal.ZERO.subtract(amount);
        }
    }

    private IncomingInvoiceItem copyWithLinks(
            final IncomingInvoiceItem itemToReverse,
            final Sort sort) {

        final IncomingInvoiceType type = itemToReverse.getIncomingInvoiceType();
        final String description = itemToReverse.getDescription();
        final Charge charge = itemToReverse.getCharge();
        final BigDecimal netAmount = itemToReverse.getNetAmount();

        final BigDecimal vatAmount = itemToReverse.getVatAmount();
        final BigDecimal grossAmount = itemToReverse.getGrossAmount();
        final Tax tax = itemToReverse.getTax();
        final LocalDate dueDate = itemToReverse.getDueDate();
        final String period = itemToReverse.getPeriod();

        final FixedAsset fixedAsset = itemToReverse.getFixedAsset();
        final Project project = itemToReverse.getProject();
        final BudgetItem budgetItem = itemToReverse.getBudgetItem();

        final IncomingInvoiceItem copyItem = addItemToThis(
                type, charge,
                sort.prefixTo(description),
                sort.adjust(netAmount),
                sort.adjust(vatAmount),
                sort.adjust(grossAmount),
                tax, dueDate,
                period, fixedAsset, project, budgetItem);

        if (sort == Sort.REVERSAL) {
            copyItem.setReversalOf(itemToReverse);
        }

        final Optional<OrderItemInvoiceItemLink> linkIfAny =
                orderItemInvoiceItemLinkRepository.findByInvoiceItem(itemToReverse);

        linkIfAny.ifPresent(link -> {
            orderItemInvoiceItemLinkRepository.createLink(
                    link.getOrderItem(), copyItem, sort.adjust(link.getNetAmount()));
        });

        return copyItem;
    }

    public String disableReverseItem() {
        ReasonBuffer2 buf = ReasonBuffer2.forAll("Invoice item cannot be reversed because");

        final IncomingInvoice viewContext = this;
        reasonDisabledDueToApprovalStateIfAny(viewContext, buf);

        buf.append(choices0ReverseItem().isEmpty(), "no items to reverse");

        return buf.getReason();
    }

    public IncomingInvoiceItem default0ReverseItem() {
        final List<IncomingInvoiceItem> choices = choices0ReverseItem();
        return choices.size() == 1 ? choices.get(0) : null;
    }

    public List<IncomingInvoiceItem> choices0ReverseItem() {
        return itemsToReverse();
    }

    @MemberOrder(name = "items", sequence = "2")
    public IncomingInvoice splitItem(
            final IncomingInvoiceItem itemToSplit,
            final String newItemDescription,
            @Digits(integer = 13, fraction = 2) final BigDecimal newItemNetAmount,
            @Nullable
            @Digits(integer = 13, fraction = 2) final BigDecimal newItemVatAmount,
            @Nullable final Tax newItemtax,
            @Digits(integer = 13, fraction = 2) final BigDecimal newItemGrossAmount,
            final Charge newItemCharge,
            @Nullable final Property newItemProperty,
            @Nullable final Project newItemProject,
            @Nullable final BudgetItem newItemBudgetItem,
            final String newItemPeriod
    ) {
        itemToSplit.subtractAmounts(newItemNetAmount, newItemVatAmount, newItemGrossAmount);
        addItemToThis(getType(), newItemCharge, newItemDescription, newItemNetAmount,
                newItemVatAmount, newItemGrossAmount, newItemtax, getDueDate(), newItemPeriod, newItemProperty,
                newItemProject, newItemBudgetItem);
        return this;
    }

    public String disableSplitItem() {

        ReasonBuffer2 buf = ReasonBuffer2.forSingle("Cannot split items because");
        reasonDisabledDueToApprovalStateIfAny(this, buf);
        buf.append(() -> choices0SplitItem().isEmpty(), "there are no items");
        return buf.getReason();
    }

    public IncomingInvoiceItem default0SplitItem() {
        final List<IncomingInvoiceItem> items = choices0SplitItem();
        return items.isEmpty() ? null : items.get(0);
    }

    private Optional<IncomingInvoiceItem> optional0SplitItem() {
        return Optional.ofNullable(default0SplitItem());
    }

    public Tax default4SplitItem() {
        return optional0SplitItem().map(IncomingInvoiceItem::getTax).orElse(null);
    }

    public Charge default6SplitItem() {
        return optional0SplitItem().map(IncomingInvoiceItem::getCharge).orElse(null);
    }

    public Property default7SplitItem() {
        return getProperty();
    }

    public Project default8SplitItem() {
        return optional0SplitItem().map(IncomingInvoiceItem::getProject).orElse(null);
    }

    public BudgetItem default9SplitItem() {
        return optional0SplitItem().map(IncomingInvoiceItem::getBudgetItem).orElse(null);
    }

    public String default10SplitItem() {
        return optional0SplitItem().map(IncomingInvoiceItem::getPeriod).orElse(null);
    }

    public List<IncomingInvoiceItem> choices0SplitItem() {
        return Lists.newArrayList(getItems()).stream()
                .map(IncomingInvoiceItem.class::cast)
                .filter(IncomingInvoiceItem::neitherReversalNorReported)
                .collect(Collectors.toList());
    }

    public List<Charge> choices6SplitItem() {
        return chargeRepository.allIncoming();
    }

    public List<BudgetItem> choices9SplitItem(
            final IncomingInvoiceItem item,
            final String newItemDescription,
            final BigDecimal newItemNetAmount,
            final BigDecimal newItemVatAmount,
            final Tax newItemtax,
            final BigDecimal newItemGrossAmount,
            final Charge newItemCharge,
            final Property newItemProperty,
            final Project newItemProject,
            final BudgetItem newItemBudgetItem,
            final String newItemPeriod) {

        return budgetItemChooser.choicesBudgetItemFor(newItemProperty, newItemCharge);
    }

    public String validateSplitItem(
            final IncomingInvoiceItem item,
            final String newItemDescription,
            final BigDecimal newItemNetAmount,
            final BigDecimal newItemVatAmount,
            final Tax newItemtax,
            final BigDecimal newItemGrossAmount,
            final Charge newItemCharge,
            final Property newItemProperty,
            final Project newItemProject,
            final BudgetItem newItemBudgetItem,
            final String newItemPeriod) {
        return PeriodUtil.reasonInvalidPeriod(newItemPeriod);
    }

    @Programmatic
    public <T> T ofFirstItem(final Function<IncomingInvoiceItem, T> f) {
        final Optional<IncomingInvoiceItem> firstItemIfAny = firstItemIfAny();
        return firstItemIfAny.map(f).orElse(null);
    }

    @Programmatic
    public Optional<IncomingInvoiceItem> firstItemIfAny() {
        return Lists.newArrayList(getItems()).stream()
                .filter(IncomingInvoiceItem.class::isInstance)
                .map(IncomingInvoiceItem.class::cast)
                .findFirst();
    }

    @MemberOrder(name = "items", sequence = "3")
    public IncomingInvoice mergeItems(
            final IncomingInvoiceItem item,
            final IncomingInvoiceItem mergeInto) {
        incomingInvoiceItemRepository.mergeItems(item, mergeInto);
        return this;
    }

    public String disableMergeItems() {
        final ReasonBuffer2 buf = ReasonBuffer2.forSingle("Cannot merge items because");

        final Object viewContext = this;
        reasonDisabledDueToApprovalStateIfAny(viewContext, buf);

        buf.append(() -> getItems().size() < 2, "merging needs 2 or more items");
        return buf.getReason();
    }

    public IncomingInvoiceItem default0MergeItems() {
        return firstItemIfAny() != null ? (IncomingInvoiceItem) getItems().last() : null;
    }

    public IncomingInvoiceItem default1MergeItems() {
        return firstItemIfAny() != null ? (IncomingInvoiceItem) getItems().first() : null;
    }

    public List<IncomingInvoiceItem> choices0MergeItems() {
        return Lists.newArrayList(getItems()).stream()
                .map(IncomingInvoiceItem.class::cast)
                .filter(IncomingInvoiceItem::neitherReversalNorReported)
                .collect(Collectors.toList());
    }

    public List<IncomingInvoiceItem> choices1MergeItems(final IncomingInvoiceItem item) {
        return Lists.newArrayList(getItems()).stream()
                .map(IncomingInvoiceItem.class::cast)
                .filter(IncomingInvoiceItem::neitherReversalNorReported)
                .filter(x -> !x.equals(item))
                .collect(Collectors.toList());
    }

    private IncomingInvoiceItem addItemToThis(
            final IncomingInvoiceType type,
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate dueDate,
            final String period,
            final FixedAsset<?> fixedAsset,
            final Project project,
            final BudgetItem budgetItem) {
        return incomingInvoiceItemRepository.addItem(
                this,
                type,
                charge,
                description,
                netAmount,
                vatAmount,
                grossAmount,
                tax,
                dueDate,
                period,
                fixedAsset,
                project,
                budgetItem);
    }

    /**
     * TODO: inline this mixin.
     */
    @Mixin(method = "act")
    public static class changeBankAccount {

        private final IncomingInvoice incomingInvoice;

        public changeBankAccount(final IncomingInvoice incomingInvoice) {
            this.incomingInvoice = incomingInvoice;
        }

        @Action(semantics = SemanticsOf.IDEMPOTENT)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public IncomingInvoice act(final BankAccount bankAccount) {
            incomingInvoice.setBankAccount(bankAccount);
            return incomingInvoice;
        }

        public String disableAct() {

            final Object viewContext = incomingInvoice;
            final String reasonIfAny = incomingInvoice.reasonDisabledFinanceDetailsDueToState(viewContext);
            if (reasonIfAny != null) {
                return reasonIfAny;
            }

            if (incomingInvoice.getSeller() == null) {
                return "Require seller in order to list available bank accounts";
            }

            final List<BankAccount> bankAccountsForSeller = choices0Act();
            if (bankAccountsForSeller.isEmpty()) {
                return "No bank accounts available for seller";
            }

            // if here then enabled
            return null;
        }

        public List<BankAccount> choices0Act() {
            return bankAccountRepository.findBankAccountsByOwner(incomingInvoice.getSeller());
        }

        public BankAccount default0Act() {
            return incomingInvoice.getBankAccount();
        }

        /**
         * An alternative design would be to filter out all non-verified bank accounts in the choicesXxx, but that
         * could be confusing to the end-user (wondering why some bank accounts of the seller aren't listed).
         */
        public String validate0Act(final BankAccount bankAccount) {
            // a mutable invoice does not need a verified bankaccount
            if (!incomingInvoice.isImmutableDueToState())
                return null;

            final BankAccountVerificationState state = stateTransitionService
                    .currentStateOf(bankAccount, BankAccountVerificationStateTransition.class);
            return state != BankAccountVerificationState.VERIFIED ? "Bank account must be verified" : null;
        }

        @Inject
        BankAccountRepository bankAccountRepository;

        @Inject
        StateTransitionService stateTransitionService;

    }

    /**
     * Default type, used for routing.
     *
     * <p>
     * This can be overridden for each invoice item.
     * </p>
     */

    @Getter @Setter
    @Column(allowsNull = "true")
    private IncomingInvoiceType type;

    public void setType(final IncomingInvoiceType type) {
        this.type = invalidateApprovalIfDiffer(this.type, type);
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public IncomingInvoice editType(
            final IncomingInvoiceType type,
            final boolean changeOnItemsAsWell) {
        if (changeOnItemsAsWell) {
            if (isReported()) {
                reverseReportedItems();
            }
            setTypeOnUnreportedItems(type);
        }
        setType(type);
        return this;
    }

    void reverseReportedItems() {
        for (IncomingInvoiceItem item : itemsToReverse()) {
            reverseItem(item);
        }
    }

    void setTypeOnUnreportedItems(final IncomingInvoiceType type) {
        for (IncomingInvoiceItem item : unreportedItemsIgnoringReversals()) {
            item.setIncomingInvoiceType(type);
        }
    }

    public IncomingInvoiceType default0EditType() {
        return getType();
    }

    public boolean default1EditType() {
        return true;
    }

    public String disableEditType() {
        return reasonDisabledDueToStateStrict();
    }

    /**
     * This relates to the owning property, while the child items may either also relate to the property,
     * or could potentially relate to individual units within the property.
     *
     * <p>
     * Note that InvoiceForLease also has a reference to FixedAsset.  It's not possible to move this
     * up to the Invoice superclass because invoicing module does not "know" about fixed assets.
     * </p>
     */
    @javax.jdo.annotations.Column(name = "propertyId", allowsNull = "true")
    @org.apache.isis.applib.annotation.Property(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private Property property;

    public void setProperty(final Property property) {
        this.property = invalidateApprovalIfDiffer(this.property, property);
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public IncomingInvoice editProperty(
            @Nullable final Property property,
            final boolean changeOnItemsAsWell) {
        setProperty(property);
        if (changeOnItemsAsWell) {
            Lists.newArrayList(getItems())  // eagerly load (DN 4.x collections do not support streaming)
                    .stream()
                    .map(IncomingInvoiceItem.class::cast)
                    .forEach(x -> x.setFixedAsset(property));
        }
        return this;
    }

    public Property default0EditProperty() {
        return getProperty();
    }

    public boolean default1EditProperty() {
        return true;
    }

    public String disableEditProperty() {
        return CountryUtil.isItalian(this) ? "Editing is disabled" : "Property can only be edited by recategorising invoice";
    }

    /**
     * Unlike pretty much every other property, changing the {@link #setBankAccount(BankAccount)} does _not_ cause the
     * {@link #isApprovedFully() approvedFully} flag to be reset.
     * Thus, if no other changes are made, then completing the invoice will send it straight through without requiring re-approval.
     */
    @Getter @Setter
    @Column(allowsNull = "true", name = "bankAccountId")
    private BankAccount bankAccount;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate dateReceived;

    public void setDateReceived(final LocalDate dateReceived) {
        this.dateReceived = invalidateApprovalIfDiffer(this.dateReceived, dateReceived);
    }

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate vatRegistrationDate;

    public boolean hideVatRegistrationDate() {
        return !this.getAtPath().startsWith("/ITA");
    }

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(hidden = Where.EVERYWHERE)
    public LocalDate paidDate;

    @Getter @Setter
    @Column(allowsNull = "true")
    @PropertyLayout(hidden = Where.EVERYWHERE)
    private boolean postedToCodaBooks;

    // TODO: does not seem to be used, raised EST-1599 to look into removing it.
    @Getter @Setter
    @Column(allowsNull = "true", name = "invoiceId")
    private IncomingInvoice relatesTo;

    // TODO: need to remove this from superclass, ie push down to InvoiceForLease subclass so not in this subtype
    @org.apache.isis.applib.annotation.Property(hidden = Where.EVERYWHERE)
    @Override
    public InvoiceStatus getStatus() {
        return super.getStatus();
    }

    @Override
    public void setCurrency(final Currency currency) {
        super.setCurrency(invalidateApprovalIfDiffer(getCurrency(), currency));
    }

    @org.apache.isis.applib.annotation.PropertyLayout(named = "ECP (as buyer)")
    @Override
    public Party getBuyer() {
        return super.getBuyer();
    }

    @Override
    public void setBuyer(final Party buyer) {
        super.setBuyer(invalidateApprovalIfDiffer(super.getBuyer(), buyer));
    }

    @org.apache.isis.applib.annotation.PropertyLayout(named = "Supplier")
    @Override
    public Party getSeller() {
        return super.getSeller();
    }

    @Override
    public void setSeller(final Party seller) {
        super.setSeller(invalidateApprovalIfDiffer(getSeller(), seller));
    }

    @Override
    public void setDueDate(final LocalDate dueDate) {
        super.setDueDate(invalidateApprovalIfDiffer(getDueDate(), dueDate));
    }

    @Override
    public void setInvoiceNumber(final String invoiceNumber) {
        super.setInvoiceNumber(invalidateApprovalIfDiffer(getInvoiceNumber(), invoiceNumber));
    }

    @Override
    public void setPaymentMethod(final PaymentMethod paymentMethod) {
        super.setPaymentMethod(invalidateApprovalIfDiffer(getPaymentMethod(), paymentMethod));
    }

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Getter @Setter
    private BigDecimal netAmount;

    public void setNetAmount(final BigDecimal netAmount) {
        this.netAmount = invalidateApprovalIfDiffer(this.netAmount, netAmount);
    }

    @org.apache.isis.applib.annotation.Property(hidden = Where.ALL_TABLES)
    @Digits(integer = 9, fraction = 2)
    public BigDecimal getVatAmount() {
        return getGrossAmount() != null && getNetAmount() != null
                ? getGrossAmount().subtract(getNetAmount())
                : null;
    }

    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Getter @Setter
    private BigDecimal grossAmount;

    public void setGrossAmount(final BigDecimal grossAmount) {
        this.grossAmount = invalidateApprovalIfDiffer(this.grossAmount, grossAmount);
    }

    @org.apache.isis.applib.annotation.Property(notPersisted = true, hidden = Where.ALL_TABLES)
    public BigDecimal getNetAmountLinked() {
        return orderItemInvoiceItemLinkRepository.calculateNetAmountLinkedToInvoice(this);
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public IncomingInvoice changeAmounts(final BigDecimal netAmount, final BigDecimal grossAmount) {
        setNetAmount(netAmount);
        setGrossAmount(grossAmount);
        return this;
    }

    public BigDecimal default0ChangeAmounts() {
        return getNetAmount();
    }

    public BigDecimal default1ChangeAmounts() {
        return getGrossAmount();
    }

    public String validateChangeAmounts(final BigDecimal netAmount, final BigDecimal grossAmount) {
        if ((grossAmount.signum() >= 0 && grossAmount.compareTo(netAmount) < 0) ||
                (grossAmount.signum() < 0 && grossAmount.compareTo(netAmount) > 0)) {
            return "Gross amount cannot be lower than net amount";
        }
        return null;
    }

    @Programmatic
    @Override
    public boolean isImmutableDueToState() {
        final Object viewContext = this;
        return reasonDisabledDueToState(viewContext) != null;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public IncomingInvoice editInvoiceNumber(
            @Nullable final String invoiceNumber) {
        setInvoiceNumber(invoiceNumber);
        return this;
    }

    public String default0EditInvoiceNumber() {
        return getInvoiceNumber();
    }

    public String disableEditInvoiceNumber() {

        final ReasonBuffer2 buf = ReasonBuffer2.forSingle("Cannot edit invoice number because");

        final Object viewContext = this;
        reasonDisabledDueToApprovalStateIfAny(viewContext, buf);

        return buf.getReason();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Edit ECP (as buyer)")
    public IncomingInvoice editBuyer(
            @Nullable final Party buyer) {
        setBuyer(buyer);
        return this;
    }

    public List<Party> autoComplete0EditBuyer(@MinLength(3) final String searchPhrase) {
        return partyRepository.autoCompleteWithRole(searchPhrase, IncomingInvoiceRoleTypeEnum.ECP);
    }

    public String validate0EditBuyer(final Party party) {
        return partyRoleRepository.validateThat(party, IncomingInvoiceRoleTypeEnum.ECP);
    }

    public Party default0EditBuyer() {
        return getBuyer();
    }

    public String disableEditBuyer() {
        return CountryUtil.isItalian(this) ? "Editing is disabled" : "Buyer is not editable; discard invoice and rescan document with appropriate barcode instead";
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Edit Supplier")
    public IncomingInvoice editSeller(
            @Nullable final Party supplier,
            final boolean createRoleIfRequired) {
        setSeller(supplier);
        setBankAccount(bankAccountRepository.getFirstBankAccountOfPartyOrNull(supplier));
        if (supplier != null && createRoleIfRequired) {
            partyRoleRepository.findOrCreate(supplier, IncomingInvoiceRoleTypeEnum.SUPPLIER);
        }
        return this;
    }

    public String validateEditSeller(
            final Party supplier,
            final boolean createRoleIfRequired) {
        if (supplier != null && !createRoleIfRequired) {
            // requires that the supplier already has this role
            return partyRoleRepository.validateThat(supplier, IncomingInvoiceRoleTypeEnum.SUPPLIER);
        }
        return null;
    }

    public Party default0EditSeller() {
        return getSeller();
    }

    public List<Party> autoComplete0EditSeller(final String search) {
        return partyRepository.autoCompleteSupplier(search, getAtPath());
    }

    public String disableEditSeller() {

        final ReasonBuffer2 buf = ReasonBuffer2.forSingle("Cannot edit seller because");

        final Object viewContext = this;
        reasonDisabledDueToApprovalStateIfAny(viewContext, buf);
        buf.append(this::sellerIsImmutableReason);

        return buf.getReason();
    }

    private String sellerIsImmutableReason() {
        for (InvoiceItem item : getItems()) {
            IncomingInvoiceItem ii = (IncomingInvoiceItem) item;
            if (ii.isLinkedToOrderItem()) {
                return "an item is linked to an order";
            }
        }
        return null;
    }

    public IncomingInvoice changeDates(
            @Nullable final LocalDate dateReceived,
            @Nullable final LocalDate invoiceDate,
            @Nullable final LocalDate dueDate
    ) {
        setDateReceived(dateReceived);
        setInvoiceDate(invoiceDate);
        setDueDate(dueDate);
        return this;
    }

    public LocalDate default0ChangeDates() {
        return getDateReceived();
    }

    public LocalDate default1ChangeDates() {
        return getInvoiceDate();
    }

    public LocalDate default2ChangeDates() {
        return getDueDate();
    }

    public String disableChangeDates() {
        final ReasonBuffer2 buf = ReasonBuffer2.forSingle("Cannot change dates because");

        final Object viewContext = this;
        reasonDisabledDueToApprovalStateIfAny(viewContext, buf);

        return buf.getReason();
    }

    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false")
    private IncomingInvoiceApprovalState approvalState;

    /**
     * that is, has passed final approval step.
     * <p>
     * Like {@link #getApprovalState()}, this field is populated as the result of transitioning.
     * It can be reset back if any property changes such as to invalidate the approval, as per {@link #invalidateApprovalIfDiffer(Object, Object)}.
     */
    @Getter @Setter
    @Column(allowsNull = "false")
    private boolean approvedFully;

    @Override
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > S getStateOf(
            final Class<ST> stateTransitionClass) {
        if (stateTransitionClass == IncomingInvoiceApprovalStateTransition.class) {
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
        if (stateTransitionClass == IncomingInvoiceApprovalStateTransition.class) {
            setApprovalState((IncomingInvoiceApprovalState) newState);
        }
    }

    // TODO: added this method for the moment until EST-1508 is picked up - then to be reviewed
    @Programmatic
    public String reasonDisabledDueToStateStrict() {
        final IncomingInvoiceApprovalState approvalState = getApprovalState();
        // guard for historic invoices (and invoice items)
        if (approvalState == null) {
            return "Cannot modify (invoice was migrated from spreadsheets)";
        }
        switch (approvalState) {
            case NEW:
                return null;
            default:
                return "Cannot modify because invoice is in state of " + getApprovalState();
        }
    }

    @Override
    @Programmatic
    public String reasonDisabledFinanceDetailsDueToState(final Object viewContext) {
        final IncomingInvoiceApprovalState approvalState = getApprovalState();
        if (approvalState == null) {
            return "Cannot modify (invoice was migrated from spreadsheets)";
        }
        switch (approvalState) {
            case DISCARDED:
                return "Invoice has been DISCARDED";
            case PAYABLE:
                final List<PaymentLine> paymentLines = paymentLineRepository.findByInvoice(this);
                if (!paymentLines.isEmpty()) {
                    return "Invoice already in a payment batch";
                }
                break;
            case PAID:
                return "Invoice has been PAID";
        }
        return null;
    }

    @Override
    @Programmatic
    public String reasonDisabledDueToState(final Object viewContext) {
        final String reasonDisabledDueToApprovalStateIfAny = reasonDisabledDueToApprovalStateIfAny(viewContext);
        if (reasonDisabledDueToApprovalStateIfAny != null) {
            return reasonDisabledDueToApprovalStateIfAny;
        }

        return null;
    }

    String reasonDisabledDueToApprovalStateIfAny(final Object viewContext) {
        final ReasonBuffer2 buf = ReasonBuffer2.forSingle("Cannot modify invoice because");

        reasonDisabledDueToApprovalStateIfAny(viewContext, buf);

        return buf.getReason();
    }

    void reasonDisabledDueToApprovalStateIfAny(final Object viewContext, final ReasonBuffer2 buf) {
        final IncomingInvoiceApprovalState approvalState = getApprovalState();

        buf.append(
                approvalState == null,
                "invoice state is unknown (was migrated so assumed to be approved)");

        buf.append(
                approvalState == IncomingInvoiceApprovalState.COMPLETED &&
                        metaModelService3.sortOf(viewContext.getClass()) == MetaModelService2.Sort.VIEW_MODEL,
                "modification through view not allowed once invoice is " + approvalState);

        buf.append(
                approvalState != IncomingInvoiceApprovalState.NEW &&
                        approvalState != IncomingInvoiceApprovalState.COMPLETED,
                "invoice is in state of " + getApprovalState());
    }

    @Programmatic
    public String reasonIncomplete() {

        String invoiceValidatorResult = new Validator()
                .checkNotNull(getType(), "incoming invoice type")
                .checkNotNull(getInvoiceNumber(), "invoice number")
                .checkNotNull(getBuyer(), "buyer")
                .checkNotNull(getSeller(), "seller")
                .checkNotNull(getDateReceived(), "date received")
                .checkNotNull(getDueDate(), "due date")
                .checkNotNull(getPaymentMethod(), "payment method")
                .checkNotNull(getNetAmount(), "net amount")
                .checkNotNull(getGrossAmount(), "gross amount")
                .validateForPaymentMethod(this)
                .validateForIncomingInvoiceType(this)
                .validateForAmounts(this)
                .validateForBankAccountOwner(this)
                .getResult();

        return mergeReasonItemsIncomplete(invoiceValidatorResult);

    }

    private String mergeReasonItemsIncomplete(final String validatorResult) {
        if (reasonItemsIncomplete() != null) {
            return validatorResult != null ?
                    validatorResult.replace(" required", ", ").concat(reasonItemsIncomplete())
                    : reasonItemsIncomplete();
        } else {
            return validatorResult;
        }
    }

    static class Validator {

        public Validator() {
            this.result = null;
        }

        @Setter
        String result;

        String getResult() {
            return result != null ? result.concat(" required") : null;
        }

        IncomingInvoice.Validator checkNotNull(Object mandatoryProperty, String propertyName) {
            if (mandatoryProperty == null) {
                setResult(result == null ? propertyName : result.concat(", ").concat(propertyName));
            }
            return this;
        }

        IncomingInvoice.Validator validateForIncomingInvoiceType(IncomingInvoice incomingInvoice) {
            if (incomingInvoice == null)
                return this;
            if (incomingInvoice.getType() == null)
                return this;
            if (CountryUtil.isItalian(incomingInvoice))
                return this; //ECP-896 Italian invoice of type CAPEX do not require a property

            String message;
            switch (incomingInvoice.getType()) {

                case CAPEX:
                case SERVICE_CHARGES:
                case PROPERTY_EXPENSES:
                    message = "property";
                    if (incomingInvoice.getProperty() == null) {
                        setResult(result == null ? message : result.concat(", ").concat(message));
                    }
                    break;

                default:
            }

            return this;
        }

        IncomingInvoice.Validator validateForPaymentMethod(IncomingInvoice incomingInvoice) {
            if (incomingInvoice == null)
                return this;
            if (incomingInvoice.getPaymentMethod() == null)
                return this;

            String message;
            switch (incomingInvoice.getPaymentMethod()) {

                case BILLING_ACCOUNT:
                case BANK_TRANSFER:
                case CASH:
                case CHEQUE:
                    message = "bank account";
                    if (incomingInvoice.getBankAccount() == null) {
                        setResult(result == null ? message : result.concat(", ").concat(message));
                    }
                    break;

                case MANUAL_PROCESS:
                case DIRECT_DEBIT:
                case CREDIT_CARD:
                case REFUND_BY_SUPPLIER:
                default:
                    break;
            }

            return this;
        }

        IncomingInvoice.Validator validateForAmounts(IncomingInvoice incomingInvoice) {
            if (incomingInvoice.getNetAmount() == null || incomingInvoice.getGrossAmount() == null) {
                // only validate when amounts are set on the invoice
                return this;
            }
            String message;
            if (!incomingInvoice.getTotalNetAmount().setScale(2).equals(incomingInvoice.getNetAmount().setScale(2))
                    || !incomingInvoice.getTotalGrossAmount().setScale(2).equals(incomingInvoice.getGrossAmount().setScale(2))
                    || !incomingInvoice.getTotalVatAmount().setScale(2).equals(incomingInvoice.getVatAmount().setScale(2))) {
                message = "total amount on items equal to amount on the invoice";
                setResult(result == null ? message : result.concat(", ").concat(message));
            }
            return this;
        }

        IncomingInvoice.Validator validateForBankAccountOwner(IncomingInvoice incomingInvoice) {
            if (incomingInvoice.getBankAccount() == null || incomingInvoice.getSeller() == null) {
                // only validate when bankaccount and seller are set on the invoice
                return this;
            }
            String message;
            if (!incomingInvoice.getBankAccount().getOwner().equals(incomingInvoice.getSeller())) {
                message = "match of owner bankaccount and seller";
                setResult(result == null ? message : result.concat(", ").concat(message));
            }
            return this;
        }

    }

    @Programmatic
    public String reasonItemsIncomplete() {
        StringBuffer buffer = new StringBuffer();
        for (InvoiceItem item : getItems()) {
            IncomingInvoiceItem incomingInvoiceItem = (IncomingInvoiceItem) item;
            if (incomingInvoiceItem.reasonIncomplete() != null) {
                buffer.append("(on item ");
                buffer.append(incomingInvoiceItem.getSequence().toString());
                buffer.append(") ");
                buffer.append(incomingInvoiceItem.reasonIncomplete());
            }
        }
        return buffer.length() == 0 ? null : buffer.toString();
    }

    @Programmatic
    public boolean isReported() {
        for (InvoiceItem item : getItems()) {
            IncomingInvoiceItem incomingInvoiceItem = (IncomingInvoiceItem) item;
            if (((IncomingInvoiceItem) item).getReportedDate() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * has final modifier so cannot be mocked out.
     */
    final <T> T invalidateApprovalIfDiffer(T previousValue, T newValue) {
        if (!Objects.equals(previousValue, newValue)) {
            invalidateApproval();
        }
        return newValue;
    }

    protected void invalidateApproval() {
        getEventBusService().post(new ApprovalInvalidatedEvent(this));
    }

    @Programmatic
    public List<ApprovalString> getApprovals() {
        // TODO: as of EST-1824 temporarily we will inspect the transition instead of the task on the transition
        return stateTransitionRepository.findByDomainObject(this)
                .stream()
                .filter(x -> x.getToState() != null && x.getToState().isApproval())
                .filter(x -> x.getCompletedBy() != null)
                .sorted(Comparator.comparing(IncomingInvoiceApprovalStateTransition::getCompletedOn)) // should always be set when completedBy is set
                .map(x -> new ApprovalString(x.getCompletedBy(), x.getCompletedOn().toString("dd-MMM-yyyy HH:mm"), x.getCompletedOn().toLocalDate()))
                .collect(Collectors.toList());
    }

    @AllArgsConstructor
    @Getter @Setter
    public class ApprovalString {

        private String completedBy;

        private String completedOn;

        private LocalDate completedOnAsDate;

    }

    @PropertyLayout(hidden = Where.OBJECT_FORMS)
    public String getDescriptionSummary() {
        StringBuffer summary = new StringBuffer();
        boolean first = true;
        for (InvoiceItem item : getItems()) {
            IncomingInvoiceItem iitem = (IncomingInvoiceItem) item;
            if (!iitem.isReversal() && !reversedItems().contains(iitem) && item.getDescription() != null && item.getDescription() != "") {
                if (!first) {
                    summary.append(" | ");
                }
                summary.append(item.getDescription());
                first = false;
            }
        }
        return summary.toString();
    }

    @Programmatic
    public String getProjectSummary() {
        List<Project> distinctProjects = new ArrayList<>();
        for (InvoiceItem item : getItems()) {
            IncomingInvoiceItem iitem = (IncomingInvoiceItem) item;
            if (!iitem.isReversal() && !reversedItems().contains(iitem) && iitem.getProject() != null && !distinctProjects.contains(iitem.getProject())) {
                distinctProjects.add(iitem.getProject());
            }
        }
        StringBuffer summary = new StringBuffer();
        for (Project project : distinctProjects) {
            if (summary.length() > 0) {
                summary.append(" | ");
            }
            summary.append(project.getName());
        }
        return summary.toString();
    }

    @Programmatic
    public String getPropertySummary() {
        List<Property> distinctProperties = new ArrayList<>();
        for (InvoiceItem item : getItems()) {
            IncomingInvoiceItem iitem = (IncomingInvoiceItem) item;
            if (!iitem.isReversal() && !reversedItems().contains(iitem) && iitem.getFixedAsset() != null && !distinctProperties.contains(iitem.getFixedAsset())) {
                distinctProperties.add((Property) iitem.getFixedAsset());
            }
        }
        StringBuffer summary = new StringBuffer();
        for (Property property : distinctProperties) {
            if (summary.length() > 0) {
                summary.append(" | ");
            }
            summary.append(property.getName());
        }
        return summary.toString();
    }

    @Override
    public int compareTo(final IncomingInvoice other) {
        return ComparisonChain.start()
                .compare(getSeller(), other.getSeller())
                .compare(getInvoiceNumber(), other.getInvoiceNumber())
                .result();
    }

    @PropertyLayout(hidden = Where.OBJECT_FORMS)
    public String getBarcode() {
        final Optional<Document> document = lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(this);
        return document.map(DocumentAbstract::getName).orElse(null);
    }

    //region > notification

    @org.apache.isis.applib.annotation.Property(editing = Editing.DISABLED)
    @PropertyLayout(multiLine = 5, hidden = Where.ALL_TABLES)
    public String getNotification() {
        final StringBuilder result = new StringBuilder();

        final String noBuyerBarcodeMatch = buyerBarcodeMatchValidation();
        if (noBuyerBarcodeMatch != null) {
            result.append(noBuyerBarcodeMatch);
        }

        final String sameInvoiceNumberCheck = doubleInvoiceCheck();
        if (sameInvoiceNumberCheck != null) {
            result.append(sameInvoiceNumberCheck);
        }

        final String multiplePaymentMethods = paymentMethodValidation();
        if (multiplePaymentMethods != null) {
            result.append(multiplePaymentMethods);
        }

        final String mismatchedTypes = mismatchedTypesOnLinkedItemsCheck();
        if (mismatchedTypes != null) {
            result.append(mismatchedTypes);
        }

        return result.length() > 0 ? result.toString() : null;
    }

    public boolean hideNotification() {
        return CountryUtil.isItalian(this) || getNotification() == null;
    }

    @Programmatic
    private String mismatchedTypesOnLinkedItemsCheck() {
        StringJoiner sj = new StringJoiner("; ");
        getItems().stream()
                .filter(IncomingInvoiceItem.class::isInstance)
                .map(IncomingInvoiceItem.class::cast)
                .map(item -> orderItemInvoiceItemLinkRepository.findByInvoiceItem(item))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(orderItemInvoiceItemLink -> {
                    IncomingInvoiceType orderItemType = orderItemInvoiceItemLink.getOrderItem().getOrdr().getType();
                    IncomingInvoiceType invoiceItemType = orderItemInvoiceItemLink.getInvoiceItem().getIncomingInvoiceType();
                    return (orderItemType != null && invoiceItemType != null) && !orderItemType.equals(invoiceItemType);
                })
                .forEach(link -> sj.add(String.format("an invoice item of type %s is linked to an order item of type %s",
                        link.getInvoiceItem().getIncomingInvoiceType().toString(),
                        link.getOrderItem().getOrdr().getType().toString()))
                );

        return sj.length() != 0 ? new StringJoiner("").add("WARNING: mismatched types between linked items: ").merge(sj).toString() : null;
    }

    @Programmatic
    private String doubleInvoiceCheck() {
        final String doubleInvoiceCheck = possibleDoubleInvoice();
        if (doubleInvoiceCheck != null) {
            return doubleInvoiceCheck;
        }
        final String sameNumberCheck = sameInvoiceNumber();
        if (sameNumberCheck != null) {
            return sameNumberCheck;
        }
        return null;
    }

    @Programmatic
    private String possibleDoubleInvoice() {
        if (getInvoiceNumber() == null || getSeller() == null || getInvoiceDate() == null) {
            return null;
        }

        IncomingInvoice possibleDouble = incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate(getInvoiceNumber(), getSeller(), getInvoiceDate());
        if (possibleDouble == null || possibleDouble.equals(this)) {
            return null;
        }

        return "WARNING: There is already an invoice with the same number and invoice date for this seller. Please check.";
    }

    @Programmatic
    private String sameInvoiceNumber() {
        if (getInvoiceNumber() == null || getSeller() == null) {
            return null;
        }

        List<IncomingInvoice> similarNumberedInvoices = incomingInvoiceRepository.findByInvoiceNumberAndSeller(getInvoiceNumber(), getSeller())
                .stream()
                .filter(invoice -> !invoice.equals(this))
                .collect(Collectors.toList());

        if (similarNumberedInvoices.size() > 0) {
            String message = "WARNING: Invoices with the same number of this seller are found ";
            for (IncomingInvoice invoice : similarNumberedInvoices) {
                if (invoice.getInvoiceDate() != null) {
                    message = message.concat("on date ").concat(invoice.getInvoiceDate().toString()).concat("; ");
                }
            }
            return message;
        }

        return null;
    }

    @Programmatic
    private String buyerBarcodeMatchValidation() {
        if (getBuyer() != null) {
            if (buyerFinder.buyerDerivedFromDocumentName(this) == null) {
                return null; // covers all cases where no buyer could be derived from document name
            }
            if (!getBuyer().equals(buyerFinder.buyerDerivedFromDocumentName(this))) {
                return "Buyer does not match barcode (document name); ";
            }
        }
        return null;
    }

    @Programmatic
    private String paymentMethodValidation() {
        if (getPaymentMethod() != null && getSeller() != null) {
            List<PaymentMethod> historicalPaymentMethods = invoiceRepository.findBySeller(getSeller()).stream()
                    .map(Invoice::getPaymentMethod)
                    .filter(Objects::nonNull)
                    .filter(pm -> pm != PaymentMethod.BANK_TRANSFER)
                    .filter(pm -> pm != PaymentMethod.REFUND_BY_SUPPLIER)
                    .filter(pm -> pm != PaymentMethod.MANUAL_PROCESS)
                    .distinct()
                    .collect(Collectors.toList());

            // Current payment method is bank transfer, but at least one different payment method has been used before
            if (getPaymentMethod() == PaymentMethod.BANK_TRANSFER && !historicalPaymentMethods.isEmpty()) {
                StringBuilder builder = new StringBuilder().append("WARNING: payment method is set to bank transfer, but previous invoices from this seller have used the following payment methods: ");
                historicalPaymentMethods.forEach(pm -> {
                    builder.append(pm.title());
                    builder.append(", ");
                });

                builder.delete(builder.length() - 2, builder.length() - 1);

                return builder.toString();
            }
        }

        return null;
    }

    //endregion

    @Inject
    @NotPersistent
    public IncomingInvoiceApprovalStateTransition.Repository stateTransitionRepository;

    @Inject
    @NotPersistent
    PaymentLineRepository paymentLineRepository;

    @Inject
    @NotPersistent
    LookupAttachedPdfService lookupAttachedPdfService;

    @Inject
    @NotPersistent
    MetaModelService3 metaModelService3;

    @Inject
    @NotPersistent
    BankAccountRepository bankAccountRepository;

    @Inject
    @NotPersistent
    OrderItemRepository orderItemRepository;

    @Inject
    @NotPersistent
    OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

    @Inject
    @NotPersistent
    PartyRoleRepository partyRoleRepository;

    @Inject
    @NotPersistent
    PartyRepository partyRepository;

    @Inject
    @NotPersistent
    ProjectRepository projectRepository;

    @Inject
    @NotPersistent
    BudgetItemChooser budgetItemChooser;

    @Inject
    @NotPersistent
    IncomingInvoiceItemRepository incomingInvoiceItemRepository;

    @Inject
    @NotPersistent
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    @NotPersistent
    InvoiceRepository invoiceRepository;

    @Inject
    @NotPersistent
    ChargeRepository chargeRepository;

    @Inject
    @NotPersistent
    StateTransitionService stateTransitionService;

    @Inject
    @NotPersistent
    BuyerFinder buyerFinder;

}
