package org.estatio.module.capex.spiimpl.docs.rml;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.xdocreport.dom.service.XDocReportModel;

import org.incode.module.document.dom.impl.applicability.RendererModelFactoryAbstract;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
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
                .contextModel(ContextModel.of(getClockService().now(), order.getAtPath()))
                .orderModel(OrderModel.of(order))
                .propertyModel(PropertyModel.of(order.getProperty()))
                .supplierModel(SupplierModel.of(order.getSeller()))
                .orderItemModels(
                        Lists.newArrayList(order.getItems()).stream()
                                .map(OrderItemModel::of)
                                .collect(Collectors.toList()))
                .build();
    }

    @Getter(AccessLevel.PROTECTED)
    @Inject
    ClockService clockService;

    @Data(staticConstructor = "of")
    public static class ContextModel  {

        @Getter(AccessLevel.PACKAGE)
        private final LocalDate currentDate;
        @Getter(AccessLevel.PACKAGE)
        private final String atPath;

        public String getCurrentDateLocalized() {
            if(atPath != null) {
                if(atPath.startsWith("/ITA")) {
                    return currentDate.toString("d MMMMM yyyy", Locale.ITALIAN);
                }
                if(atPath.startsWith("/FRA")) {
                    return currentDate.toString("d MMMMM yyyy", Locale.FRENCH);
                }
            }
            return currentDate.toString("d MMMMM yyyy", Locale.ENGLISH);
        }
    }

    @Data(staticConstructor = "of")
    public static class OrderModel  {

        @Getter(AccessLevel.PACKAGE)
        private final Order order;

        public String getOrderNumber() { return getOrder().getOrderNumber(); }

        public String getNetAmount() {
            return order.getNetAmount().setScale(2, RoundingMode.DOWN).toString();
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
            final Locale locale = Locale.ITALIAN;
            NumberFormat format = NumberFormat.getNumberInstance(locale);
            format.setMinimumFractionDigits(2);
            format.setMaximumFractionDigits(2);
            format.setCurrency(Currency.getInstance(locale));
            return format.format(orderItem.getNetAmount());
        }

    }

    @Data(staticConstructor = "of")
    public static class PropertyModel  {

        @Getter(AccessLevel.PACKAGE)
        private final Property property;

        public String getFullName() {
            return property != null ? property.getFullName() : "???";
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
    }

    @Data
    @Builder
    public static class DataModel implements XDocReportModel {

        private final ContextModel contextModel;
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
                    "context", Data.object(contextModel),
                    "order", Data.object(orderModel),
                    "supplier", Data.object(supplierModel),
                    "property", Data.object(propertyModel),
                    "orderItems", Data.list(orderItemModels, OrderItemModel.class)
            );

        }
    }
}
