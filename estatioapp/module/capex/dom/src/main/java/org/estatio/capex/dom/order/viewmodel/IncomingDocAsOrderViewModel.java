package org.estatio.capex.dom.order.viewmodel;

import java.util.Optional;

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
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.dom.party.Party;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@DomainObject(
        // WORKAROUND: using fqcn as objectType because Isis' invalidation of cache in prototyping mode causing NPEs in some situations
        objectType = "org.estatio.capex.dom.documents.categorisation.order.IncomingDocAsOrderViewModel",
        editing = Editing.ENABLED
)
@XmlRootElement(name = "incomingOrderViewModel")
@XmlType(
        propOrder = {
                "document",
                "orderNumber",
                "buyer",
                "seller",
                "sellerOrderReference",
                "orderDate",
                "description",
                "charge",
                "property",
                "project",
                "period",
                "budgetItem",
                "netAmount",
                "vatAmount",
                "tax",
                "grossAmount",
                "domainObject",
                "originatingTask"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@Getter @Setter
public class IncomingDocAsOrderViewModel extends IncomingDocViewModel<Order> {


    /**
     * for unit testing
     */
    IncomingDocAsOrderViewModel() {}

    public IncomingDocAsOrderViewModel(final Order order, final Document document) {
        super(document);
        this.domainObject = order;
    }

    @Property(editing = Editing.DISABLED)
    @PropertyLayout(named = "Order")
    Order domainObject;

    @Property(editing = Editing.ENABLED)
    private String orderNumber;

    @Property(editing = Editing.ENABLED)
    private String sellerOrderReference;

    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    @Property(editing = Editing.ENABLED)
    private LocalDate orderDate;


    @Mixin(method="act")
    public static class changeOrderDetails {
        private final IncomingDocAsOrderViewModel viewModel;
        public changeOrderDetails(final IncomingDocAsOrderViewModel viewModel) {
            this.viewModel = viewModel;
        }
        @Action(semantics = SemanticsOf.IDEMPOTENT)
        public IncomingDocAsOrderViewModel act(
                final String orderNumber,
                final Party buyer,
                final Party seller,
                @Nullable
                final String sellerOrderReference,
                @Nullable
                final LocalDate orderDate
        ){
            viewModel.setOrderNumber(orderNumber);
            viewModel.setBuyer(buyer);
            viewModel.setSeller(seller);
            viewModel.setSellerOrderReference(sellerOrderReference);
            viewModel.setOrderDate(orderDate);
            return viewModel;
        }

        public String default0Act(){
            return viewModel.getOrderNumber();
        }

        public Party default1Act(){
            return viewModel.getBuyer();
        }

        public Party default2Act(){
            return viewModel.getSeller();
        }

        public String default3Act(){
            return viewModel.getSellerOrderReference();
        }

        public LocalDate default4Act(){
            return viewModel.getOrderDate();
        }

        public String disableAct() {
            return viewModel.reasonNotEditableIfAny();
        }

    }


    @Programmatic
    public String minimalRequiredDataToComplete(){
        StringBuilder buffer = new StringBuilder();
        if (getOrderNumber()==null){
            buffer.append("order number, ");
        }
        if (getBuyer()==null){
            buffer.append("buyer, ");
        }
        if (getSeller()==null){
            buffer.append("seller, ");
        }
        if (getDescription()==null){
            buffer.append("description, ");
        }
        if (getNetAmount()==null){
            buffer.append("net amount, ");
        }
        if (getGrossAmount()==null){
            buffer.append("gross amount, ");
        }
        if (getCharge()==null){
            buffer.append("charge, ");
        }
        if (getPeriod()==null){
            buffer.append("period, ");
        }
        final int buflen = buffer.length();
        return buflen != 0
                ? buffer.replace(buflen - 2, buflen, " required").toString()
                : null;
    }


    @Programmatic
    public void init() {

        final Order order = getDomainObject();
        setOrderNumber(order.getOrderNumber());
        setSellerOrderReference(order.getSellerOrderReference());
        setOrderDate(order.getOrderDate());
        setSeller(order.getSeller());
        setBuyer(order.getBuyer());
        setProperty(order.getProperty());

        final Optional<OrderItem> firstItemIfAny = getFirstItemIfAny();
        if(firstItemIfAny.isPresent()) {
            OrderItem orderItem = firstItemIfAny.get();

            setCharge(orderItem.getCharge());
            setDescription(orderItem.getDescription());
            setNetAmount(orderItem.getNetAmount());
            setVatAmount(orderItem.getVatAmount());
            setGrossAmount(orderItem.getGrossAmount());
            setTax(orderItem.getTax());
            setPeriod(periodFrom(orderItem.getStartDate(), orderItem.getEndDate()));
            setProperty(orderItem.getProperty());
            setProject(orderItem.getProject());
            setBudgetItem(orderItem.getBudgetItem());
        }
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public Order cancel() {
        return getDomainObject();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Order save() {

        Order order = getDomainObject();
        order.setOrderNumber(getOrderNumber());
        order.setSellerOrderReference(getSellerOrderReference());
        order.setEntryDate(clockService.now());
        order.setOrderDate(getOrderDate());
        order.setSeller(getSeller());
        order.setBuyer(getBuyer());

        Optional<OrderItem> firstItemIfAny = getFirstItemIfAny();
        if(firstItemIfAny.isPresent()) {

            OrderItem orderItem = firstItemIfAny.get();
            orderItem.setCharge(getCharge());
            orderItem.setDescription(getDescription());
            orderItem.setNetAmount(getNetAmount());
            orderItem.setVatAmount(getVatAmount());
            orderItem.setGrossAmount(getGrossAmount());
            orderItem.setTax(getTax());
            orderItem.setStartDate(getStartDateFromPeriod());
            orderItem.setEndDate(getEndDateFromPeriod());
            orderItem.setProperty(getProperty());
            orderItem.setProject(getProject());
            orderItem.setBudgetItem(getBudgetItem());
        } else {
            factoryService.mixin(Order.addItem.class, order).act(
                    getCharge(),
                    getDescription(),
                    getVatAmount(),
                    getNetAmount(),
                    getGrossAmount(),
                    getTax(),
                    getStartDateFromPeriod(),
                    getEndDateFromPeriod(),
                    getProperty(),
                    getProject(),
                    getBudgetItem()
            );
        }

        return order;
    }

    public String disableSave() {
        return reasonNotEditableIfAny();
    }

    @Override
    protected String reasonNotEditableIfAny() {
        Order order = getDomainObject();

        String reasonDisabledDueToState = order.reasonDisabledDueToState();
        if(reasonDisabledDueToState != null) {
            return reasonDisabledDueToState;
        }

        return null;
    }

    private Optional<OrderItem> getFirstItemIfAny() {
        return getDomainObject().getItems().stream().findFirst();
    }

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    ClockService clockService;

    @Inject
    @XmlTransient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    FactoryService factoryService;

}
