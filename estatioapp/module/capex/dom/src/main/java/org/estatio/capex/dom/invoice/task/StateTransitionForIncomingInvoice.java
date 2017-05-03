package org.estatio.capex.dom.invoice.task;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;

import org.apache.isis.applib.annotation.DomainObject;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceState;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceTransitionType;
import org.estatio.capex.dom.task.StateTransition;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo"
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(
        "invoice.StateTransitionForIncomingInvoice"
)
@Queries({
        @Query(
                name = "findByInvoice", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.task.StateTransitionForIncomingInvoice "
                        + "WHERE invoice == :invoice"),
        @Query(
                name = "findByInvoiceAndTransition", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.task.StateTransitionForIncomingInvoice "
                        + "WHERE invoice == :invoice && "
                        + "transition == :transition "),
        @Query(
                name = "findByInvoiceAndAssignedTo", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.task.StateTransitionForIncomingInvoice "
                        + "WHERE invoice == :invoice && "
                        + "role == :role"),
        @Query(
                name = "findByInvoiceAndAssignedToAndCompleted", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.task.StateTransitionForIncomingInvoice "
                        + "WHERE invoice == :invoice && "
                        + "assignedTo == :assignedTo && "
                        + "completed == :completed")

})

@DomainObject(objectType = "invoice.StateTransitionForIncomingInvoice" )
public class StateTransitionForIncomingInvoice
        implements
        StateTransition<StateTransitionForIncomingInvoice, IncomingInvoice, IncomingInvoiceTransitionType, IncomingInvoiceState> {

    @Column(allowsNull = "false")
    @Getter @Setter
    private IncomingInvoiceTransitionType transitionType;

    @Column(allowsNull = "false")
    @Getter @Setter
    private IncomingInvoice domainObject;

}
