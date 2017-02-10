package org.estatio.budgetassignment.dom.calculationresult;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.dom.lease.LeaseTermForServiceCharge;

import lombok.Getter;
import lombok.Setter;
@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo" // Isis' ObjectSpecId inferred from @DomainObject#objectType
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
                        "FROM BudgetCalculationResultLink " +
                        "WHERE budgetCalculationResult == :budgetCalculationResult && "
                        + "leaseTermForServiceCharge == :leaseTermForServiceCharge"),
        @Query(
                name = "findByCalculationResult", language = "JDOQL",
                value = "SELECT " +
                        "FROM BudgetCalculationResultLink " +
                        "WHERE budgetCalculationResult == :budgetCalculationResult"),
        @Query(
                name = "findByLeaseTerm", language = "JDOQL",
                value = "SELECT " +
                        "FROM BudgetCalculationResultLink " +
                        "WHERE leaseTermForServiceCharge == :leaseTermForServiceCharge")
})
@Unique(name = "BudgetCalculationResultLink_result_leaseTerm_UNQ", members = { "budgetCalculationResult", "leaseTermForServiceCharge" })

@DomainObject(
        objectType = "BudgetCalculationResultLink"
)
public class BudgetCalculationResultLink {

    @Getter @Setter
    @Column(name="budgetCalculationResultId", allowsNull = "false")
    private BudgetCalculationResult budgetCalculationResult;

    @Getter @Setter
    @Column(name="leaseTermId", allowsNull = "false")
    private LeaseTermForServiceCharge leaseTermForServiceCharge;

    public void remove() {
        repositoryService.removeAndFlush(this);
    }

    @Inject
    private RepositoryService repositoryService;

}
