package org.estatio.module.budgetassignment.dom.calculationresult;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;

import org.estatio.module.lease.dom.LeaseTermForServiceCharge;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "BudgetCalculationResultLeaseTermLink"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findUnique", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultLeaseTermLink "
                        + "WHERE budgetCalculationResult == :budgetCalculationResult "
                        + "   && leaseTerm  == :leaseTerm "),
        @Query(
                name = "findByBudgetCalculationResult", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultLeaseTermLink "
                        + "WHERE budgetCalculationResult == :budgetCalculationResult "),
        @Query(
                name = "findByLeaseTerm", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.budgetassignment.dom.calculationresult.BudgetCalculationResultLeaseTermLink "
                        + "WHERE leaseTerm == :leaseTerm "),

})
@Unique(name = "BudgetCalculationResultLeaseTermLink_budgetCalculationResult_leaseTerm_UNQ", members = { "budgetCalculationResult", "leaseTerm" })
@DomainObject(
        objectType = "budgetassignment.BudgetCalculationResultLeaseTermLink",
        editing = Editing.DISABLED
)
public class BudgetCalculationResultLeaseTermLink {

    public BudgetCalculationResultLeaseTermLink(final BudgetCalculationResult budgetCalculationResult, final LeaseTermForServiceCharge leaseTerm){
        this.budgetCalculationResult = budgetCalculationResult;
        this.leaseTerm = leaseTerm;
    }

    @Column(allowsNull = "false", name = "budgetCalculationResultId")
    @Getter @Setter
    private BudgetCalculationResult budgetCalculationResult;

    @Column(allowsNull = "false", name = "leaseTermId")
    @Getter @Setter
    private LeaseTermForServiceCharge leaseTerm;
}
