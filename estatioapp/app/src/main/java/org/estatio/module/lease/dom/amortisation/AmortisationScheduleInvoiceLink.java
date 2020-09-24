package org.estatio.module.lease.dom.amortisation;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;

import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "AmortisationScheduleInvoiceLink"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.lease.dom.amortisation.AmortisationScheduleInvoiceLink " +
                        "WHERE amortisationSchedule == :amortisationSchedule && "
                        + "invoice == :invoice"),
        @Query(
                name = "findBySchedule", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.lease.dom.amortisation.AmortisationScheduleInvoiceLink " +
                        "WHERE amortisationSchedule == :amortisationSchedule "),
        @Query(
                name = "findByInvoice", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.lease.dom.amortisation.AmortisationScheduleInvoiceLink " +
                        "WHERE invoice == :invoice ")
})
@Uniques({
    @Unique(name = "AmortisationScheduleInvoiceLink_UNQ", members = { "amortisationSchedule", "invoice" })
})
@DomainObject(
        objectType = "amortisation.AmortisationScheduleInvoiceLink"
)
public class AmortisationScheduleInvoiceLink {

    public AmortisationScheduleInvoiceLink(){}

    public AmortisationScheduleInvoiceLink(final AmortisationSchedule schedule, final InvoiceForLease invoice){
        this.amortisationSchedule = schedule;
        this.invoice = invoice;
    }

    @Getter @Setter
    @Column(allowsNull = "false", name = "amortisationScheduleId")
    private AmortisationSchedule amortisationSchedule;

    @Getter @Setter
    @Column(allowsNull = "false", name = "invoiceId")
    private InvoiceForLease invoice;

}
