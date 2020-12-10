package org.estatio.module.lease.dom.etlutilobjects;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;

import org.estatio.module.lease.dom.Lease;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "ecpdw",
        table = "LeaseEvaluationDateEtlObject"
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
                        "FROM org.estatio.module.lease.dom.etlutilobjects.LeaseEvaluationDateEtlObject " +
                        "WHERE lease == :lease && "
                        + "leaseEvaluationDate == :leaseEvaluationDate && "
                        + "startDate == :startDate "),
        @Query(
                name = "findByLease", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.lease.dom.etlutilobjects.LeaseEvaluationDateEtlObject " +
                        "WHERE lease == :lease "),
})
@Uniques({
    @Unique(name = "LeaseEvaluationDateEtlObject_UNQ", members = { "lease", "leaseEvaluationDate", "startDate" })
})
@DomainObject(
        objectType = "org.estatio.module.lease.dom.etlutilobjects.LeaseEvaluationDateEtlObject"
)
public class LeaseEvaluationDateEtlObject {

    public LeaseEvaluationDateEtlObject(){}

    public LeaseEvaluationDateEtlObject(final Lease lease, final LocalDate leaseEvaluationDate, final LocalDate startDate){
        this.lease = lease;
        this.leaseEvaluationDate = leaseEvaluationDate;
        this.startDate = startDate;
    }

    @Getter @Setter
    @Column(allowsNull = "false", name = "leaseId")
    private Lease lease;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate leaseEvaluationDate;

    @Getter @Setter
    @Column(allowsNull = "false")
    private LocalDate startDate;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate endDate;

    @Getter @Setter
    @Column(allowsNull = "true", name = "nextEtlObjectId")
    private LeaseEvaluationDateEtlObject next;

    @Programmatic
    public LeaseEvaluationDateEtlObject createNext(final LocalDate leaseEvaluationDate){
        final LeaseEvaluationDateEtlObject newEtlObject = leaseEvaluationDateEtlObjectRepository
                .create(getLease(), leaseEvaluationDate, clockService.now());
        setNext(newEtlObject);
        setEndDate(newEtlObject.getStartDate().minusDays(1));
        return newEtlObject;
    }

    @Inject
    LeaseEvaluationDateEtlObjectRepository leaseEvaluationDateEtlObjectRepository;

    @Inject
    ClockService clockService;

}
