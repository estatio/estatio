package org.estatio.capex.dom.task;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.applib.types.DescriptionType;

import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.dom.roles.EstatioRole;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "find", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.task.Task "),
        @Query(
                name = "findByAssignedToIncomplete", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.task.Task "
                        + "WHERE assignedTo == :assignedTo "
                        + "   && completedBy == null "
                        + "ORDER BY createdOn ASC "
        )
})
@DomainObject(objectType = "task.Task")
public class Task implements Comparable<Task> {

    public Task(
            final EstatioRole assignedTo,
            final String description,
            final LocalDateTime createdOn,
            final String transitionObjectType) {
        this.assignedTo = assignedTo;
        this.description = description;
        this.createdOn = createdOn;
        this.transitionObjectType = transitionObjectType;
    }

    @Getter @Setter
    @Column(allowsNull = "false")
    private EstatioRole assignedTo;

    /**
     * Human friendly name, used for the title.
     */
    @Column(allowsNull = "false", length = DescriptionType.Meta.MAX_LEN)
    @Getter @Setter
    @Title
    private String description;

    /**
     * Acts as a discriminator, making it more efficient to lookup the {@link StateTransition} that refers back to
     * this {@link Task}.
     *
     * <p>
     *     The value held is the {@link org.apache.isis.applib.services.metamodel.MetaModelService3#toObjectType(Class) object type} of the corresponding {@link StateTransition}.
     * </p>
     */
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    @Column(allowsNull = "false")
    private String transitionObjectType;

    public boolean isCompleted() {
        return getCompletedBy() != null;
    }

    @Column(allowsNull = "false")
    @Getter @Setter
    private LocalDateTime createdOn;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDateTime completedOn;

    @Getter @Setter
    @Column(allowsNull = "true")
    private String completedBy;

    @Column(allowsNull = "true")
    @Getter @Setter
    private String comment;

    /**
     * Convenience method to (naively) convert a list of {@link StateTransition}s to their corresponding {@link Task}.
     */
    public static <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    > Task from(final ST transition) {
        return transition != null ? transition.getTask() : null;
    }

    /**
     * Convenience method to (naively) convert a list of {@link StateTransition}s to their corresponding {@link Task}.
     */
    public static <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
    > List<Task> from(final List<ST> transitions) {
        return Lists.newArrayList(
                FluentIterable.from(transitions).transform(StateTransition::getTask)
                        .filter(Objects::nonNull).toList()
        );
    }


    @Programmatic
    public void completed(final String comment) {

        final String completedBy = userService.getUser().getName();

        setCompletedBy(completedBy);
        setComment(comment);
    }


    @Override
    public int compareTo(final Task other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "createdOn,transitionObjectType,description,comment");
    }

    @Inject
    UserService userService;

    /**
     * Base class for mixins on {@link Task} that delegate to a corresponding mixin on some domain object which will
     * result in a {@link Task} being completed.
     */
    public abstract static class _mixinAbstract<M, DO> {

        protected final org.estatio.capex.dom.task.Task task;
        private final Class<M> mixinClass;

        public _mixinAbstract(final org.estatio.capex.dom.task.Task task, final Class<M> mixinClass) {
            this.task = task;
            this.mixinClass = mixinClass;
        }

        protected org.estatio.capex.dom.task.Task taskToReturn(final boolean goToNext, final org.estatio.capex.dom.task.Task task) {
            if (goToNext){
                final org.estatio.capex.dom.task.Task nextTask = nextTask();
                if (nextTask != null) {
                    return nextTask;
                }
                // fall through to returning the view model for this document
                messageService.informUser("No more tasks");
            }

            return task;
        }

        private org.estatio.capex.dom.task.Task nextTask() {
            final List<org.estatio.capex.dom.task.Task> tasks = taskRepository.findMyTasksIncomplete();
            return tasks.size() > 0 ? tasks.get(0) : null;
        }

        /**
         * Subclasses should override and make <tt>public</tt>.
         */
        protected boolean hideAct() {
            return task.isCompleted() || getDomainObjectIfAny() == null;
        }

        protected DO getDomainObjectIfAny() {
//            return doGetDomainObjectIfAny();
            return queryResultsCache.execute(
                    this::doGetDomainObjectIfAny,
                    getClass(), "getDomainObjectIfAny", task);
        }

        protected M mixin() {
            return factoryService.mixin(mixinClass, doGetDomainObjectIfAny());
        }

        protected abstract DO doGetDomainObjectIfAny();

        @Inject
        TaskRepository taskRepository;

        @Inject
        MessageService messageService;

        @Inject
        FactoryService factoryService;

        @Inject
        QueryResultsCache queryResultsCache;

    }

}
