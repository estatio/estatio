package org.estatio.module.lease.dom.amortisation;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;

import org.estatio.module.lease.dom.amendments.LeaseAmendmentItemForDiscount;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "AmortisationScheduleAmendmentItemLink"
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
                        "FROM org.estatio.module.lease.dom.amortisation.AmortisationScheduleAmendmentItemLink " +
                        "WHERE amortisationSchedule == :amortisationSchedule && "
                        + "leaseAmendmentItemForDiscount == :leaseAmendmentItemForDiscount")
})
@Uniques({
    @Unique(name = "AmortisationScheduleAmendmentItemLink_UNQ", members = { "amortisationSchedule", "leaseAmendmentItemForDiscount" })
})
@DomainObject(
        objectType = "amortisation.AmortisationScheduleAmendmentItemLink"
)
public class AmortisationScheduleAmendmentItemLink {

    @Getter @Setter
    @Column(allowsNull = "false", name = "amortisationScheduleId")
    private AmortisationSchedule amortisationSchedule;

    @Getter @Setter
    @Column(allowsNull = "false", name = "amendmentItemId")
    private LeaseAmendmentItemForDiscount leaseAmendmentItemForDiscount;


}
