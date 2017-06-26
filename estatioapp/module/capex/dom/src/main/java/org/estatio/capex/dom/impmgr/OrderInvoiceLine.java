package org.estatio.capex.dom.impmgr;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.message.MessageService;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderItem;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.capex.dom.orderinvoice.OrderItemInvoiceItemLinkRepository;
import org.estatio.capex.dom.project.Project;
import org.estatio.capex.dom.project.ProjectRepository;
import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.asset.role.FixedAssetRole;
import org.estatio.dom.asset.role.FixedAssetRoleRepository;
import org.estatio.dom.asset.role.FixedAssetRoleTypeEnum;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.OrganisationRepository;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "orderInvoiceLine")
@XmlType(
        propOrder = {
                "sheetName",
                "rowNumber",
                "status",
                "sellerOrderReference",
                "charge",
                "orderNumber",
                "entryDate",
                "orderDate",
                "seller",
                "orderDescription",
                "netAmount",
                "vatAmount",
                "grossAmount",
                "orderApprovedBy",
                "orderApprovedOn",
                "projectReference",
                "period",
                "tax",
                "invoiceNumber",
                "invoiceType",
                "invoiceDescription",
                "invoiceNetAmount",
                "invoiceVatAmount",
                "invoiceGrossAmount",
                "invoiceTax"
        }
)
@DomainObject(
        objectType = "orders.OrderInvoiceLine"
)
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
public class OrderInvoiceLine {

    public String title() {
        return "Order - Invoice Import Line";
    }

    public OrderInvoiceLine() {}

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String sheetName;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "2")
    private Integer rowNumber;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "3")
    private String status;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "4")
    private String charge;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "4")
    private String orderNumber;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "4")
    private String sellerOrderReference;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "5")
    private LocalDate entryDate;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "6")
    private LocalDate orderDate;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "7")
    private String seller;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "8")
    private String orderDescription;

    @XmlElement(required = false)
    @Getter @Setter
    @Column(scale = 2)
    @MemberOrder(sequence = "9")
    private BigDecimal netAmount;

    @XmlElement(required = false)
    @Getter @Setter
    @Column(scale = 2)
    @MemberOrder(sequence = "10")
    private BigDecimal vatAmount;

    @XmlElement(required = false)
    @Getter @Setter
    @Column(scale = 2)
    @MemberOrder(sequence = "11")
    private BigDecimal grossAmount;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "12")
    private String orderApprovedBy;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "13")
    private LocalDate orderApprovedOn;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "14")
    private String projectReference;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "15")
    private String period;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "16")
    private String tax;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "17")
    private String invoiceNumber;

    /**
     * Corresponding to {@link IncomingInvoiceType}.
     */
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "18")
    private String invoiceType;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "19")
    private String invoiceDescription;

    @XmlElement(required = false)
    @Getter @Setter
    @Column(scale = 2)
    @MemberOrder(sequence = "20")
    private BigDecimal invoiceNetAmount;

    @XmlElement(required = false)
    @Getter @Setter
    @Column(scale = 2)
    @MemberOrder(sequence = "21")
    private BigDecimal invoiceVatAmount;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "22")
    @Column(scale = 2)
    private BigDecimal invoiceGrossAmount;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "23")
    private String invoiceTax;


    /**
     * Using a mixin so can continue to use lombok's @AllArgsConstructor
     * (else the additional injected services required confuse things)
     */
    @Mixin(method="act")
    public static class _apply {
        private static final Logger LOG = LoggerFactory.getLogger(OrderInvoiceLine.class);
        private final OrderInvoiceLine line;
        public _apply(final OrderInvoiceLine line) {
            this.line = line;
        }

        @Action()
        @ActionLayout(contributed= Contributed.AS_ACTION)
        public OrderInvoiceLine act() {
            final Party supplier = determineSupplier();
            final Project project = lookupProject();
            final Property property = inferPropertyFrom(line.projectReference);
            final Party buyer =  deriveBuyerFrom(property);
            final String atPath = "/FRA";

            final LocalDate startDate = PeriodUtil.yearFromPeriod(line.period).startDate();
            final LocalDate endDate = PeriodUtil.yearFromPeriod(line.period).endDate();

            if(property == null) {
                return line;
            }

            final boolean isOrder = line.entryDate != null;
            final boolean isInvoice = line.invoiceNumber != null;

            final Charge chargeObj = chargeRepository.findByReference(line.charge);

            if(isOrder) {
                Order order = orderRepository.upsert(
                        property, line.getOrderNumber(),
                        line.getSeller(),
                        line.entryDate,
                        line.orderDate,
                        supplier,
                        buyer,
                        atPath
                );

                final Tax tax = taxRepository.findByReference(line.tax);

                factoryService.mixin(Order.addItem.class, order).act(
                        chargeObj, line.orderDescription,
                        line.netAmount, line.vatAmount, line.grossAmount,
                        tax, startDate, endDate, property, project, null);

                project.addItem(chargeObj, chargeObj.getDescription(), null, null, null, property, null);
            }

            IncomingInvoice invoice = null;

            if(isInvoice) {
                final LocalDate invoiceDate = line.getOrderDate();
                final LocalDate dueDate = line.getOrderDate();
                final PaymentMethod paymentMethod = PaymentMethod.BANK_TRANSFER; // assumed for Capex
                final InvoiceStatus invoiceStatus = InvoiceStatus.APPROVED;      // migrating historic data...
                IncomingInvoiceApprovalState paid = IncomingInvoiceApprovalState.PAID; // migrating historic data

                invoice = incomingInvoiceRepository.upsert(
                        IncomingInvoiceType.parse(line.invoiceType), line.getInvoiceNumber(), property, atPath, buyer, supplier, invoiceDate, dueDate, paymentMethod,
                        invoiceStatus, null, null, paid);

                final IncomingInvoice invoiceObj = incomingInvoiceRepository.findByInvoiceNumberAndSellerAndInvoiceDate(line.getInvoiceNumber(), supplier, invoiceDate);
                final Tax invoiceTax = taxRepository.findByReference(line.getInvoiceTax());


                factoryService.mixin(IncomingInvoice.addItem.class, invoiceObj).act(
                        chargeObj, line.getInvoiceDescription(),
                        line.getInvoiceNetAmount(), line.getInvoiceVatAmount(), line.getInvoiceGrossAmount(),
                        invoiceTax,
                        dueDate, startDate, endDate, property, project, null);

            }

            // match invoice item to order item
            if(isInvoice && invoice!=null) {
                Order order = orderRepository.findByOrderNumber(line.getOrderNumber());
                if (order!=null) {
                    OrderItem orderItem = order.getItems().first();
                    IncomingInvoiceItem invoiceItem = (IncomingInvoiceItem) invoice.getItems().first();
                    orderItemInvoiceItemLinkRepository.findOrCreateLink(orderItem, invoiceItem);
                } else {
                    final String message = String.format("Matching order for invoice with number %s not found", line.getInvoiceNumber()) ;
                    messageService.warnUser(message);
                    LOG.info(message);
                }
            }

            return line;
        }

        private Party deriveBuyerFrom(final Property property) {
            FixedAssetRole role = fixedAssetRoleRepository.findRole(property, FixedAssetRoleTypeEnum.PROPERTY_OWNER);
            return role!=null ? role.getParty() : null;
        }

        public String disableAct() {
            return "OK".equals(line.status) ? null : "Cannot apply: " + line.status;
        }

        private Party determineSupplier() {
            Party party = partyRepository.matchPartyByReferenceOrName(line.seller);
            Country france = countryRepository.findCountry("FRA");

            if (party==null){

                RandomCodeGenerator10Chars generator = new RandomCodeGenerator10Chars();
                String orgReference = "FRIMP_".concat(generator.generateRandomCode());

                boolean useNumeratorForReference = false;
                String name = line.seller;
                Country country = france;

                party = organisationRepository.newOrganisation(orgReference.toUpperCase(), useNumeratorForReference, name, country);
            }
            return party;
        }

        private final Pattern projectReferencePattern = Pattern.compile("^([^-]+)[-].*$");
        private Property inferPropertyFrom(final String projectReference) {
            final Matcher matcher = projectReferencePattern.matcher(projectReference);
            if(!matcher.matches()) {
                return null;
            }
            final String propertyReference = matcher.group(1);
            return propertyRepository.findPropertyByReference(propertyReference);
        }

        private Project lookupProject() {

            final String reference = line.projectReference;
            final String name = line.projectReference;
            final LocalDate startDate = null;
            final LocalDate endDate = null;
            final String atPath = "/FRA";
            final Project parent = null;

            return projectRepository.findOrCreate(reference, name, startDate, endDate, atPath, parent);
        }

        @Inject
        OrderRepository orderRepository;
        @Inject
        PropertyRepository propertyRepository;
        @Inject
        ProjectRepository projectRepository;
        @Inject
        ChargeRepository chargeRepository;
        @Inject
        TaxRepository taxRepository;
        @Inject
        PartyRepository partyRepository;
        @Inject
        OrganisationRepository organisationRepository;
        @Inject
        CountryRepository countryRepository;
        @Inject
        IncomingInvoiceRepository incomingInvoiceRepository;
        @Inject
        FixedAssetRoleRepository fixedAssetRoleRepository;
        @Inject
        OrderItemInvoiceItemLinkRepository orderItemInvoiceItemLinkRepository;
        @Inject
        MessageService messageService;
        @Inject
        FactoryService factoryService;


        public static class RandomCodeGenerator10Chars {

            private static final int NUMBER_CHARACTERS = 10;
            private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

            public String generateRandomCode() {
                final StringBuilder buf = new StringBuilder(NUMBER_CHARACTERS);
                for (int i = 0; i < NUMBER_CHARACTERS; i++) {
                    final int pos = (int) ((Math.random() * CHARACTERS.length()));
                    buf.append(CHARACTERS.charAt(pos));
                }
                return buf.toString();
            }
        }
    }

}

