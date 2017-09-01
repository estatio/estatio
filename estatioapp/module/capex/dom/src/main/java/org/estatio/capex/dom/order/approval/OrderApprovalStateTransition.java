package org.estatio.capex.dom.order.approval;

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

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.state.StateTransitionAbstract;
import org.estatio.capex.dom.state.StateTransitionRepositoryAbstract;
import org.estatio.capex.dom.task.Task;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "OrderApprovalStateTransition"
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
                        + "FROM org.estatio.capex.dom.order.approval.OrderApprovalStateTransition "
                        + "WHERE ordr == :domainObject "
                        + "ORDER BY completedOn DESC "
        ),
        @Query(
                name = "findByDomainObjectAndCompleted", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.approval.OrderApprovalStateTransition "
                        + "WHERE ordr == :domainObject "
                        + "&& completed == :completed "
                        + "ORDER BY completedOn DESC "
        ),
        @Query(
                name = "findByTask", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.order.approval.OrderApprovalStateTransition "
                        + "WHERE task == :task "
        ),
})
@DomainObject(objectType = "order.OrderApprovalStateTransition" )
public class OrderApprovalStateTransition
        extends StateTransitionAbstract<
        Order,
        OrderApprovalStateTransition,
        OrderApprovalStateTransitionType,
        OrderApprovalState> {


    /**
     * Null only for the first transition (which will be complete), thereafter is always populated and
     * corresponds to the current state of the domain object.
     */
    @Column(allowsNull = "true")
    @Getter @Setter
    private OrderApprovalState fromState;

    @Column(allowsNull = "false")
    @Getter @Setter
    private OrderApprovalStateTransitionType transitionType;

    /**
     * The most recent non-null value corresponds to the current state of the domain object.
     * If null, then this transition is not yet complete.
     */
    @Column(allowsNull = "true")
    @Getter @Setter
    private OrderApprovalState toState;

    @Column(allowsNull = "false", name = "orderId")
    @Getter @Setter
    private Order ordr;


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
    public Order getDomainObject() {
        return getOrdr();
    }

    @Programmatic
    @Override
    public void setDomainObject(final Order domainObject) {
        setOrdr(domainObject);
    }

    @DomainService(
            nature = NatureOfService.DOMAIN,
            repositoryFor = OrderApprovalStateTransition.class
    )
    public static class Repository
            extends StateTransitionRepositoryAbstract<
                    Order,
            OrderApprovalStateTransition,
            OrderApprovalStateTransitionType,
            OrderApprovalState> {

        public Repository() {
            super(OrderApprovalStateTransition.class);
        }

    }
}
