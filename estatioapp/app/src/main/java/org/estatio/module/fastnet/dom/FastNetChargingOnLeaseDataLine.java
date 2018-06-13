package org.estatio.module.fastnet.dom;

import java.math.BigDecimal;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.schema.utils.jaxbadapters.JodaLocalDateStringAdapter;

import lombok.Getter;
import lombok.Setter;

// NOTE: this view also is maintained by fly db

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.NONDURABLE,
        table = "FastNetChargingOnLeaseDataLine",
        schema = "fastnet",
        extensions = {
                @Extension(vendorName = "datanucleus", key = "view-definition",
                        value = "CREATE VIEW \"fastnet\".\"FastNetChargingOnLeaseDataLine\" " +
                                "( " +
                                "  {this.keyToLeaseExternalReference}, " +
                                "  {this.keyToChargeReference}, " +
                                "  {this.exportDate}, " +

                                "  {this.kontraktNr}, " +
                                "  {this.kundNr}, " +
                                "  {this.kod}, " +
                                "  {this.kod2}, " +
                                "  {this.kontText}, " +
                                "  {this.kontText2}, " +
                                "  {this.fromDat}, " +
                                "  {this.tomDat}, " +
                                "  {this.debPer}, " +
                                "  {this.firstPosStart}, " +
                                "  {this.arsBel}, " +
                                "  {this.applied}, " +
                                "  {this.importStatus}, " +

                                "  {this.leaseReference}, " +
                                "  {this.externalReference}, " +
                                "  {this.tenantName}, " +
                                "  {this.tenantReference}, " +
                                "  {this.leaseStatus}, " +
                                "  {this.tenancyStartDate}, " +
                                "  {this.tenancyEndDate}, " +
                                "  {this.leaseStartDate}, " +
                                "  {this.leaseEndDate}, " +

                                "  {this.leaseItemType}, " +
                                "  {this.invoicingFrequency}, " +
                                "  {this.leaseItemStartDate}, " +
                                "  {this.leaseItemEndDate}, " +
                                "  {this.chargeReference}, " +
                                "  {this.chargeGroupReference}, " +

                                "  {this.leaseTermStartDate}, " +
                                "  {this.leaseTermEndDate}, " +
                                "  {this.leaseTermStatus}, " +
                                "  {this.baseValue}, " +
                                "  {this.settledValue}, " +
                                "  {this.value}, " +
                                "  {this.budgetedValue} " +
                                ") AS " +

                                "WITH leaseData AS ( " +
                                "SELECT " +

                                "l. \"externalReference\"" +
                                ", ch.\"reference\" as \"chargeReference\"" +

                                ", li.\"type\" as \"leaseItemType\"" +
                                ", li.\"startDate\" as \"leaseItemStartDate\"" +
                                ", li.\"endDate\" as \"leaseItemEndDate\"" +
                                ", li.\"invoicingFrequency\"" +
                                ", li.\"status\" as \"leaseItemStatus\"" +

                                ", a.\"reference\" as \"leaseReference\"" +
                                ", l.\"status\" as \"leaseStatus\"" +
                                ", a.\"startDate\" as \"leaseStartDate\"" +
                                ", a.\"endDate\" as \"leaseEndDate\"" +
                                ", l.\"tenancyStartDate\"" +
                                ", l.\"tenancyEndDate\"" +
                                ", p.\"name\" as \"tenantName\"" +
                                ", p.\"reference\" as \"tenantReference\"" +

                                ", lt.\"startDate\" as \"leaseTermStartDate\"" +
                                ", lt.\"endDate\" as \"leaseTermEndDate\"" +
                                ", lt.\"status\" as \"leaseTermStatus\"" +
                                ", lt.\"baseValue\"" +
                                ", lt.\"settledValue\"" +
                                ", lt.\"value\"" +
                                ", lt.\"budgetedValue\"" +

                                "FROM \"dbo\".\"LeaseItem\" li " +
                                "INNER JOIN \"dbo\".\"Lease\" l ON l.\"id\" = li.\"leaseId\" " +
                                "INNER JOIN \"dbo\".\"Agreement\" a ON a.\"id\" = l.\"id\" " +
                                "INNER JOIN \"dbo\".\"Charge\" ch ON ch.\"id\" = li.\"chargeId\" " +
                                "INNER JOIN \"dbo\".\"LeaseTerm\" lt ON lt.\"leaseItemId\" = li.\"id\" " +
                                "LEFT OUTER JOIN (SELECT * FROM \"dbo\".\"AgreementRole\" WHERE \"typeId\" = 6 AND \"endDate\" is null) AS agr ON agr.\"agreementId\" = a.\"id\" " +
                                "LEFT OUTER JOIN \"dbo\".\"Party\" p ON p.\"id\" = agr.\"partyId\" " +
                                "WHERE li.\"atPath\" LIKE \'/SWE%\' " +
                                "AND lt.\"nextLeaseTermId\" is null " +
                                " ) " +

                                "SELECT " +
                                // keys
                                "  cl.\"keyToLeaseExternalReference\"" +
                                ", cl.\"keyToChargeReference\"" +
                                ", cl.\"exportDate\"" +
                                // charging
                                ", cl.\"kontraktNr\"" +
                                ", cl.\"kundNr\"" +
                                ", cl.\"kod\"" +
                                ", cl.\"kod2\"" +
                                ", cl.\"kontText\"" +
                                ", cl.\"kontText2\"" +
                                ", cl.\"fromDat\"" +
                                ", cl.\"tomDat\"" +
                                ", cl.\"debPer\"" +
                                ", cl.\"firstPosStart\"" +
                                ", cl.\"arsBel\"" +
                                ", cl.\"applied\"" +
                                ", cl.\"importStatus\"" +
                                // lease
                                ", ISNUll(ld.\"leaseReference\", a2.\"reference\") as \"leaseReference\"" +
                                ", ISNUll(ld.\"externalReference\", l2.\"externalReference\") as \"externalReference\"" +
                                ", ISNUll(ld.\"tenantName\", p2.\"name\") as \"tenantName\"" +
                                ", ISNUll(ld.\"tenantReference\", p2.\"reference\") as \"tenantReference\"" +
                                ", ISNUll(ld.\"leaseStatus\", l2.\"status\") as \"leaseStatus\"" +
                                ", ISNUll(ld.\"tenancyStartDate\", l2.\"tenancyStartDate\") as \"tenancyStartDate\"" +
                                ", ISNUll(ld.\"tenancyEndDate\", l2.\"tenancyEndDate\") as \"tenancyEndDate\"" +
                                ", ISNUll(ld.\"leaseStartDate\", a2.\"startDate\") as \"leaseStartDate\"" +
                                ", ISNUll(ld.\"leaseEndDate\", a2.\"endDate\") as \"leaseEndDate\"" +
                                // lease item
                                ", ld.\"leaseItemType\"" +
                                ", ld.\"invoicingFrequency\"" +
                                ", ld.\"leaseItemStartDate\"" +
                                ", ld.\"leaseItemEndDate\"" +
                                ", ch.\"reference\" as chargeReference" +
                                ", chg.\"reference\" as chargeGroupReference" +
                                // lease term
                                ", ld.\"leaseTermStartDate\"" +
                                ", ld.\"leaseTermEndDate\"" +
                                ", ld.\"leaseTermStatus\"" +
                                ", ld.\"baseValue\"" +
                                ", ld.\"settledValue\"" +
                                ", ld.\"value\"" +
                                ", ld.\"budgetedValue\"" +
                                "FROM \"fastnet\".\"ChargingLine\" cl " +
                                "  LEFT OUTER JOIN leaseData ld " +
                                "  ON ld.\"externalReference\" = cl.\"keyToLeaseExternalReference\" " +
                                "  AND ld.\"chargeReference\" = cl.\"keyToChargeReference\" " +
                                "  LEFT OUTER JOIN \"dbo\".\"Lease\" l2 " +
                                "  ON l2.\"externalReference\" = cl.\"keyToLeaseExternalReference\" " +
                                "  LEFT OUTER JOIN \"dbo\".\"Agreement\" a2 " +
                                "  ON l2.\"id\" = a2.\"id\"" +
                                "  LEFT OUTER JOIN (SELECT * FROM \"dbo\".\"AgreementRole\" WHERE \"typeId\" = 6 AND \"endDate\" is null) AS agr2 " +
                                "  ON agr2.\"agreementId\" = a2.\"id\" " +
                                "  LEFT OUTER JOIN \"dbo\".\"Party\" p2 " +
                                "  ON p2.\"id\" = agr2.\"partyId\" " +
                                "  LEFT OUTER JOIN \"dbo\".\"Charge\" ch " +
                                "  ON ch.\"reference\" = cl.\"keyToChargeReference\" " +
                                "  LEFT OUTER JOIN \"dbo\".\"ChargeGroup\" chg " +
                                "  ON ch.\"groupId\" = chg.\"id\" "
                )
        })
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByExportDate", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.fastnet.dom.FastNetChargingOnLeaseDataLine " +
                        "WHERE exportDate == :exportDate " +
                        "ORDER BY keyToLeaseExternalReference, keyToChargeReference, leaseTermStartDate DESC"),
        @javax.jdo.annotations.Query(
                name = "findNonDiscardedAndNonAppliedByExportDate", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.fastnet.dom.FastNetChargingOnLeaseDataLine " +
                        "WHERE exportDate == :exportDate " +
                        "&& applied == null " +
                        "&& ( importStatus == null || importStatus != 'DISCARDED' ) " +
                        "ORDER BY keyToLeaseExternalReference, keyToChargeReference, leaseTermStartDate DESC"),

})
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@XmlRootElement(name = "FastNetChargingOnLeaseDataLine")
@XmlType(
        propOrder = {
                "keyToLeaseExternalReference",
                "keyToChargeReference",
                "exportDate",

                "kontraktNr",
                "kundNr",
                "kod",
                "kod2",
                "kontText",
                "kontText2",
                "fromDat",
                "tomDat",
                "debPer",
                "firstPosStart",
                "arsBel",
                "applied",
                "importStatus",

                "leaseReference",
                "externalReference",
                "tenantName",
                "tenantReference",
                "leaseStatus",
                "tenancyStartDate",
                "tenancyEndDate",
                "leaseStartDate",
                "leaseEndDate",

                "leaseItemType",
                "invoicingFrequency",
                "leaseItemStartDate",
                "leaseItemEndDate",
                "chargeReference",
                "chargeGroupReference",

                "leaseTermStartDate",
                "leaseTermEndDate",
                "leaseTermStatus",
                "baseValue",
                "settledValue",
                "value",
                "budgetedValue"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@DomainObject(objectType = "org.estatio.module.fastnet.dom.FastNetChargingOnLeaseDataLine")
@Getter @Setter
public class FastNetChargingOnLeaseDataLine {

    @MemberOrder(sequence = "1")
    private ImportStatus importStatus;

    @MemberOrder(sequence = "2")
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate applied;

    @MemberOrder(sequence = "3")
    private String keyToLeaseExternalReference;

    @MemberOrder(sequence = "4")
    private String keyToChargeReference;

    @MemberOrder(sequence = "5")
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate exportDate;

    @MemberOrder(sequence = "6")
    private String leaseReference;

    @MemberOrder(sequence = "7")
    private String externalReference;

    @MemberOrder(sequence = "8")
    private String leaseStatus;

    @MemberOrder(sequence = "9")
    private String tenantName;

    @MemberOrder(sequence = "10")
    private String tenantReference;

    @MemberOrder(sequence = "11")
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate tenancyStartDate;

    @MemberOrder(sequence = "12")
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate tenancyEndDate;

    @MemberOrder(sequence = "13")
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate leaseStartDate;

    @MemberOrder(sequence = "14")
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate leaseEndDate;

    @MemberOrder(sequence = "15")
    private String leaseItemType;

    @MemberOrder(sequence = "16")
    private String invoicingFrequency;

    @MemberOrder(sequence = "17")
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate leaseItemStartDate;

    @MemberOrder(sequence = "18")
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate leaseItemEndDate;

    @MemberOrder(sequence = "19")
    private String chargeReference;

    @MemberOrder(sequence = "20")
    private String chargeGroupReference;

    @MemberOrder(sequence = "21")
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate leaseTermStartDate;

    @MemberOrder(sequence = "22")
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate leaseTermEndDate;

    @MemberOrder(sequence = "23")
    private String leaseTermStatus;

    @MemberOrder(sequence = "24")
    private BigDecimal value;

    @MemberOrder(sequence = "25")
    private BigDecimal baseValue;

    @MemberOrder(sequence = "26")
    private BigDecimal settledValue;

    @MemberOrder(sequence = "27")
    private BigDecimal budgetedValue;

    @MemberOrder(sequence = "28")
    private String kontraktNr;

    @MemberOrder(sequence = "29")
    private String kundNr;

    @MemberOrder(sequence = "30")
    private String kod;

    @MemberOrder(sequence = "31")
    private String kontText;

    @MemberOrder(sequence = "32")
    private String kod2;

    @MemberOrder(sequence = "33")
    private String kontText2;

    @MemberOrder(sequence = "34")
    private String fromDat;

    @MemberOrder(sequence = "35")
    private String tomDat;

    @MemberOrder(sequence = "36")
    private BigDecimal arsBel;

    @MemberOrder(sequence = "37")
    private String debPer;

    @MemberOrder(sequence = "38")
    private String firstPosStart;

}
