package org.estatio.module.lease.dom.amortisation;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;

import org.estatio.module.lease.dom.LeaseItem;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "AmortisationScheduleLeaseItemLink"
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
                        "FROM org.estatio.module.lease.dom.amortisation.AmortisationScheduleLeaseItemLink " +
                        "WHERE amortisationSchedule == :amortisationSchedule && "
                        + "leaseItem == :leaseItem"),
        @Query(
                name = "findBySchedule", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.lease.dom.amortisation.AmortisationScheduleLeaseItemLink " +
                        "WHERE amortisationSchedule == :amortisationSchedule "),
        @Query(
                name = "findByLeaseItem", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.lease.dom.amortisation.AmortisationScheduleLeaseItemLink " +
                        "WHERE leaseItem == :leaseItem ")
})
@Uniques({
    @Unique(name = "AmortisationScheduleLeaseItemLink_UNQ", members = { "amortisationSchedule", "leaseItem" })
})
@DomainObject(
        objectType = "amortisation.AmortisationScheduleLeaseItemLink"
)
public class AmortisationScheduleLeaseItemLink {

    public AmortisationScheduleLeaseItemLink(){}

    public AmortisationScheduleLeaseItemLink(final AmortisationSchedule schedule, final LeaseItem leaseItem){
        this.amortisationSchedule = schedule;
        this.leaseItem = leaseItem;
    }

    @Getter @Setter
    @Column(allowsNull = "false", name = "amortisationScheduleId")
    private AmortisationSchedule amortisationSchedule;

    @Getter @Setter
    @Column(allowsNull = "false", name = "leaseItemId")
    private LeaseItem leaseItem;

}
