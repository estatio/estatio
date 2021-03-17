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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// NOTE: this view also is maintained by fly db
@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.NONDURABLE,
        table = "FastNetRentRollOnLeaseDataLine",
        schema = "fastnet",
        extensions = {
                @Extension(vendorName = "datanucleus", key = "view-definition",
                        value = "CREATE VIEW \"fastnet\".\"FastNetRentRollOnLeaseDataLine\" " +
                                "( " +
                                "  {this.keyToLeaseExternalReference}, " +
                                "  {this.exportDate}, " +

                                "  {this.kontraktNr}, " +
                                "  {this.hyresgast}, " +
                                "  {this.kundNr}, " +
                                "  {this.arshyra}, " +
                                "  {this.kontraktFrom}, " +
                                "  {this.kontraktTom}, " +
                                "  {this.futureRentRollLine}, " +
                                "  {this.applied}, " +
                                "  {this.importStatus}, " +

                                "  {this.leaseReference}, " +
                                "  {this.externalReference}, " +
                                "  {this.leaseStartDate}, " +
                                "  {this.leaseEndDate} " +
                                ") AS " +
                                "WITH leaseData AS ( " +
                                "SELECT " +
                                "l.\"externalReference\"" +
                                ", a.\"reference\" as \"leaseReference\"" +
                                ", a.\"startDate\" as \"leaseStartDate\"" +
                                ", a.\"endDate\" as \"leaseEndDate\"" +
                                " FROM \"dbo\".\"Lease\" l " +
                                " INNER JOIN \"dbo\".\"Agreement\" a " +
                                " ON a.\"id\" = l.\"id\" " +
                                "WHERE l.\"atPath\" LIKE \'/SWE%\' " +
                                " ) " +
                                "SELECT " +
                                " rrl.\"keyToLeaseExternalReference\"" +
                                ", rrl.\"exportDate\" " +
                                ", rrl.\"kontraktNr\"" +
                                ", rrl.\"hyresgast\"" +
                                ", rrl.\"kundNr\"" +
                                ", rrl.\"arshyra\"" +
                                ", rrl.\"kontraktFrom\"" +
                                ", rrl.\"kontraktTom\"" +
                                ", rrl.\"futureRentRollLine\" " +
                                ", rrl.\"applied\" " +
                                ", rrl.\"importStatus\" " +
                                ", ld.\"leaseReference\" " +
                                ", ld.\"externalReference\" " +
                                ", ld.\"leaseStartDate\" " +
                                ", ld.\"leaseEndDate\" " +
                                "FROM \"fastnet\".\"RentRollLine\" rrl " +
                                "  LEFT OUTER JOIN leaseData ld " +
                                "    ON ld.\"externalReference\" = rrl.\"keyToLeaseExternalReference\" ")
        })
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByExportDate", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.fastnet.dom.FastNetRentRollOnLeaseDataLine " +
                        "WHERE exportDate == :exportDate "),
        @javax.jdo.annotations.Query(
                name = "findByExternalReferenceAndExportDate", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.fastnet.dom.FastNetRentRollOnLeaseDataLine " +
                        "WHERE keyToLeaseExternalReference == :externalReference " +
                        "&& exportDate == :exportDate "),
        @javax.jdo.annotations.Query(
                name = "nonMatchingRentRollLinesForExportDate", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.fastnet.dom.FastNetRentRollOnLeaseDataLine " +
                        "WHERE exportDate == :exportDate "
                        + "&& kontraktNr != null "
                        + "&& leaseReference == null "),
        @javax.jdo.annotations.Query(
                name = "matchingRentRollLinesForExportDate", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.fastnet.dom.FastNetRentRollOnLeaseDataLine " +
                        "WHERE exportDate == :exportDate "
                        + "&& leaseReference != null "),

})
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@XmlRootElement(name = "FastNetRentRollOnLeaseDataLine")
@XmlType(
        propOrder = {
                "keyToLeaseExternalReference",
                "exportDate",

                "kontraktNr",
                "hyresgast",
                "kundNr",
                "arshyra",
                "kontraktFrom",
                "kontraktTom",
                "futureRentRollLine",
                "applied",
                "importStatus",

                "leaseReference",
                "externalReference",
                "leaseStartDate",
                "leaseEndDate"

        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@DomainObject(objectType = "org.estatio.module.fastnet.dom.FastNetRentRollOnLeaseDataLine")
@AllArgsConstructor
@Getter @Setter
public class FastNetRentRollOnLeaseDataLine {

    @MemberOrder(sequence = "1")
    private ImportStatus importStatus;

    @MemberOrder(sequence = "2")
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate applied;

    @MemberOrder(sequence = "3")
    private String keyToLeaseExternalReference;

    @MemberOrder(sequence = "4")
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate exportDate;

    @MemberOrder(sequence = "5")
    private String leaseReference;

    @MemberOrder(sequence = "6")
    private String externalReference;

    @MemberOrder(sequence = "7")
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate leaseStartDate;

    @MemberOrder(sequence = "8")
    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate leaseEndDate;

    @MemberOrder(sequence = "9")
    private String kontraktNr;

    @MemberOrder(sequence = "10")
    private String kundNr;

    @MemberOrder(sequence = "11")
    private BigDecimal arshyra;

    @MemberOrder(sequence = "12")
    private String hyresgast;

    @MemberOrder(sequence = "13")
    private String kontraktFrom;

    @MemberOrder(sequence = "14")
    private String kontraktTom;

    @MemberOrder(sequence = "15")
    private boolean futureRentRollLine;



}
