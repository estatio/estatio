package org.estatio.capex.dom.invoice.task;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceState;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceStateTransitionType;
import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.task.Task;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo"
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(
        "capex.IncomingInvoiceStateTransition"
)
@Queries({
        @Query(
                name = "findByInvoiceAndTransitionAndTaskCompleted", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.task.IncomingInvoiceStateTransition "
                        + "WHERE invoice == :invoice "
                        + "&& transitionType == :transitionType "
                        + "&& this.task.completed == :completed "
        ),
        @Query(
                name = "findByInvoice", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.task.IncomingInvoiceStateTransition "
                        + "WHERE invoice == :invoice "
                        + "ORDER BY task.createdOn DESC "
        ),
})

@DomainObject(objectType = "capex.IncomingInvoiceStateTransition" )
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
