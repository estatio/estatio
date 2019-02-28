package org.estatio.module.capex.spiimpl.docs.rml;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.factory.FactoryService;

import org.isisaddons.module.xdocreport.dom.service.XDocReportModel;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelPurposeType;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.PostalAddress;
import org.incode.module.document.dom.impl.applicability.RendererModelFactoryAbstract;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.base.dom.CurrencyUtil;
import org.estatio.module.base.dom.LocaleUtil;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.order.dom.attr.prop.Order_introduction;
import org.estatio.module.order.dom.attr.prop.Order_orderDescription;
import org.estatio.module.order.dom.attr.prop.Order_priceAndPayments;
import org.estatio.module.order.dom.attr.prop.Order_signature;
import org.estatio.module.order.dom.attr.prop.Order_subject;
import org.estatio.module.order.dom.attr.prop.Order_totalWorkCost;
import org.estatio.module.order.dom.attr.prop.Order_workSchedule;
import org.estatio.module.party.dom.Party;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

public class RendererModelFactoryForOrder extends RendererModelFactoryAbstract<Order> {

    public RendererModelFactoryForOrder() {
        super(Order.class);
    }

    @Override
    protected Object doNewRendererModel(
            final DocumentTemplate documentTemplate, final Order order) {

        // don't expose entities directly to XDocReportService because it takes forever to traverse the object graph.
        // instead, we expose only view models.
        return DataModel.builder()
                .letterModel(newLetterModel(order))
                .orderModel(newOrderModel(order))
                .propertyModel(newPropertyModel(order))
                .supplierModel(newSupplierModel(order))
                .orderItemModels(newOrderITemModels(order))
                .build();
    }

    protected LetterModel newLetterModel(final Order order) {
        return LetterModel.of(getClockService().now(), order.getAtPath(),
                factoryService.mixin(Order_subject.class, order).prop(),
                factoryService.mixin(Order_introduction.class, order).prop(),
                factoryService.mixin(Order_orderDescription.class, order).prop(),
                factoryService.mixin(Order_totalWorkCost.class, order).prop(),
                factoryService.mixin(Order_workSchedule.class, order).prop(),
                factoryService.mixin(Order_priceAndPayments.class, order).prop(),
                factoryService.mixin(Order_signature.class, order).prop()
        );
    }

    protected OrderModel newOrderModel(final Order order) {
        return OrderModel.of(order);
    }

    protected PropertyModel newPropertyModel(final Order order) {
        return PropertyModel.of(order.getProperty());
    }

    protected SupplierModel newSupplierModel(final Order order) {
        return SupplierModel.of(order.getSeller(), addressOf(order.getSeller()));
    }

    protected List<OrderItemModel> newOrderITemModels(final Order order) {
        return Lists.newArrayList(order.getItems()).stream()
                .map(OrderItemModel::of)
                .collect(Collectors.toList());
    }

    protected String addressOf(final Party party) {
        if(party == null) {
            return "???";
        }
        final SortedSet<CommunicationChannel> channels =
                communicationChannelRepository.findByOwnerAndType(party, CommunicationChannelType.POSTAL_ADDRESS);

        return channels.stream()
                .filter(PostalAddress.class::isInstance)
                .map(PostalAddress.class::cast)
                .filter(x -> x.getPurpose() == CommunicationChannelPurposeType.INVOICING)
                .findFirst()
                .map(PostalAddress::asAddressLabel)
                .orElse("???");
    }

    @Getter(AccessLevel.PROTECTED)
    @Inject
    CommunicationChannelRepository communicationChannelRepository;

    @Getter(AccessLevel.PROTECTED)
    @Inject
    ClockService clockService;

    @Getter(AccessLevel.PROTECTED)
    @Inject
    FactoryService factoryService;

    @Data(staticConstructor = "of")
    public static class LetterModel {

        @Getter(AccessLevel.PACKAGE)
        private final LocalDate currentDate;

        public String getCurrentDateLocalized() {
            Locale locale = LocaleUtil.deriveLocale(atPath);
            return currentDate.toString("d MMMMM yyyy", locale);
        }

        private final String atPath;
        private final String subject;
        private final String introduction;
        private final String orderDescription;
        private final String totalWorkCost;
        private final String workSchedule;
        private final String priceAndPayments;
        private final String signature;
    }

    @Data(staticConstructor = "of")
    public static class OrderModel  {

        @Getter(AccessLevel.PACKAGE)
        private final Order order;

        public String getOrderNumber() { return getOrder().getOrderNumber(); }

        public String getNetAmount() {
            return CurrencyUtil.formattedAmount(order.getNetAmount(), order.getAtPath());
        }

    }

    @Data(staticConstructor = "of")
    public static class OrderItemModel  {

        @Getter(AccessLevel.PACKAGE)
        private final OrderItem orderItem;

        public String getDescription() {
            return orderItem.getDescription();
        }
        public String getNetAmount() {
            return CurrencyUtil.formattedAmount(orderItem.getNetAmount(), orderItem.getAtPath());
        }

    }

    @Data(staticConstructor = "of")
    public static class PropertyModel  {

        @Getter(AccessLevel.PACKAGE)
        private final Property property;

        public String getFullName() {
            return property != null
                    ? property.getFullName() != null
                        ? property.getFullName()
                        : property.getName()
                    : "???";
        }

    }

    @Data(staticConstructor = "of")
    public static class SupplierModel  {

        @Getter(AccessLevel.PACKAGE)
        private final Party party;

        public String getName() {
            final String partyName = party != null ? party.getName() : "???";
            return Util.capitalizeSentence(partyName);
        }
        public String getNameNoSrlSuffix() {
            return Util.stripSrlSuffix(getName());
        }
        public String getNameSrlSuffix() {
            return Util.handleSrlSuffix(getName());
        }

        public String getReference() {
            return party != null ? party.getReference() : "???";
        }

        private final String address;

    }

    @Data
    @Builder
    public static class DataModel implements XDocReportModel {

        private final LetterModel letterModel;
        private final OrderModel orderModel;
        private final SupplierModel supplierModel;
        private final PropertyModel propertyModel;
        private final List<OrderItemModel> orderItemModels;

        // for freemarker
        public String getOrderNumber() { return orderModel.getOrderNumber(); }

        // for freemarker
        public LocalDate getOrderDate() { return orderModel.getOrder().getOrderDate(); }

        @Override
        public Map<String, Data> getContextData() {
            return ImmutableMap.of(
                    "letter", Data.object(letterModel),
                    "order", Data.object(orderModel),
                    "supplier", Data.object(supplierModel),
                    "property", Data.object(propertyModel),
                    "orderItems", Data.list(orderItemModels, OrderItemModel.class)
            );

        }
    }
}
