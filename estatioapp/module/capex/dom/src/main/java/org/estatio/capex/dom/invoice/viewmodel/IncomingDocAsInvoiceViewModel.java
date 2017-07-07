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

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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

import org.estatio.capex.dom.documents.viewmodel.IncomingDocViewModel;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.capex.dom.invoice.SellerBankAccountCreator;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderItemRepository;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.capex.dom.order.OrderItemService;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.dom.charge.Charge;
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

    @Property(editing = Editing.DISABLED)
    @PropertyLayout(named = "Incoming invoice")
    IncomingInvoice domainObject;

    @Property(editing = Editing.ENABLED)
    private IncomingInvoiceType incomingInvoiceType;

    //region > bankAccount (prop)
    @org.apache.isis.applib.annotation.Property(editing = Editing.ENABLED)
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
    private Boolean notCorrect;

    //region > orderItem (prop)

    @Property(editing = Editing.ENABLED)
    private OrderItem orderItem;
    public void modifyOrderItem(OrderItem orderItem) {
        setOrderItem(orderItem);
        autoFillIn();
    }

    public List<OrderItem> autoCompleteOrderItem(@MinLength(3) final String searchString){

        return orderItemService.searchOrderItem(
                searchString,
                getSeller(),
                getCharge(),
                getProject(),
                getProperty());

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
                setBankAccount(getFirstBankAccountOfPartyOrNull(order.getSeller()));
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
        setBankAccount(getFirstBankAccountOfPartyOrNull(seller));
    }


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
                @Nullable final PaymentMethod paymentMethod
        ){
            viewModel.setInvoiceNumber(invoiceNumber);
            viewModel.setBuyer(buyer);
            viewModel.setSeller(seller);
            viewModel.setBankAccount(viewModel.getFirstBankAccountOfPartyOrNull(seller));
            viewModel.setDateReceived(dateReceived);
            viewModel.setInvoiceDate(invoiceDate);
            viewModel.setDueDate(dueDate);
            if (dueInNumberOfDaysFromNow!=null){
                viewModel.setDueDate(clockService.now().plusDays(dueInNumberOfDaysFromNow));
            }
            viewModel.setPaymentMethod(paymentMethod);
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
            return viewModel.getPaymentMethod()==null
                    ? PaymentMethod.MANUAL_PROCESS
                    : viewModel.getPaymentMethod();
        }

        public String disableAct() {
            return viewModel.reasonNotEditableIfAny();
        }

        @Inject
        ClockService clockService;
    }


    private BankAccount getFirstBankAccountOfPartyOrNull(final Party party){
        return bankAccountRepository.findBankAccountsByOwner(party).size()>0 ?
                bankAccountRepository.findBankAccountsByOwner(party).get(0) : null;
    }

    protected String minimalRequiredDataToComplete(){
        StringBuffer buffer = new StringBuffer();
        if (getInvoiceNumber()==null){
            buffer.append("invoice number, ");
        }
        if (getBuyer()==null){
            buffer.append("buyer, ");
        }
        if (getSeller()==null){
            buffer.append("seller, ");
        }
        if (getDateReceived()==null){
            buffer.append("date received, ");
        }
        if (getDueDate()==null){
            buffer.append("due date, ");
        }
        if (getPaymentMethod()==null){
            buffer.append("payment method, ");
        }
        if (getNetAmount()==null){
            buffer.append("net amount, ");
        }
        if (getGrossAmount()==null){
            buffer.append("gross amount, ");
        }
        if (getPeriod()==null){
            buffer.append("period, ");
        }
        if (buffer.length()==0){
            return null;
        } else {
            return buffer.replace(buffer.length()-2, buffer.length(), " required").toString();
        }
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
        } else {
            incomingInvoiceItemRepository.create(
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
            IncomingInvoiceItem invoiceItemToLink = (IncomingInvoiceItem) incomingInvoice.getItems().first();
            orderItemInvoiceItemLinkRepository.findOrCreateLink(orderItemToLink, invoiceItemToLink);
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

        String propertyInvalidReason = getIncomingInvoiceType().validateProperty(getProperty());
        if(propertyInvalidReason != null) {
            return propertyInvalidReason;
        }
        String reasonDisabledDueToState = incomingInvoice.reasonDisabledDueToState();
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
                items.stream()
                        .filter(IncomingInvoiceItem.class::isInstance)
                        .map(IncomingInvoiceItem.class::cast)
                        .findFirst();
        return firstItemIfAny;
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
    OrderItemService orderItemService;

}
