package org.estatio.capex.dom.invoice.state;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.state.StateTransition;
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
                name = "findByInvoiceAndTransitionTypeAndTaskCompleted", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.state.IncomingInvoiceStateTransition "
                        + "WHERE invoice == :invoice "
                        + "&& transitionType == :transitionType "
                        + "&& task.completed == :taskCompleted "
                        + "ORDER BY task.createdOn DESC "
        ),
        @Query(
                name = "findByInvoice", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.state.IncomingInvoiceStateTransition "
                        + "WHERE invoice == :invoice "
                        + "ORDER BY task.createdOn DESC "
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
        implements
        StateTransition<IncomingInvoice, IncomingInvoiceStateTransition, IncomingInvoiceStateTransitionType, IncomingInvoiceState> {

    public IncomingInvoiceStateTransition(
            final IncomingInvoiceStateTransitionType transitionType,
            final IncomingInvoice invoice,
            final Task task) {
        this.transitionType = transitionType;
        this.invoice = invoice;
        this.task = task;
    }

    @Column(allowsNull = "false")
    @Getter @Setter
    private IncomingInvoiceStateTransitionType transitionType;

    @Column(allowsNull = "false", name = "invoiceId")
    @Getter @Setter
    private IncomingInvoice invoice;

    @Column(allowsNull = "false", name = "taskId")
    @Getter @Setter
    private Task task;

    @Programmatic
    @Override
    public IncomingInvoice getDomainObject() {
        return getInvoice();
    }
}
