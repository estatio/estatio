/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.capex.dom.invoice.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.schema.utils.jaxbadapters.JodaLocalDateStringAdapter;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.BuyerFinder;
import org.estatio.capex.dom.documents.viewmodel.IncomingDocViewModel;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.capex.dom.invoice.SellerBankAccountCreator;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderItemRepository;
import org.estatio.capex.dom.order.OrderItemInvoiceItemLinkValidationService;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Party;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@DomainObject(
        // WORKAROUND: using fqcn as objectType because Isis' invalidation of cache in prototyping mode causing NPEs in some situations
        objectType = "org.estatio.capex.dom.documents.categorisation.invoice.IncomingDocAsInvoiceViewModel"
)
@XmlRootElement(name = "incomingInvoiceViewModel")
@XmlType(
        propOrder = {
                "document",
                "invoiceNumber",
                "buyer",
                "seller",
                "bankAccount",
                "dateReceived",
                "invoiceDate",
                "dueDate",
                "paymentMethod",
                "currency",
                "description",
                "orderItem",
                "property",
                "project",
                "period",
                "budgetItem",
                "charge",
                "netAmount",
                "vatAmount",
                "tax",
                "grossAmount",
                "notCorrect",
                "domainObject",
                "incomingInvoiceType",
                "originatingTask"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@Getter @Setter
public class IncomingDocAsInvoiceViewModel
        extends IncomingDocViewModel<IncomingInvoice>
        implements SellerBankAccountCreator {

    /**
     * for unit testing
     */
    IncomingDocAsInvoiceViewModel() {
    }

    public IncomingDocAsInvoiceViewModel(final IncomingInvoice incomingInvoice, final Document document) {
        super(document);
        this.domainObject = incomingInvoice;
    }

    @Programmatic
    public IncomingInvoiceApprovalState getApprovalState(){
        return getDomainObject()!=null ? getDomainObject().getApprovalState() : null;
    }

    @Property(editing = Editing.DISABLED)
    @PropertyLayout(named = "Incoming invoice")
    IncomingInvoice domainObject;

    @Property(editing = Editing.ENABLED)
    private IncomingInvoiceType incomingInvoiceType;

    //region > bankAccount (prop)
    @Property(editing = Editing.ENABLED)
    private BankAccount bankAccount;

    public void modifyBankAccount(final BankAccount bankAccount){
        setBankAccount(bankAccount);
        setSeller(bankAccount.getOwner());
    }

    public List<BankAccount> autoCompleteBankAccount(@MinLength(3) final String searchString){
        if (getSeller()!=null){
            return bankAccountRepository.findBankAccountsByOwner(getSeller());
        } else {
            return bankAccountRepository.autoComplete(searchString);
        }
    }
    //endregion

    @Property(editing = Editing.ENABLED)
    private String invoiceNumber;

    //region > dateReceived (prop)

    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    @Property(editing = Editing.ENABLED)
    private LocalDate dateReceived;

    public LocalDate defaultDateReceived(){
        return getDateReceived()==null ? dateReceivedDerivedFromDocument() : getDateReceived();
    }

    private LocalDate dateReceivedDerivedFromDocument() {
        return getDocument().getCreatedAt().toLocalDate();
    }
    //endregion

    //region > invoiceDate (prop)

    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    @Property(editing = Editing.ENABLED)
    private LocalDate invoiceDate;

    public void modifyInvoiceDate(LocalDate invoiceDate){
        setInvoiceDate(invoiceDate);
        updateDueDate();
    }

    private void updateDueDate(){
        if (getInvoiceDate()!=null){
            setDueDate(getInvoiceDate().plusMonths(1));
        }
    }
    //endregion

    @XmlElement(required = false) @Nullable
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    @Property(editing = Editing.ENABLED)
    private LocalDate dueDate;

    //region > paymentMethod (prop)

    @Property(editing = Editing.ENABLED)
    private PaymentMethod paymentMethod;

    public PaymentMethod defaultPaymentMethod(){
        return getPaymentMethod();
    }
    //endregion


    @Property(editing = Editing.ENABLED)
    private Currency currency;


    @Property(editing = Editing.ENABLED)
    private Boolean notCorrect;

    //region > orderItem (prop)

    @Property(editing = Editing.ENABLED)
    private OrderItem orderItem;
    public void modifyOrderItem(OrderItem orderItem) {
        setOrderItem(orderItem);
        autoFillIn();
    }

    public List<OrderItem> choicesOrderItem(){
        // the disable guard ensures this is non-null
        final Party seller = getSeller();
        return orderItemRepository.findBySeller(seller);
    }

    public String disableOrderItem(){
        final Party seller = getSeller();
        if(seller == null) {
            return "Invoice's seller is required before items can be linked";
        }
        return null; // EST-1507: invoices item can be attached to order items any time
    }

    public String validateOrderItem(final OrderItem orderItem) {
        return linkValidationService.validateOrderItem(orderItem, this);
    }

    private void autoFillIn(){
        if (hasOrderItem()){
            Order order = orderRepository.findByOrderNumber(getOrderItem().getOrdr().getOrderNumber());
            OrderItem orderItem = orderItemRepository.findByOrderAndCharge(order, getOrderItem().getCharge());
            if (!(hasNetAmount() && hasGrossAmount() && hasVatAmount())){
                setNetAmount(orderItem.getNetAmount());
                setVatAmount(orderItem.getVatAmount());
                setGrossAmount(orderItem.getGrossAmount());
            }
            if (!hasTax()){
                setTax(orderItem.getTax());
            }
            if (!hasBuyer()){
                setBuyer(order.getBuyer());
            }
            if (!hasSeller()){
                setSeller(order.getSeller());
                setBankAccount(bankAccountRepository.getFirstBankAccountOfPartyOrNull(order.getSeller()));
            }
            if (!hasDescription()){
                setDescription(orderItem.getDescription());
            }
            if (!hasCharge()){
                setCharge(orderItem.getCharge());
            }
            if (!hasProject()){
                setProject(orderItem.getProject());
            }
            if (!hasProperty()){
                setProperty(orderItem.getProperty());
            }
            if (!hasBudgetItem()){
                setBudgetItem(orderItem.getBudgetItem());
            }
            if (!hasPeriod()){
                setPeriod(orderItem.getPeriod());
            }
        }
    }

    private boolean hasOrderItem(){
        return getOrderItem() != null;
    }

    //endregion

    @Override
    public void modifySeller(final Party seller){
        setSeller(seller);
        setBankAccount(bankAccountRepository.getFirstBankAccountOfPartyOrNull(seller));
    }

    /**
     * TODO: inline this mixin
     */
    @Mixin(method="act")
    public static class changeInvoiceDetails {
        private final IncomingDocAsInvoiceViewModel viewModel;
        public changeInvoiceDetails(final IncomingDocAsInvoiceViewModel viewModel) {
            this.viewModel = viewModel;
        }

        @Action(semantics = SemanticsOf.IDEMPOTENT)
        public IncomingDocAsInvoiceViewModel act(
                final String invoiceNumber,
                @Nullable final Party buyer,
                @Nullable final Party seller,
                final LocalDate dateReceived,
                @Nullable final LocalDate invoiceDate,
                @Nullable final LocalDate dueDate,
                @Nullable final Integer dueInNumberOfDaysFromNow,
                @Nullable final PaymentMethod paymentMethod,
                final Currency currency){
            viewModel.setInvoiceNumber(invoiceNumber);
            viewModel.setBuyer(buyer);
            viewModel.setSeller(seller);
            viewModel.setBankAccount(viewModel.bankAccountRepository.getFirstBankAccountOfPartyOrNull(seller));
            viewModel.setDateReceived(dateReceived);
            viewModel.setInvoiceDate(invoiceDate);
            viewModel.setDueDate(dueDate);
            if (dueInNumberOfDaysFromNow!=null){
                viewModel.setDueDate(clockService.now().plusDays(dueInNumberOfDaysFromNow));
            }
            viewModel.setPaymentMethod(paymentMethod);
            viewModel.setCurrency(currency);

            return viewModel;
        }

        public String default0Act(){
            return viewModel.getInvoiceNumber();
        }

        public Party default1Act(){
            return viewModel.getBuyer();
        }

        public Party default2Act(){
            return viewModel.getSeller();
        }

        public LocalDate default3Act(){
            return viewModel.getDateReceived()==null ? viewModel.dateReceivedDerivedFromDocument() : viewModel.getDateReceived();
        }

        public LocalDate default4Act(){
            return viewModel.getInvoiceDate();
        }

        public LocalDate default5Act(){
            return viewModel.getDueDate();
        }

        public PaymentMethod default7Act(){
            return viewModel.getPaymentMethod();
        }

        public Currency default8Act() {
            return viewModel.getCurrency();
        }
        public String disableAct() {
            return viewModel.reasonNotEditableIfAny();
        }

        @Inject
        ClockService clockService;
    }

    @Programmatic
    public void init() {
        IncomingInvoice incomingInvoice = getDomainObject();

        setIncomingInvoiceType(incomingInvoice.getType());
        setInvoiceNumber(incomingInvoice.getInvoiceNumber());
        setBuyer(incomingInvoice.getBuyer());
        setSeller(incomingInvoice.getSeller());
        setInvoiceDate(incomingInvoice.getInvoiceDate());
        setDueDate(incomingInvoice.getDueDate());
        setPaymentMethod(incomingInvoice.getPaymentMethod());
        setDateReceived(incomingInvoice.getDateReceived());
        setBankAccount(incomingInvoice.getBankAccount());
        setCurrency(incomingInvoice.getCurrency());

        final Optional<IncomingInvoiceItem> firstItemIfAny = getFirstItemIfAny();
        if(firstItemIfAny.isPresent()) {
            IncomingInvoiceItem invoiceItem = firstItemIfAny.get();
            setCharge(invoiceItem.getCharge());
            setDescription(invoiceItem.getDescription());
            setNetAmount(invoiceItem.getNetAmount());
            setVatAmount(invoiceItem.getVatAmount());
            setGrossAmount(invoiceItem.getGrossAmount());
            setTax(invoiceItem.getTax());
            setDueDate(invoiceItem.getDueDate());
            setPeriod(periodFrom(invoiceItem.getStartDate(), invoiceItem.getEndDate()));
            setProperty((org.estatio.dom.asset.Property) invoiceItem.getFixedAsset());
            setProject(invoiceItem.getProject());
            setBudgetItem(invoiceItem.getBudgetItem());

            List<OrderItemInvoiceItemLink> links =
                    orderItemInvoiceItemLinkRepository.findByInvoiceItem(invoiceItem);

            final Optional<OrderItemInvoiceItemLink> linkIfAny = links.stream().findFirst();
            linkIfAny.ifPresent(x -> setOrderItem(linkIfAny.get().getOrderItem()));

        } else {

            // for the first time we edit there will be no first item,
            // so we instead get the property from the header invoice
            setProperty(incomingInvoice.getProperty());
        }
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public IncomingInvoice save() {

        IncomingInvoice incomingInvoice = getDomainObject();

        IncomingInvoiceType previousType = incomingInvoice.getType();
        incomingInvoice.setType(getIncomingInvoiceType());
        incomingInvoice.setInvoiceNumber(getInvoiceNumber());
        incomingInvoice.setBuyer(getBuyer());
        incomingInvoice.setSeller(getSeller());
        incomingInvoice.setInvoiceDate(getInvoiceDate());
        incomingInvoice.setDueDate(getDueDate());
        incomingInvoice.setPaymentMethod(getPaymentMethod());
        incomingInvoice.setCurrency(getCurrency());
        incomingInvoice.setDateReceived(getDateReceived());
        incomingInvoice.setBankAccount(getBankAccount());

        incomingInvoice.setNetAmount(getNetAmount());
        incomingInvoice.setGrossAmount(getGrossAmount());

        // if changed the type, then we need to re-evaluate the state machine
        if(previousType != getIncomingInvoiceType()) {
            stateTransitionService.trigger(incomingInvoice, IncomingInvoiceApprovalStateTransition.class, null, null, null);
        }

        // upsert invoice item
        // this will also update the parent header's property with that from the first item
        Optional<IncomingInvoiceItem> firstItemIfAny = getFirstItemIfAny();
        IncomingInvoiceItem firstItem;

        if(firstItemIfAny.isPresent()) {
            IncomingInvoiceItem item = firstItemIfAny.get();
            item.setCharge(getCharge());
            item.setDescription(getDescription());
            item.setNetAmount(getNetAmount());
            item.setVatAmount(getVatAmount());
            item.setGrossAmount(getGrossAmount());
            item.setTax(getTax());
            item.setStartDate(getStartDateFromPeriod());
            item.setEndDate(getEndDateFromPeriod());
            item.setFixedAsset(getProperty());
            item.setProject(getProject());
            item.setBudgetItem(getBudgetItem());

            firstItem = item;
        } else {
            firstItem = incomingInvoiceItemRepository.create(
                    incomingInvoice.nextItemSequence(),
                    incomingInvoice,
                    incomingInvoiceType,
                    getCharge(),
                    getDescription(),
                    getNetAmount(),
                    getVatAmount(),
                    getGrossAmount(),
                    getTax(),
                    getDueDate(),
                    getStartDateFromPeriod(),
                    getEndDateFromPeriod(),
                    getProperty(),
                    getProject(),
                    getBudgetItem());
        }

        // link to orderItem if applicable
        // (if was changed, then will add to previous link, meaning that
        // 'switch view' will not be available subsequently because the Invoice/Item is "too complex")
        if (getOrderItem()!=null){
            Order order = getOrderItem().getOrdr();
            Charge chargeFromWrapper = getOrderItem().getCharge();
            OrderItem orderItemToLink = orderItemRepository.findByOrderAndCharge(order, chargeFromWrapper);
            orderItemInvoiceItemLinkRepository.findOrCreateLink(orderItemToLink, firstItem, firstItem.getNetAmount());
        } else {
            // remove all (or the one and only) link.
            final List<OrderItemInvoiceItemLink> links = orderItemInvoiceItemLinkRepository.findByInvoiceItem(firstItem);
            for (OrderItemInvoiceItemLink link : links) {
                link.remove();
            }
        }

        return incomingInvoice;
    }

    public String disableSave() {
        return reasonNotEditableIfAny();
    }


    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public IncomingInvoice cancel() {
        return getDomainObject();
    }


    @Override
    protected String reasonNotEditableIfAny() {
        IncomingInvoice incomingInvoice = getDomainObject();
        if (getIncomingInvoiceType() == null){
            return "Incoming invoice type is required";
        }
        String propertyInvalidReason = getIncomingInvoiceType().validateProperty(getProperty());
        if(propertyInvalidReason != null) {
            return propertyInvalidReason;
        }
        final Object viewContext = this;
        String reasonDisabledDueToState =
                incomingInvoice.reasonDisabledDueToState(viewContext);
        if(reasonDisabledDueToState != null) {
            return reasonDisabledDueToState;
        }

        SortedSet<InvoiceItem> items = incomingInvoice.getItems();
        if(items.size() > 1) {
            return "Only simple invoices with 1 item can be maintained using this view";
        }

        return null;
    }


    private Optional<IncomingInvoiceItem> getFirstItemIfAny() {
        SortedSet<InvoiceItem> items = getDomainObject().getItems();
        Optional<IncomingInvoiceItem> firstItemIfAny =
                Lists.newArrayList(items).stream()
                        .filter(IncomingInvoiceItem.class::isInstance)
                        .map(IncomingInvoiceItem.class::cast)
                        .findFirst();
        return firstItemIfAny;
    }

    @Property(editing = Editing.DISABLED)
    @PropertyLayout(multiLine = 5)
    public String getNotification(){
        final StringBuilder result = new StringBuilder();

        final String noBuyerBarcodeMatch = buyerBarcodeMatchValidation();
        if (noBuyerBarcodeMatch!=null){
            result.append(noBuyerBarcodeMatch);
        }

        final String sameInvoiceNumberCheck = doubleInvoiceCheck();
        if (sameInvoiceNumberCheck !=null){
            result.append(sameInvoiceNumberCheck);
        }

        return result.length()>0 ? result.toString() : null;

    }

    public boolean hideNotification(){
        return getNotification() == null;
    }

    private String doubleInvoiceCheck(){
        final String doubleInvoiceCheck = possibleDoubleInvoice();
        if (doubleInvoiceCheck !=null){
            return doubleInvoiceCheck;
        }
        final String sameNumberCheck = sameInvoiceNumber();
        if (sameNumberCheck !=null){
            return sameNumberCheck;
        }
        return null;
    }

    private String possibleDoubleInvoice(){
        if (getInvoiceNumber()==null || getSeller()==null || getInvoiceDate()==null){
            return null;
        }
        if (getDomainObject() == null) {
            return null;
        }
        IncomingInvoice possibleDouble = incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate(getInvoiceNumber(), getSeller(), getInvoiceDate());
        if (possibleDouble == null || possibleDouble.equals(domainObject)) {
            return null;
        }

        return "WARNING: There is already an invoice with the same number and invoice date for this seller. Please check.";
    }

    private String sameInvoiceNumber(){
        if (getInvoiceNumber()==null || getSeller()==null){
            return null;
        }
        if (getDomainObject()!=null){
            List<IncomingInvoice> similarNumberedInvoices = new ArrayList<>();
            for (IncomingInvoice invoice : incomingInvoiceRepository.findByInvoiceNumberAndSeller(getInvoiceNumber(), getSeller())) {
                if (!invoice.equals(getDomainObject())) {
                    similarNumberedInvoices.add(invoice);
                }
            }
            if (similarNumberedInvoices.size()>0){
                String message = "WARNING: Invoices with the same number of this seller are found ";
                for (IncomingInvoice invoice : similarNumberedInvoices){
                    if (invoice.getInvoiceDate()!=null) {
                        message = message.concat("on date ").concat(invoice.getInvoiceDate().toString()).concat("; ");
                    }
                }
                return message;
            }
        }
        return null;
    }

    private String buyerBarcodeMatchValidation(){
        if (getBuyer()!=null && getDomainObject()!=null){
            if (buyerFinder.buyerDerivedFromDocumentName(getDomainObject())==null){
                return null; // covers all cases where no buyer could be derived from document name
            }
            if (!getBuyer().equals(buyerFinder.buyerDerivedFromDocumentName(getDomainObject()))){
                return "Buyer does not match barcode (document name); ";
            }
        }
        return null;
    }

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    IncomingInvoiceItemRepository incomingInvoiceItemRepository;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    StateTransitionService stateTransitionService;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    OrderItemRepository orderItemRepository;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    OrderRepository orderRepository;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    FactoryService factoryService;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    OrderItemInvoiceItemLinkValidationService linkValidationService;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    BuyerFinder buyerFinder;

}
