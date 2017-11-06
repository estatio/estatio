package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.metamodel.MetaModelService2;
import org.apache.isis.applib.services.metamodel.MetaModelService3;
import org.apache.isis.applib.util.TitleBuffer;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.capex.dom.documents.BudgetItemChooser;
import org.estatio.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.capex.dom.payment.PaymentLine;
import org.estatio.capex.dom.payment.PaymentLineRepository;
import org.estatio.capex.dom.project.Project;
import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.state.Stateful;
import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.role.PartyRoleRepository;
import org.estatio.dom.utils.ReasonBuffer2;
import org.estatio.module.tax.dom.Tax;

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
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoice "
                        + "WHERE approvalState == :approvalState "),
        @Query(
                name = "findByApprovalStateAndPaymentMethod", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoice "
                        + "WHERE approvalState == :approvalState "
                        + "   && paymentMethod == :paymentMethod "),
        @Query(
                name = "findByInvoiceNumber", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoice "
                        + "WHERE invoiceNumber == :invoiceNumber "),
        @Query(
                name = "findByInvoiceNumberAndSeller", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoice "
                        + "WHERE invoiceNumber == :invoiceNumber "
                        + " && seller == :seller "),
        @Query(
                name = "findByInvoiceNumberAndSellerAndInvoiceDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoice "
                        + "WHERE invoiceNumber == :invoiceNumber "
                        + "   && seller == :seller "
                        + "   && invoiceDate == :invoiceDate "),
        @Query(
                name = "findByInvoiceDateBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoice "
                        + "WHERE invoiceDate >= :fromDate "
                        + "   && invoiceDate <= :toDate "),
        @Query(
                name = "findCompletedOrLaterWithItemsByReportedDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoice "
                        + "WHERE items.contains(ii) "
                        + "   && (ii.reportedDate == :reportedDate) "
                        + "   && (approvalState != 'NEW' && approvalState != 'DISCARDED') "
                        + "VARIABLES org.estatio.capex.dom.invoice.IncomingInvoiceItem ii "
        ),
        @Query(
                name = "findByDueDateBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoice "
                        + "WHERE dueDate >= :fromDate "
                        + "   && dueDate <= :toDate "),
        @Query(
                name = "findByPropertyAndDateReceivedBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoice "
                        + "WHERE property == :property "
                        + "   && dateReceived >= :fromDate "
                        + "   && dateReceived <= :toDate "),
        @Query(
                name = "findNotInAnyPaymentBatchByApprovalStateAndPaymentMethod", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoice "
                        + "WHERE !(SELECT invoice "
                        +         "  FROM org.estatio.capex.dom.payment.PaymentLine).contains(this) "
                        + "   && approvalState == :approvalState "
                        + "   && paymentMethod == :paymentMethod "
                        + "ORDER BY invoiceDate ASC " // oldest first
        ),
        @Query(
                name = "findByBankAccount", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoice "
                        + "WHERE bankAccount == :bankAccount "
                        + "ORDER BY invoiceDate DESC " // newest first
        )
})
@FetchGroup(
        name="seller_buyer_property_bankAccount",
        members={
                @Persistent(name="seller"),
                @Persistent(name="buyer"),
                @Persistent(name="property"),
                @Persistent(name="bankAccount")
        })
@Indices({
        @Index(name = "IncomingInvoice_approvalState_IDX", members = { "approvalState" })
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
        public IncomingInvoice getIncomingInvoice(){
            return (IncomingInvoice) getSource();
        }
    }

    public static class ObjectPersistingEvent
            extends org.apache.isis.applib.services.eventbus.ObjectPersistingEvent <IncomingInvoice> {
    }
    public static class ObjectPersistedEvent
            extends org.apache.isis.applib.services.eventbus.ObjectPersistedEvent <IncomingInvoice> {
    }
    public static class ObjectRemovingEvent
            extends org.apache.isis.applib.services.eventbus.ObjectRemovingEvent <IncomingInvoice> {
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
            final IncomingInvoiceApprovalState approvalStateIfAny){
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
        setApprovalState(approvalStateIfAny);
    }

    public String title() {
        final TitleBuffer buf = new TitleBuffer();

        final Optional<Document> document = lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(this);
        document.ifPresent(d -> buf.append(d.getName()));

        final Party seller = getSeller();
        if(seller != null) {
            buf.append(": ", seller);
        }

        final String invoiceNumber = getInvoiceNumber();
        if(invoiceNumber != null) {
            buf.append(", ", invoiceNumber);
        }

        return buf.toString();
    }


    @MemberOrder(name="items", sequence = "1")
    public IncomingInvoice addItem(
            final IncomingInvoiceType type,
            final Charge charge,
            final String description,
            @Digits(integer=13, fraction = 2)
            final BigDecimal netAmount,
            @Nullable
            @Digits(integer=13, fraction = 2)
            final BigDecimal vatAmount,
            @Digits(integer=13, fraction = 2)
            final BigDecimal grossAmount,
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
        return ofFirstItem(IncomingInvoiceItem::getStartDate)!=null ? PeriodUtil.periodFromInterval(new LocalDateInterval(ofFirstItem(IncomingInvoiceItem::getStartDate), ofFirstItem(IncomingInvoiceItem::getEndDate))) : null;
    }

    public Property default9AddItem() {
        return getProperty();
    }

    public Project default10AddItem() {
        return ofFirstItem(IncomingInvoiceItem::getProject);
    }

    public List<Charge> choices1AddItem(){
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
            final BudgetItem budgetItem){
        if (period==null) return null; // period is optional
        return PeriodUtil.reasonInvalidPeriod(period);
    }

    boolean amountsCoveredByAmountsItems(){
        if ((getNetAmount()!=null && getTotalNetAmount().compareTo(getNetAmount()) >= 0)
                || (getGrossAmount() !=null && getTotalGrossAmount().compareTo(getGrossAmount()) >= 0)
                ){
            return true;
        }

        return false;

    }


    @Action
    @MemberOrder(name="items", sequence = "4")
    public IncomingInvoice reverseItem(final IncomingInvoiceItem itemToReverse) {

        final IncomingInvoiceItem reversal = copyWithLinks(itemToReverse, Sort.REVERSAL);
        final IncomingInvoiceItem correction = copyWithLinks(itemToReverse, Sort.CORRECTION);

        return this;
    }

    enum Sort {
        REVERSAL {
            @Override BigDecimal adjust(final BigDecimal amount) {
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
        };
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

        if(sort == Sort.REVERSAL) {
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
        return Lists.newArrayList(getItems()).stream().
                filter(IncomingInvoiceItem.class::isInstance)
                .map(IncomingInvoiceItem.class::cast)
                .filter(x -> x.getReportedDate() != null)
                .filter(x -> x.getReversalOf() == null)
                .collect(Collectors.toList());
    }




    @MemberOrder(name = "items", sequence = "2")
    public IncomingInvoice splitItem(
            final IncomingInvoiceItem itemToSplit,
            final String newItemDescription,
            @Digits(integer = 13, fraction = 2)
            final BigDecimal newItemNetAmount,
            @Nullable
            @Digits(integer = 13, fraction = 2)
            final BigDecimal newItemVatAmount,
            @Nullable
            final Tax newItemtax,
            @Digits(integer = 13, fraction = 2)
            final BigDecimal newItemGrossAmount,
            final Charge newItemCharge,
            @Nullable
            final Property newItemProperty,
            @Nullable
            final Project newItemProject,
            @Nullable
            final BudgetItem newItemBudgetItem,
            final String newItemPeriod
    ) {
        itemToSplit.subtractAmounts(newItemNetAmount, newItemVatAmount, newItemGrossAmount);
        addItemToThis(getType(), newItemCharge, newItemDescription, newItemNetAmount,
                newItemVatAmount, newItemGrossAmount, newItemtax, getDueDate(), newItemPeriod, newItemProperty,
                newItemProject, newItemBudgetItem);
        return this;
    }

    public String disableSplitItem() {

        ReasonBuffer2 buf = ReasonBuffer2.forSingle("Cannot spli items because");
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

    public List<Charge> choices6SplitItem(){
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
            final String newItemPeriod){
        return PeriodUtil.reasonInvalidPeriod(newItemPeriod);
    }




    @Programmatic
    public <T> T ofFirstItem(final Function<IncomingInvoiceItem, T> f) {
        final Optional<IncomingInvoiceItem> firstItemIfAny = firstItemIfAny();
        return firstItemIfAny.map(f).orElse(null);
    }

    @Programmatic
    public Optional<IncomingInvoiceItem> firstItemIfAny() {
        return  Lists.newArrayList(getItems()).stream()
                .filter(IncomingInvoiceItem.class::isInstance)
                .map(IncomingInvoiceItem.class::cast)
                .findFirst();
    }



    @MemberOrder(name = "items", sequence = "3")
    public IncomingInvoice mergeItems(
            final IncomingInvoiceItem item,
            final IncomingInvoiceItem mergeInto){
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
        return firstItemIfAny()!=null ? (IncomingInvoiceItem) getItems().last() : null;
    }

    public IncomingInvoiceItem default1MergeItems() {
        return firstItemIfAny()!=null ? (IncomingInvoiceItem) getItems().first() : null;
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
                .filter(x->!x.equals(item))
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
    @Mixin(method="act")
    public static class changeBankAccount  {

        private final IncomingInvoice incomingInvoice;

        public changeBankAccount(final IncomingInvoice incomingInvoice) {
            this.incomingInvoice = incomingInvoice;
        }

        @Action(semantics = SemanticsOf.IDEMPOTENT)
        @ActionLayout(contributed= Contributed.AS_ACTION)
        public IncomingInvoice act(final BankAccount bankAccount){
            incomingInvoice.setBankAccount(bankAccount);
            return  incomingInvoice;
        }

        public String disableAct(){

            final Object viewContext = incomingInvoice;
            final String reasonIfAny = incomingInvoice.reasonDisabledFinanceDetailsDueToState(viewContext);
            if(reasonIfAny != null) {
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

        public List<BankAccount> choices0Act(){
            return bankAccountRepository.findBankAccountsByOwner(incomingInvoice.getSeller());
        }
        public BankAccount default0Act(){
            return incomingInvoice.getBankAccount();
        }

        /**
         * An alternative design would be to filter out all non-verified bank accounts in the choicesXxx, but that
         * could be confusing to the end-user (wondering why some bank accounts of the seller aren't listed).
         */
        public String validate0Act(final BankAccount bankAccount){
            // a mutable invoice does not need a verified bankaccount
            if (!incomingInvoice.isImmutableDueToState()) return null;

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
     *     This can be overridden for each invoice item.
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
            final boolean changeOnItemsAsWell){
        if (changeOnItemsAsWell){
            Lists.newArrayList(getItems())  // eagerly load (DN 4.x collections do not support streaming)
                    .stream()
                    .map(IncomingInvoiceItem.class::cast)
                    .forEach(x->x.setIncomingInvoiceType(type));
        }
        setType(type);
        return this;
    }

    public IncomingInvoiceType default0EditType(){
        return getType();
    }

    public boolean default1EditType(){
        return true;
    }

    public String disableEditType(){
        return reasonDisabledDueToStateStrict();
    }

    /**
     * This relates to the owning property, while the child items may either also relate to the property,
     * or could potentially relate to individual units within the property.
     *
     * <p>
     *     Note that InvoiceForLease also has a reference to FixedAsset.  It's not possible to move this
     *     up to the Invoice superclass because invoicing module does not "know" about fixed assets.
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
            @Nullable
            final Property property,
            final boolean changeOnItemsAsWell){
        setProperty(property);
        if (changeOnItemsAsWell){
            Lists.newArrayList(getItems())  // eagerly load (DN 4.x collections do not support streaming)
                    .stream()
                    .map(IncomingInvoiceItem.class::cast)
                    .forEach(x->x.setFixedAsset(property));
        }
        return this;
    }

    public Property default0EditProperty(){
        return getProperty();
    }

    public boolean default1EditProperty(){
        return true;
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

    // TODO: does not seem to be used, raised EST-1599 to look into removing it.
    @Getter @Setter
    @Column(allowsNull = "true", name="invoiceId")
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


    @org.apache.isis.applib.annotation.Property(hidden = Where.ALL_TABLES)
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
    public IncomingInvoice changeAmounts(final BigDecimal netAmount, final BigDecimal grossAmount){
        setNetAmount(netAmount);
        setGrossAmount(grossAmount);
        return this;
    }

    public BigDecimal default0ChangeAmounts(){
        return getNetAmount();
    }

    public BigDecimal default1ChangeAmounts(){
        return getGrossAmount();
    }

    public String validateChangeAmounts(final BigDecimal netAmount, final BigDecimal grossAmount){
        if (grossAmount.compareTo(netAmount) < 0){
            return "Gross amount cannot be lower than net amount";
        }
        return null;
    }

    @Programmatic
    @Override
    public boolean isImmutableDueToState() {
        final Object viewContext = this;
        return reasonDisabledDueToState(viewContext)!=null;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public IncomingInvoice editInvoiceNumber(
            @Nullable
            final String invoiceNumber){
        setInvoiceNumber(invoiceNumber);
        return this;
    }

    public String default0EditInvoiceNumber(){
        return getInvoiceNumber();
    }

    public String disableEditInvoiceNumber(){

        final ReasonBuffer2 buf = ReasonBuffer2.forSingle("Cannot edit invoice number because");

        final Object viewContext = this;
        reasonDisabledDueToApprovalStateIfAny(viewContext, buf);

        return buf.getReason();
    }


    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Edit ECP (as buyer)")
    public IncomingInvoice editBuyer(
            @Nullable
            final Party buyer){
        setBuyer(buyer);
        return this;
    }
    public List<Party> autoComplete0EditBuyer(@MinLength(3) final String searchPhrase){
        return partyRepository.autoCompleteWithRole(searchPhrase, IncomingInvoiceRoleTypeEnum.ECP);
    }
    public String validate0EditBuyer(final Party party){
        return partyRoleRepository.validateThat(party, IncomingInvoiceRoleTypeEnum.ECP);
    }
    public Party default0EditBuyer(){
        return getBuyer();
    }
    public String disableEditBuyer(){
        final ReasonBuffer2 buf = ReasonBuffer2.forSingle("Cannot edit buyer because");

        final Object viewContext = this;
        reasonDisabledDueToApprovalStateIfAny(viewContext, buf);

        return buf.getReason();
    }

    
    
    
    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(named = "Edit Supplier")
    public IncomingInvoice editSeller(
            @Nullable
            final Party supplier,
            final boolean createRoleIfRequired){
        setSeller(supplier);
        setBankAccount(bankAccountRepository.getFirstBankAccountOfPartyOrNull(supplier));
        if(supplier != null && createRoleIfRequired) {
            partyRoleRepository.findOrCreate(supplier, IncomingInvoiceRoleTypeEnum.SUPPLIER);
        }
        return this;
    }
    public String validateEditSeller(final Party party, final boolean createRoleIfRequired){
        if(party != null && !createRoleIfRequired) {
            // requires that the supplier already has this role
            return partyRoleRepository.validateThat(party, IncomingInvoiceRoleTypeEnum.SUPPLIER);
        }
        return null;
    }
    public Party default0EditSeller(){
        return getSeller();
    }
    public String disableEditSeller(){

        final ReasonBuffer2 buf = ReasonBuffer2.forSingle("Cannot edit seller because");

        final Object viewContext = this;
        reasonDisabledDueToApprovalStateIfAny(viewContext, buf);
        buf.append(this::sellerIsImmutableReason);

        return buf.getReason();
    }



    private String sellerIsImmutableReason(){
        for (InvoiceItem item : getItems()){
            IncomingInvoiceItem ii = (IncomingInvoiceItem) item;
            if (ii.isLinkedToOrderItem()){
                return "an item is linked to an order";
            }
        }
        return null;
    }

    public IncomingInvoice changeDates(
            @Nullable
            final LocalDate dateReceived,
            @Nullable
            final LocalDate invoiceDate,
            @Nullable
            final LocalDate dueDate
    ){
        setDateReceived(dateReceived);
        setInvoiceDate(invoiceDate);
        setDueDate(dueDate);
        return this;
    }

    public LocalDate default0ChangeDates(){
        return getDateReceived();
    }

    public LocalDate default1ChangeDates(){
        return getInvoiceDate();
    }

    public LocalDate default2ChangeDates(){
        return getDueDate();
    }

    public String disableChangeDates(){
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
     *
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
        if(stateTransitionClass == IncomingInvoiceApprovalStateTransition.class) {
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
        if(stateTransitionClass == IncomingInvoiceApprovalStateTransition.class) {
            setApprovalState( (IncomingInvoiceApprovalState) newState );
        }
    }

    // TODO: added this method for the moment until EST-1508 is picked up - then to be reviewed
    @Programmatic
    public String reasonDisabledDueToStateStrict() {
        final IncomingInvoiceApprovalState approvalState = getApprovalState();
        // guard for historic invoices (and invoice items)
        if (approvalState==null){
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
        if (approvalState==null){
            return "Cannot modify (invoice was migrated from spreadsheets)";
        }
        switch (approvalState) {
        case DISCARDED:
            return "Invoice has been DISCARDED";
        case PAYABLE:
            final List<PaymentLine> paymentLines = paymentLineRepository.findByInvoice(this);
            if(!paymentLines.isEmpty()) {
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
        if(reasonDisabledDueToApprovalStateIfAny != null) {
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
                approvalState==null,
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
    public String reasonIncomplete(){

        String invoiceValidatorResult = new Validator()
                .checkNotNull(getType(),"incoming invoice type")
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
                .getResult();

        return mergeReasonItemsIncomplete(invoiceValidatorResult);

    }

    private String mergeReasonItemsIncomplete(final String validatorResult){
        if (reasonItemsIncomplete()!=null) {
            return validatorResult!=null ?
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

        IncomingInvoice.Validator validateForIncomingInvoiceType(IncomingInvoice incomingInvoice){
            if (incomingInvoice == null) return this;
            if (incomingInvoice.getType() == null) return this;

            String message;
            switch (incomingInvoice.getType()){

            case CAPEX:
            case SERVICE_CHARGES:
            case PROPERTY_EXPENSES:
                message = "property";
                if (incomingInvoice.getProperty()==null){
                    setResult(result==null ? message : result.concat(", ").concat(message));
                }
                break;

            default:
            }

            return this;
        }

        IncomingInvoice.Validator validateForPaymentMethod(IncomingInvoice incomingInvoice){
            if (incomingInvoice == null) return this;
            if (incomingInvoice.getPaymentMethod() == null) return this;

            String message;
            switch (incomingInvoice.getPaymentMethod()){

            case BILLING_ACCOUNT:
            case BANK_TRANSFER:
            case CASH:
            case CHEQUE:
                message = "bank account";
                if (incomingInvoice.getBankAccount()==null){
                    setResult(result==null ? message : result.concat(", ").concat(message));
                }
                break;

            default:
            }

            return this;
        }

        IncomingInvoice.Validator validateForAmounts(IncomingInvoice incomingInvoice){
            if (incomingInvoice.getNetAmount()==null || incomingInvoice.getGrossAmount()==null){
                // only validate when amounts are set on the invoice
                return this;
            }
            String message;
            if (!incomingInvoice.getTotalNetAmount().setScale(2).equals(incomingInvoice.getNetAmount().setScale(2))
                    || !incomingInvoice.getTotalGrossAmount().setScale(2).equals(incomingInvoice.getGrossAmount().setScale(2))
                    || !incomingInvoice.getTotalVatAmount().setScale(2).equals(incomingInvoice.getVatAmount().setScale(2))){
                message = "total amount on items equal to amount on the invoice";
                setResult(result==null ? message : result.concat(", ").concat(message));
            }
            return this;
        }

    }

    @Programmatic
    public String reasonItemsIncomplete(){
        StringBuffer buffer = new StringBuffer();
        for (InvoiceItem item : getItems()){
            IncomingInvoiceItem incomingInvoiceItem = (IncomingInvoiceItem) item;
            if (incomingInvoiceItem.reasonIncomplete()!=null) {
                buffer.append("(on item ");
                buffer.append(incomingInvoiceItem.getSequence().toString());
                buffer.append(") ");
                buffer.append(incomingInvoiceItem.reasonIncomplete());
            }
        }
        return buffer.length() == 0 ? null : buffer.toString();
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






    @Override
    public int compareTo(final IncomingInvoice other) {
        return ComparisonChain.start()
                .compare(getSeller(), other.getSeller())
                .compare(getInvoiceNumber(), other.getInvoiceNumber())
                .result();
    }

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
    OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

    @Inject
    @NotPersistent
    PartyRoleRepository partyRoleRepository;

    @Inject
    @NotPersistent
    PartyRepository partyRepository;

    @Inject
    @NotPersistent
    BudgetItemChooser budgetItemChooser;

    @Inject
    @NotPersistent
    IncomingInvoiceItemRepository incomingInvoiceItemRepository;

    @Inject
    @NotPersistent
    ChargeRepository chargeRepository;


}
