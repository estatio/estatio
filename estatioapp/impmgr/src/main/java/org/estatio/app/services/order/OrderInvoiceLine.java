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

import org.estatio.capex.dom.charge.IncomingCharge;
import org.estatio.capex.dom.charge.IncomingChargeRepository;
import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.capex.dom.time.TimeInterval;
import org.estatio.capex.dom.time.TimeIntervalRepository;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.party.OrganisationRepository;
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
                "status",
                "orderNumber",
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


    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "1")
    private String status;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "2")
    private String orderNumber;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "3")
    private String charge;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "4")
    private LocalDate entryDate;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "5")
    private LocalDate orderDate;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "6")
    private String seller;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "7")
    private String orderDescription;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "8")
    @Column(scale = 2)
    private BigDecimal netAmount;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "9")
    @Column(scale = 2)
    private BigDecimal vatAmount;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "10")
    @Column(scale = 2)
    private BigDecimal grossAmount;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "11")
    private String orderApprovedBy;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "12")
    private LocalDate orderApprovedOn;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "13")
    private String projectReference;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "14")
    private String period;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "15")
    private String tax;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "16")
    private String invoiceNumber;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "17")
    private String invoiceDescription;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "18")
    @Column(scale = 2)
    private BigDecimal invoiceNetAmount;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "19")
    @Column(scale = 2)
    private BigDecimal invoiceVatAmount;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "20")
    @Column(scale = 2)
    private BigDecimal invoiceGrossAmount;
    @XmlElement(required = false)
    @Getter @Setter
    @MemberOrder(sequence = "21")
    private String invoiceTax;

    
    @Mixin(method="act")
    public static class _apply {
        private final OrderInvoiceLine line;
        public _apply(final OrderInvoiceLine line) {
            this.line = line;
        }
        @Action()
        @ActionLayout(contributed= Contributed.AS_ACTION)
        public OrderInvoiceLine act() {
            final String reference = determineReference();
            final Project project = lookupProject(line.projectReference);
            final Property property = inferPropertyFrom(line.projectReference);
            final String atPath = "/FRA";
            final TimeInterval timeInterval = timeIntervalRepository.findByName(line.period);

            if(property == null) {
                return line;
            }

            Order order = orderRepository.findOrCreate(
                    reference, line.orderNumber,
                    line.entryDate, line.orderDate,
                    timeInterval, line.seller,
                    project, property,
                    atPath,
                    line.orderApprovedBy, line.orderApprovedOn);

            final IncomingCharge chargeObj = incomingChargeRepository.findByName(line.charge);
            final Tax taxObj = taxRepository.findByReference(line.tax);

            order.addItem(
                    chargeObj, line.orderDescription,
                    line.netAmount, line.vatAmount, line.grossAmount,
                    taxObj, timeInterval.getCalendarType());

            return line;
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

        private Project lookupProject(final String projectReference) {
            return projectRepository.findByReference(projectReference);
        }

        private String determineReference() {
            return "TODO";
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
        TimeIntervalRepository timeIntervalRepository;

    }
    
    
}

