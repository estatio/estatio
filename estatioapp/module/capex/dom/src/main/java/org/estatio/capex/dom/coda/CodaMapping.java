package org.estatio.capex.dom.coda;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Property;

import org.estatio.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.dom.charge.Charge;

import lombok.Getter;
import lombok.Setter;

@DomainObject(objectType = "org.estatio.capex.dom.coda.CodaMapping")
@PersistenceCapable(identityType = IdentityType.DATASTORE, schema = "dbo")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByAll", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.coda.CodaMapping "
                        + "WHERE "
                        + "atPath == :atPath && "
                        + "documentType == :documentType && "
                        + "incomingInvoiceType == :incomingInvoiceType && "
                        + "codaTransactionType == :codaTransactionType && "
                        + "charge == :charge && "
                        + "propertyIsFullyOwned == :propertyIsFullyOwned && "
                        + "periodStartDate == :periodStartDate && "
                        + "periodEndDate == :periodEndDate && "
                        + "startDate == :startDate && "
                        + "endDate == :endDate && "
                        + "codaElement == :codaElement"
        ),
})
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "CodaMapping_UNQ", members = { "atPath", "documentType", "incomingInvoiceType", "codaTransactionType", "charge", "propertyIsFullyOwned", "periodStartDate", "periodEndDate", "startDate", "endDate", "codaElement" }),
})
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Setter @Getter
public class CodaMapping {

    @Column(length = 50, allowsNull = "false")
    private String atPath;

    @Column(length = 50, allowsNull = "false")
    private DocumentType documentType;

    @Column(length = 50, allowsNull = "false")
    private IncomingInvoiceType incomingInvoiceType;

    @Column(length = 50, allowsNull = "false")
    private CodaTransactionType codaTransactionType;

    @Column(allowsNull = "false", name = "chargeId")
    private Charge charge;

    private boolean propertyIsFullyOwned;

    @Column(allowsNull = "true")
    @Property(optionality = Optionality.OPTIONAL)
    private LocalDate periodStartDate;

    @Column(allowsNull = "true")
    @Property(optionality = Optionality.OPTIONAL)
    private LocalDate periodEndDate;

    @Column(allowsNull = "false", name = "codaElementId")
    private CodaElement codaElement;

    @Column(allowsNull = "true")
    @Property(optionality = Optionality.OPTIONAL)
    private LocalDate startDate;

    @Column(allowsNull = "true")
    @Property(optionality = Optionality.OPTIONAL)
    private LocalDate endDate;

}
