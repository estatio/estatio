package org.estatio.capex.dom.bankaccount.verification;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.capex.dom.state.StateTransitionAbstract;
import org.estatio.capex.dom.state.StateTransitionRepositoryAbstract;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.financial.bankaccount.BankAccount;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "BankAccountVerificationStateTransition"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id"
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@Queries({
        @Query(
                name = "findByDomainObject", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition "
                        + "WHERE bankAccount == :domainObject "
                        + "ORDER BY completedOn DESC, createdOn DESC " // completedOn should be sufficient, createdOn added for timing issue in integtests
        ),
        @Query(
                name = "findByDomainObjectAndCompleted", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition "
                        + "WHERE bankAccount == :domainObject "
                        + "&& completed == :completed "
                        + "ORDER BY completedOn DESC "
        ),
        @Query(
                name = "findByTask", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition "
                        + "WHERE task == :task "
        ),
})
@DomainObject(objectType = "bankAccount.BankAccountVerificationStateTransition" )
public class BankAccountVerificationStateTransition
        extends StateTransitionAbstract<
        BankAccount,
        BankAccountVerificationStateTransition,
        BankAccountVerificationStateTransitionType,
        BankAccountVerificationState> {

    /**
     * Null only for the first transition (which will be complete), thereafter is always populated and
     * corresponds to the current state of the domain object.
     */
    @Column(allowsNull = "true")
    @Getter @Setter
    private BankAccountVerificationState fromState;

    @Column(allowsNull = "false")
    @Getter @Setter
    private BankAccountVerificationStateTransitionType transitionType;

    /**
     * The most recent non-null value corresponds to the current state of the domain object.
     * If null, then this transition is not yet complete.
     */
    @Column(allowsNull = "true")
    @Getter @Setter
    private BankAccountVerificationState toState;

    @Column(allowsNull = "false", name = "bankAccountId")
    @Getter @Setter
    private BankAccount bankAccount;


    /**
     * Not every transition necessarily has a task.
     */
    @Column(allowsNull = "true", name = "taskId")
    @Getter @Setter
    private Task task;

    @Column(allowsNull = "false")
    @Getter @Setter
    private LocalDateTime createdOn;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDateTime completedOn;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String completedBy;

    @Getter @Setter
    @Column(allowsNull = "false")
    private boolean completed;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String comment;

    @Programmatic
    @Override
    public BankAccount getDomainObject() {
        return getBankAccount();
    }

    @Programmatic
    @Override
    public void setDomainObject(final BankAccount domainObject) {
        setBankAccount(domainObject);
    }

    @DomainService(
            nature = NatureOfService.DOMAIN,
            repositoryFor = BankAccountVerificationStateTransition.class
    )
    public static class Repository
            extends StateTransitionRepositoryAbstract<
            BankAccount,
            BankAccountVerificationStateTransition,
            BankAccountVerificationStateTransitionType,
            BankAccountVerificationState> {

        public Repository() {
            super(BankAccountVerificationStateTransition.class);
        }

    }
}
