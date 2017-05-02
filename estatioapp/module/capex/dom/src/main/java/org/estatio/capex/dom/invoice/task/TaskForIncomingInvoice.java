package org.estatio.capex.dom.invoice.task;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;

import org.apache.isis.applib.annotation.DomainObject;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.rule.IncomingInvoiceTransition;
import org.estatio.capex.dom.task.Task;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo"
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(
        "invoice.TaskForIncomingInvoice"
)
@Queries({
        @Query(
                name = "findByInvoice", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.task.TaskForIncomingInvoice "
                        + "WHERE invoice == :invoice"),
        @Query(
                name = "findByInvoiceAndAssignedTo", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.task.TaskForIncomingInvoice "
                        + "WHERE invoice == :invoice && "
                        + "role == :role"),
        @Query(
                name = "findByInvoiceAndAssignedToAndCompleted", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.task.TaskForIncomingInvoice "
                        + "WHERE invoice == :invoice && "
                        + "assignedTo == :assignedTo && "
                        + "completed == :completed")

})

@DomainObject(objectType = "invoice.TaskForIncomingInvoice" )
public class TaskForIncomingInvoice extends Task<TaskForIncomingInvoice> /* implements ApprovableTask */ {

    @Column(allowsNull = "false")
    @Getter @Setter
    private IncomingInvoiceTransition transition;

    @Column(allowsNull = "false")
    @Getter @Setter
    private IncomingInvoice invoice;


}
