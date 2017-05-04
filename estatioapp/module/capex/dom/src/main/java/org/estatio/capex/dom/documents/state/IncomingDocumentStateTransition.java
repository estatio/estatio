package org.estatio.capex.dom.documents.state;

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

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.task.Task;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "incomingDocument",
        table = "IncomingDocumentStateTransition"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id"
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@Queries({
        @Query(
                name = "findByDocumentAndTransitionTypeAndTaskCompleted", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.documents.state.IncomingDocumentStateTransition "
                        + "WHERE invoice == :invoice "
                        + "&& transitionType == :transitionType "
                        + "&& task.completed == :taskCompleted "
                        + "ORDER BY task.createdOn DESC "
        ),
        @Query(
                name = "findByDocument", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.documents.state.IncomingDocumentStateTransition "
                        + "WHERE invoice == :invoice "
                        + "ORDER BY task.createdOn DESC "
        ),
        @Query(
                name = "findByTask", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.documents.state.IncomingDocumentStateTransition "
                        + "WHERE task == :task "
        ),
})
@DomainObject(objectType = "incomingDocument.IncomingDocumentStateTransition" )
public class IncomingDocumentStateTransition
        implements
        StateTransition<Document, IncomingDocumentStateTransition, IncomingDocumentStateTransitionType, IncomingDocumentState> {

    public IncomingDocumentStateTransition(
            final IncomingDocumentStateTransitionType transitionType,
            final Document document,
            final Task task) {
        this.transitionType = transitionType;
        this.document = document;
        this.task = task;
    }

    /**
     * For the first transition, represents the initial state of the domain object
     * Thereafter, will hold the same value as the "to state" of the preceding transition.
     */
    @Column(allowsNull = "false")
    @Getter @Setter
    private IncomingDocumentState fromState;

    @Column(allowsNull = "false")
    @Getter @Setter
    private IncomingDocumentStateTransitionType transitionType;

    /**
     * If null, then this transition is not yet complete.
     */
    @Column(allowsNull = "true")
    @Getter @Setter
    private IncomingDocumentState toState;

    @Column(allowsNull = "false", name = "documentId")
    @Getter @Setter
    private Document document;

    /**
     * Not every transition necessarily has a task.
     */
    @Column(allowsNull = "true", name = "taskId")
    @Getter @Setter
    private Task task;

    @Programmatic
    @Override
    public Document getDomainObject() {
        return getDocument();
    }

    @Programmatic
    @Override
    public void completed() {
        setToState(getTransitionType().getToState());
    }

}
