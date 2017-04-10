package org.estatio.app.services.order;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
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

import org.estatio.capex.dom.charge.IncomingCharge;
import org.estatio.capex.dom.charge.IncomingChargeRepository;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.party.OrganisationRepository;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.project.Project;
import org.estatio.dom.project.ProjectRepository;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRepository;

import lombok.AllArgsConstructor;
import lombok.Setter;

@XmlRootElement(name = "orderInvoiceLine")
@XmlType(
        propOrder = {
                "sheetName",
                "rowNumber",
                "status",
                "supplierName",
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
@AllArgsConstructor
public class OrderInvoiceLine {

    public String title() {
        return "Order - Invoice Import Line";
    }

    public OrderInvoiceLine() {}

    @Setter
    @MemberOrder(sequence = "1")
    private String sheetName;

    @XmlElement(required = false)
    public String getSheetName() {
        return sheetName;
    }

    @Setter
    @MemberOrder(sequence = "2")
    private Integer rowNumber;

    @XmlElement(required = false)
    public Integer getRowNumber() {
        return rowNumber;
    }

    @Setter
    @MemberOrder(sequence = "3")
    private String status;

    @XmlElement(required = false)
    public String getStatus() {
        return status;
    }

    @Setter
    private String supplierName;

    @XmlElement(required = false)
    public String getSupplierName() {
        return supplierName;
    }

    @Setter
    @MemberOrder(sequence = "4")
    private String charge;

    @XmlElement(required = false)
    public String getCharge() {
        return charge;
    }


    @Setter
    @MemberOrder(sequence = "5")
    private LocalDate entryDate;

    @XmlElement(required = false)
    public LocalDate getEntryDate() {
        return entryDate;
    }

    @Setter
    @MemberOrder(sequence = "6")
    private LocalDate orderDate;

    @XmlElement(required = false)
    public LocalDate getOrderDate() {
        return orderDate;
    }

    @Setter
    @MemberOrder(sequence = "7")
    private String seller;

    @XmlElement(required = false)
    public String getSeller() {
        return seller;
    }

    @Setter
    @MemberOrder(sequence = "8")
    private String orderDescription;

    @XmlElement(required = false)
    public String getOrderDescription() {
        return orderDescription;
    }

    @Setter
    @Column(scale = 2)
    @MemberOrder(sequence = "9")
    private BigDecimal netAmount;

    @XmlElement(required = false)
    public BigDecimal getNetAmount() {
        return netAmount;
    }

    @Setter
    @Column(scale = 2)
    @MemberOrder(sequence = "10")
    private BigDecimal vatAmount;

    @XmlElement(required = false)
    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    @Setter
    @Column(scale = 2)
    @MemberOrder(sequence = "11")
    private BigDecimal grossAmount;

    @XmlElement(required = false)
    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    @Setter
    @MemberOrder(sequence = "12")
    private String orderApprovedBy;

    @XmlElement(required = false)
    public String getOrderApprovedBy() {
        return orderApprovedBy;
    }

    @Setter
    @MemberOrder(sequence = "13")
    private LocalDate orderApprovedOn;

    @XmlElement(required = false)
    public LocalDate getOrderApprovedOn() {
        return orderApprovedOn;
    }

    @Setter
    @MemberOrder(sequence = "14")
    private String projectReference;

    @XmlElement(required = false)
    public String getProjectReference() {
        return projectReference;
    }

    @Setter
    @MemberOrder(sequence = "15")
    private String period;

    @XmlElement(required = false)
    public String getPeriod() {
        return period;
    }

    @Setter
    @MemberOrder(sequence = "16")
    private String tax;

    @XmlElement(required = false)
    public String getTax() {
        return tax;
    }

    @Setter
    @MemberOrder(sequence = "17")
    private String invoiceNumber;

    @XmlElement(required = false)
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    @Setter
    @MemberOrder(sequence = "18")
    private String invoiceDescription;

    @XmlElement(required = false)
    public String getInvoiceDescription() {
        return invoiceDescription;
    }

    @Setter
    @Column(scale = 2)
    @MemberOrder(sequence = "19")
    private BigDecimal invoiceNetAmount;

    @XmlElement(required = false)
    public BigDecimal getInvoiceNetAmount() {
        return invoiceNetAmount;
    }

    @Setter
    @Column(scale = 2)
    @MemberOrder(sequence = "20")
    private BigDecimal invoiceVatAmount;

    @XmlElement(required = false)
    public BigDecimal getInvoiceVatAmount() {
        return invoiceVatAmount;
    }

    @Setter
    @MemberOrder(sequence = "21")
    @Column(scale = 2)
    private BigDecimal invoiceGrossAmount;

    @XmlElement(required = false)
    public BigDecimal getInvoiceGrossAmount() {
        return invoiceGrossAmount;
    }

    @Setter
    @MemberOrder(sequence = "22")
    private String invoiceTax;

    @XmlElement(required = false)
    public String getInvoiceTax() {
        return invoiceTax;
    }

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
            final Party buyer =  partyRepository.findPartyByReference(property.getExternalReference());
            final String atPath = "/FRA";

            final LocalDate startDate = determineStartDateFrom(line.period);
            final LocalDate endDate = startDate.plusYears(1).minusDays(1);

            if(property == null) {
                return line;
            }

            Order order = orderRepository.findOrCreate(
                    orderNumber,
                    line.getSupplierName(),
                    line.entryDate,
                    line.orderDate,
                    supplier,
                    buyer,
                    atPath,
                    line.orderApprovedBy,
                    line.orderApprovedOn);

            final IncomingCharge chargeObj = incomingChargeRepository.findByName(line.charge);
            final Tax taxObj = taxRepository.findByReference(line.tax);

            order.addItem(
                    chargeObj, line.orderDescription,
                    line.netAmount, line.vatAmount, line.grossAmount,
                    taxObj, startDate, endDate, property, project);

            IncomingInvoice invoice = incomingInvoiceRepository.findOrCreate(line.getInvoiceNumber(), atPath, buyer, supplier, line.getOrderDate(), line.getOrderDate());

            return line;
        }

        private LocalDate determineStartDateFrom(final String period) {
            return new LocalDate(Integer.parseInt(period.substring(1)), 7, 1);
        }

        private Party determineSupplier() {
            Party party = partyRepository.matchPartyByReferenceOrName(line.supplierName);
            Country france = countryRepository.findCountry("FRA");
            if (party==null){
                RandomCodeGenerator10Chars generator = new RandomCodeGenerator10Chars();
                party = organisationRepository.newOrganisation(generator.generateRandomCode().toUpperCase(), false, line.supplierName, france);
            }
            return party;
        }

        private final Pattern projectReferencePattern = Pattern.compile("^([^-])+[-].*$");
        private Property inferPropertyFrom(final String projectReference) {
            final Matcher matcher = projectReferencePattern.matcher(projectReference);
            if(!matcher.matches()) {
                return null;
            }
            final String propertyReference = matcher.group(1);
            return propertyRepository.findPropertyByReference(propertyReference);
        }

        private Project lookupProject() {
            return projectRepository.findByReference(line.projectReference);
        }

        private String determineOrderNumber() {
            Integer counter = 1;
            String suffix = "-".concat(String.format("%03d", counter));
            String result = line.getOrderDate().toString().concat(suffix);
            while (orderRepository.findByOrderNumber(result)!=null){
                counter = counter++;
                suffix = "-".concat(String.format("%03d", counter));
                result = line.getOrderDate().toString().concat(suffix);
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
        IncomingChargeRepository incomingChargeRepository;
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

