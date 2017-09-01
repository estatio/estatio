package org.estatio.capex.dom.documents.categorisation;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;

import org.joda.time.LocalDateTime;
import org.wicketstuff.pdfjs.Scale;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.wicket.pdfjs.cpt.applib.PdfJsViewer;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.state.StateTransitionAbstract;
import org.estatio.capex.dom.state.StateTransitionRepositoryAbstract;
import org.estatio.capex.dom.task.Task;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "IncomingDocumentCategorisationStateTransition"
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
                        + "FROM org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition "
                        + "WHERE document == :domainObject "
                        + "ORDER BY completedOn DESC "
        ),
        @Query(
                name = "findByDomainObjectAndCompleted", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition "
                        + "WHERE document == :domainObject "
                        + "&& completed == :completed "
                        + "ORDER BY completedOn DESC "
        ),
        @Query(
                name = "findByTask", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition "
                        + "WHERE task == :task "
        ),
})
@DomainObject(objectType = "incomingDocument.IncomingDocumentCategorisationStateTransition" )
public class IncomingDocumentCategorisationStateTransition
        extends StateTransitionAbstract<
                    Document,
        IncomingDocumentCategorisationStateTransition,
        IncomingDocumentCategorisationStateTransitionType,
        IncomingDocumentCategorisationState> {



    /**
     * Null only for the first transition (which will be complete), thereafter is always populated and
     * corresponds to the current state of the domain object.
     */
    @Column(allowsNull = "true")
    @Getter @Setter
    private IncomingDocumentCategorisationState fromState;

    @Column(allowsNull = "false")
    @Getter @Setter
    private IncomingDocumentCategorisationStateTransitionType transitionType;

    /**
     * The most recent non-null value corresponds to the current state of the domain object.
     * If null, then this transition is not yet complete.
     */
    @Column(allowsNull = "true")
    @Getter @Setter
    private IncomingDocumentCategorisationState toState;

    @Column(allowsNull = "false", name = "documentId")
    @Getter @Setter
    private Document document;


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
    public Document getDomainObject() {
        return getDocument();
    }

    @Programmatic
    @Override
    public void setDomainObject(final Document domainObject) {
        setDocument(domainObject);
    }

    @DomainService(
            nature = NatureOfService.DOMAIN,
            repositoryFor = IncomingInvoiceApprovalStateTransition.class
    )
    public static class Repository
            extends StateTransitionRepositoryAbstract<
                        Document,
                        IncomingDocumentCategorisationStateTransition,
            IncomingDocumentCategorisationStateTransitionType,
                        IncomingDocumentCategorisationState> {

        public Repository() {
            super(IncomingDocumentCategorisationStateTransition.class);
        }

    }


    //region > _pdf (derived property)

    /**
     * TODO: inline
     */
    @Mixin(method="prop")
    public static class _pdf {
        private final IncomingDocumentCategorisationStateTransition stateTransition;
        public _pdf(final IncomingDocumentCategorisationStateTransition stateTransition) {
            this.stateTransition = stateTransition;
        }

        public static class DomainEvent extends ActionDomainEvent<Document> {}

        @PdfJsViewer(initialPageNum = 1, initialScale = Scale.PAGE_WIDTH, initialHeight = 1500)
        @Action(
                semantics = SemanticsOf.SAFE,
                domainEvent = IncomingDocumentCategorisationStateTransition._pdf.DomainEvent.class
        )
        @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
        public Blob prop() {
            return stateTransition.getDocument().getBlob();
        }
    }

}
