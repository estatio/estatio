package org.estatio.capex.dom.payment.approval;

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

import org.estatio.capex.dom.payment.PaymentBatch;
import org.estatio.capex.dom.state.StateTransitionAbstract;
import org.estatio.capex.dom.state.StateTransitionRepositoryAbstract;
import org.estatio.capex.dom.task.Task;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "PaymentBatchApprovalStateTransition"
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
                        + "FROM org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransition "
                        + "WHERE paymentBatch == :domainObject "
                        + "ORDER BY completedOn DESC "
        ),
        @Query(
                name = "findByDomainObjectAndCompleted", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransition "
                        + "WHERE paymentBatch == :domainObject "
                        + "&& completed == :completed "
                        + "ORDER BY completedOn DESC "
        ),
        @Query(
                name = "findByTask", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransition "
                        + "WHERE task == :task "
        ),
})
@DomainObject(objectType = "payment.PaymentApprovalStateTransition" )
public class PaymentBatchApprovalStateTransition
        extends StateTransitionAbstract<
        PaymentBatch,
        PaymentBatchApprovalStateTransition,
        PaymentBatchApprovalStateTransitionType,
        PaymentBatchApprovalState> {


    /**
     * Null only for the first transition (which will be complete), thereafter is always populated and
     * corresponds to the current state of the domain object.
     */
    @Column(allowsNull = "true")
    @Getter @Setter
    private PaymentBatchApprovalState fromState;

    @Column(allowsNull = "false")
    @Getter @Setter
    private PaymentBatchApprovalStateTransitionType transitionType;

    /**
     * The most recent non-null value corresponds to the current state of the domain object.
     * If null, then this transition is not yet complete.
     */
    @Column(allowsNull = "true")
    @Getter @Setter
    private PaymentBatchApprovalState toState;

    @Column(allowsNull = "false", name = "paymentId")
    @Getter @Setter
    private PaymentBatch paymentBatch;


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
    public PaymentBatch getDomainObject() {
        return this.getPaymentBatch();
    }

    @Programmatic
    @Override
    public void setDomainObject(final PaymentBatch domainObject) {
        setPaymentBatch(domainObject);
    }

    @DomainService(
            nature = NatureOfService.DOMAIN,
            repositoryFor = PaymentBatchApprovalStateTransition.class
    )
    public static class Repository
            extends StateTransitionRepositoryAbstract<
            PaymentBatch,
            PaymentBatchApprovalStateTransition,
            PaymentBatchApprovalStateTransitionType,
            PaymentBatchApprovalState> {

        public Repository() {
            super(PaymentBatchApprovalStateTransition.class);
        }

    }
}
