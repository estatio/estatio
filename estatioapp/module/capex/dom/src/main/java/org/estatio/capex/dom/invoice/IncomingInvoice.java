package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.validation.constraints.Digits;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.TitleBuffer;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.capex.dom.documents.BudgetItemChooser;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.invoice.approval.triggers.IncomingInvoice_triggerAbstract;
import org.estatio.capex.dom.project.Project;
import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.state.Stateful;
import org.estatio.dom.asset.Property;
import org.estatio.dom.budgeting.budgetitem.BudgetItem;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Party;
import org.estatio.dom.tax.Tax;

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
                name = "findByDueDateBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoice "
                        + "WHERE dueDate >= :fromDate "
                        + "   && dueDate <= :toDate "),
        @Query(
                name = "findByDateReceivedBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoice "
                        + "WHERE dateReceived >= :fromDate "
                        + "   && dateReceived <= :toDate "),
        @Query(
                name = "findNotInAnyPaymentBatchByApprovalStateAndPaymentMethod", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoice "
                        + "WHERE !(SELECT invoice "
                        +         "  FROM org.estatio.capex.dom.payment.PaymentLine).contains(this) "
                        + "   && approvalState == :approvalState "
                        + "   && paymentMethod == :paymentMethod "
                        + "ORDER BY invoiceDate ASC "
        ),
        @Query(
                name = "findByBankAccount", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoice "
                        + "WHERE bankAccount == :bankAccount ")
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
        persistedLifecycleEvent = IncomingInvoice.ObjectPersistedEvent.class
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class IncomingInvoice extends Invoice<IncomingInvoice> implements SellerBankAccountCreator, Stateful {

    public static class ObjectPersistedEvent
            extends org.apache.isis.applib.services.eventbus.ObjectPersistedEvent <IncomingInvoice> {
    }
    public static class ObjectPersistingEvent
            extends org.apache.isis.applib.services.eventbus.ObjectPersistingEvent <IncomingInvoice> {
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

    @Mixin(method="act")
    public static class addItem {
        private final IncomingInvoice incomingInvoice;
        public addItem(final IncomingInvoice incomingInvoice) {
            this.incomingInvoice = incomingInvoice;
        }

        @MemberOrder(name="items", sequence = "1")
        public IncomingInvoice act(
                final IncomingInvoiceType type,
                final Charge charge,
                @Nullable final String description,
                final BigDecimal netAmount,
                @Nullable final BigDecimal vatAmount,
                @Nullable final BigDecimal grossAmount,
                @Nullable final Tax tax,
                @Nullable final LocalDate dueDate,
                @Nullable final LocalDate startDate,
                @Nullable final LocalDate endDate,
                @Nullable final Property property,
                @Nullable final Project project,
                @Nullable final BudgetItem budgetItem) {
            final BigInteger sequence = incomingInvoice.nextItemSequence();
            incomingInvoiceItemRepository.upsert(
                    sequence,
                    incomingInvoice,
                    type, charge,
                    description,
                    netAmount,
                    vatAmount,
                    grossAmount,
                    tax,
                    dueDate,
                    startDate,
                    endDate,
                    property,
                    project,
                    budgetItem);

            return incomingInvoice;
        }

        public String disableAct() {
            return incomingInvoice.reasonDisabledDueToState();
        }

        public IncomingInvoiceType default0Act() {
            return incomingInvoice.getType();
        }

        public LocalDate default7Act() {
            return ofFirstItem(IncomingInvoiceItem::getDueDate);
        }

        public LocalDate default8Act() {
            return ofFirstItem(IncomingInvoiceItem::getStartDate);
        }

        public LocalDate default9Act() {
            return ofFirstItem(IncomingInvoiceItem::getEndDate);
        }

        public Property default10Act() {
            return incomingInvoice.getProperty();
        }

        public Project default11Act() {
            return ofFirstItem(IncomingInvoiceItem::getProject);
        }

        public List<BudgetItem> choices12Act(
                final IncomingInvoiceType type,
                final Charge charge,
                final String description,
                final BigDecimal netAmount,
                final BigDecimal vatAmount,
                final BigDecimal grossAmount,
                final Tax tax,
                final LocalDate dueDate,
                final LocalDate startDate,
                final LocalDate endDate,
                final Property property,
                final Project project) {

            return budgetItemChooser.choicesBudgetItemFor(property, charge);
        }

        private <T> T ofFirstItem(final Function<IncomingInvoiceItem, T> f) {
            final Optional<IncomingInvoiceItem> firstItemIfAny = firstItemIfAny();
            return firstItemIfAny.map(f).orElse(null);
        }

        private Optional<IncomingInvoiceItem> firstItemIfAny() {
            return  incomingInvoice.getItems().stream()
                    .filter(IncomingInvoiceItem.class::isInstance)
                    .map(IncomingInvoiceItem.class::cast)
                    .findFirst();
        }

        @Inject
        BudgetItemChooser budgetItemChooser;

        @Inject
        IncomingInvoiceItemRepository incomingInvoiceItemRepository;

    }


    @Mixin(method="act")
    public static class changeBankAccount extends IncomingInvoice_triggerAbstract {

        private final IncomingInvoice incomingInvoice;

        public changeBankAccount(final IncomingInvoice incomingInvoice) {
            super(incomingInvoice, Arrays.asList(IncomingInvoiceApprovalState.NEW), null);
            this.incomingInvoice = incomingInvoice;
        }

        @Action(semantics = SemanticsOf.IDEMPOTENT)
        @ActionLayout(contributed= Contributed.AS_ACTION)
        public IncomingInvoice act(
                final BankAccount bankAccount,
                @Nullable final String comment){
            incomingInvoice.setBankAccount(bankAccount);
            trigger(comment);
            return  incomingInvoice;
        }

        public boolean hideAct() {
            return cannotTransition();
        }

    }

    /**
     * Default type, used for routing.
     *
     * <p>
     *     This can be overridden for each invoice item.
     * </p>
     */
    @Getter @Setter
    @Column(allowsNull = "false")
    private IncomingInvoiceType type;

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

    @Getter @Setter
    @Column(allowsNull = "true", name = "bankAccountId")
    private BankAccount bankAccount;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate dateReceived;

    @Getter @Setter
    @Column(allowsNull = "true", name="invoiceId")
    private IncomingInvoice relatesTo;

    // TODO: need to remove this from superclass, ie push down to InvoiceForLease subclass so not in this subtype
    @org.apache.isis.applib.annotation.Property(hidden = Where.EVERYWHERE)
    @Override
    public InvoiceStatus getStatus() {
        return super.getStatus();
    }


    @org.apache.isis.applib.annotation.Property(hidden = Where.ALL_TABLES)
    @javax.jdo.annotations.Column(scale = 2, allowsNull = "true")
    @Getter @Setter
    private BigDecimal netAmount;

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

    @Programmatic
    @Override
    public boolean isImmutable() {
        return reasonDisabledDueToState()!=null;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public IncomingInvoice editSeller(
            @Nullable
            final Party seller){
        setSeller(seller);
        return this;
    }

    public String disableEditSeller(){
        if (isImmutable()){
            return reasonDisabledDueToState();
        }
        return sellerIsImmutable() ? "Seller is immutable because an item is linked to an order" : null;
    }

    private boolean sellerIsImmutable(){
        for (InvoiceItem item : getItems()){
            IncomingInvoiceItem ii = (IncomingInvoiceItem) item;
            if (ii.isLinkedToOrderItem()){
                return true;
            }
        }
        return false;
    }

    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false")
    private IncomingInvoiceApprovalState approvalState;

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


    @Programmatic
    public String reasonDisabledDueToState() {
        final IncomingInvoiceApprovalState approvalState1 = getApprovalState();
        // guard for historic invoices (and invoice items)
        if (approvalState1==null){
            return "Cannot modify";
        }
        switch (approvalState1) {
        case NEW:
        case COMPLETED:
            return null;
        default:
            return "Cannot modify because invoice is in state of " + getApprovalState();
        }
    }

    @Programmatic
    public String reasonIncomplete(){
        if (getBankAccount() == null) {
            return "Bank account is required";
        }
        return null;
    }


    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;


}
