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

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.dom.asset.FixedAssetRole;
import org.estatio.dom.asset.FixedAssetRoleRepository;
import org.estatio.dom.asset.FixedAssetRoleType;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.party.OrganisationRepository;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.project.Project;
import org.estatio.dom.project.ProjectRepository;
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
                "invoiceDescription",
                "invoiceNetAmount",
                "invoiceVatAmount",
                "invoiceGrossAmount",
                "invoiceTax"
        }
)
@DomainObject(
        objectType = "org.estatio.app.services.order.OrderInvoiceLine"
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

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "18")
    private String invoiceDescription;

    @XmlElement(required = false)
    @Getter @Setter
    @Column(scale = 2)
    @MemberOrder(sequence = "19")
    private BigDecimal invoiceNetAmount;

    @XmlElement(required = false)
    @Getter @Setter
    @Column(scale = 2)
    @MemberOrder(sequence = "20")
    private BigDecimal invoiceVatAmount;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "21")
    @Column(scale = 2)
    private BigDecimal invoiceGrossAmount;

    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "22")
    private String invoiceTax;


    /**
     * Using a mixin so can continue to use lombok's @AllArgsConstructor
     * (else the additional injected services required confuse things)
     */
    @Mixin(method="act")
    public static class _apply {
        private final OrderInvoiceLine line;
        public _apply(final OrderInvoiceLine line) {
            this.line = line;
        }
        @Action()
        @ActionLayout(contributed= Contributed.AS_ACTION)
        public OrderInvoiceLine act() {
            final String orderNumber = determineOrderNumber();
            final Party supplier = determineSupplier();
            final Project project = lookupProject();
            final Property property = inferPropertyFrom(line.projectReference);
            final Party buyer =  deriveBuyerFrom(property);
            final String atPath = "/FRA";

            final LocalDate startDate = determineStartDateFrom(line.period);
            final LocalDate endDate = startDate.plusYears(1).minusDays(1);

            if(property == null) {
                return line;
            }

            final boolean isOrder = line.entryDate != null;
            final boolean isInvoice = line.invoiceNumber != null;

            final Charge chargeObj = chargeRepository.findByReference(line.charge);

            if(isOrder) {
                Order order = orderRepository.findOrCreate(
                        orderNumber,
                        line.getSeller(),
                        line.entryDate,
                        line.orderDate,
                        supplier,
                        buyer,
                        atPath,
                        line.orderApprovedBy,
                        line.orderApprovedOn);

                final Tax tax = taxRepository.findByReference(line.tax);

                order.addItem(
                        chargeObj, line.orderDescription,
                        line.netAmount, line.vatAmount, line.grossAmount,
                        tax, startDate, endDate, property, project);
            }

            if(isInvoice) {
                final LocalDate invoiceDate = line.getOrderDate();
                final LocalDate dueDate = line.getOrderDate();
                IncomingInvoice invoice = incomingInvoiceRepository.findOrCreate(line.getInvoiceNumber(), atPath, buyer, supplier, invoiceDate, dueDate);

                final IncomingInvoice invoiceObj = incomingInvoiceRepository.findByInvoiceNumber(line.getInvoiceNumber());
                final Tax invoiceTax = taxRepository.findByReference(line.invoiceTax);

                invoice.addItem(invoiceObj, chargeObj, line.getInvoiceDescription(), line.getInvoiceNetAmount(), line.getInvoiceVatAmount(), line.getInvoiceGrossAmount(), invoiceTax, startDate, endDate, property, project);
            }

            return line;
        }

        private Party deriveBuyerFrom(final Property property) {
            FixedAssetRole role = fixedAssetRoleRepository.findRole(property, FixedAssetRoleType.PROPERTY_OWNER);
            return role!=null ? role.getParty() : null;
        }

        public String disableAct() {
            return "OK".equals(line.status) ? null : "Cannot apply: " + line.status;
        }

        private LocalDate determineStartDateFrom(final String period) {
            return new LocalDate(Integer.parseInt(period.substring(1)), 7, 1);
        }

        private Party determineSupplier() {
            Party party = partyRepository.matchPartyByReferenceOrName(line.seller);
            Country france = countryRepository.findCountry("FRA");

            if (party==null){

                RandomCodeGenerator10Chars generator = new RandomCodeGenerator10Chars();
                String orgReference = generator.generateRandomCode();

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
            final BigDecimal budgetedAmount = null;
            final String atPath = "/FRA";
            final Project parent = null;

            return projectRepository.findOrCreate(reference, name, startDate, endDate, budgetedAmount, atPath, parent);
        }

        String determineOrderNumber() {
            Integer counter = 1;
            String suffix = "-".concat(String.format("%03d", counter));
            String result = line.getOrderDate().toString().replace("-","").concat(suffix);
            while (orderRepository.findByOrderNumber(result)!=null){
                counter = counter + 1;
                suffix = "-".concat(String.format("%03d", counter));
                result = line.getOrderDate().toString().replace("-","").concat(suffix);
            }
            return result;
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

