package org.estatio.capex.dom.invoice.state;

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
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.state.StateTransitionAbstract;
import org.estatio.capex.dom.task.Task;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "incomingInvoice",
        table = "IncomingInvoiceStateTransition"
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
                        + "FROM org.estatio.capex.dom.invoice.state.IncomingInvoiceStateTransition "
                        + "WHERE invoice == :domainObject "
                        + "ORDER BY createdOn DESC "
        ),
        @Query(
                name = "findByDomainObjectAndIncomplete", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.state.IncomingInvoiceStateTransition "
                        + "WHERE invoice == :domainObject "
                        + "&& toState == null "
                        + "ORDER BY createdOn DESC "
        ),
        @Query(
                name = "findByTask", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.state.IncomingInvoiceStateTransition "
                        + "WHERE task == :task "
        ),
})
@DomainObject(objectType = "incomingInvoice.IncomingInvoiceStateTransition" )
public class IncomingInvoiceStateTransition
        extends StateTransitionAbstract<
                    IncomingInvoice,
                    IncomingInvoiceStateTransition,
                    IncomingInvoiceStateTransitionType,
                    IncomingInvoiceState> {

    /**
     * For the first transition, represents the initial state of the domain object
     * Thereafter, will hold the same value as the "to state" of the preceding transition.
     */
    @Column(allowsNull = "false")
    @Getter @Setter
    private IncomingInvoiceState fromState;

    @Column(allowsNull = "false")
    @Getter @Setter
    private IncomingInvoiceStateTransitionType transitionType;

    /**
     * If null, then this transition is not yet complete.
     */
    @Column(allowsNull = "true")
    @Getter @Setter
    private IncomingInvoiceState toState;

    @Column(allowsNull = "false", name = "invoiceId")
    @Getter @Setter
    private IncomingInvoice invoice;


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


    @Programmatic
    @Override
    public IncomingInvoice getDomainObject() {
        return getInvoice();
    }

    @Programmatic
    @Override
    public void setDomainObject(final IncomingInvoice domainObject) {
        setInvoice(domainObject);
    }

}
