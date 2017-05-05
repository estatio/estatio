package org.estatio.capex.dom.task;

import java.util.List;

import javax.annotation.Nullable;
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

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.applib.types.DescriptionType;

import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.state.StateTransitionService;
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
                        + "FROM org.estatio.capex.dom.task.Task ")
})
@DomainObject(objectType = "task.Task")
public class Task implements Comparable<Task> {

    public Task(
            final EstatioRole assignedTo,
            final String description,
            final String transitionObjectType,
            final LocalDateTime createdOn) {
        this.assignedTo = assignedTo;
        this.description = description;
        this.transitionObjectType = transitionObjectType;
        this.createdOn = createdOn;
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


    @Column(allowsNull = "false")
    @Getter @Setter
    private boolean completed;
    // because code completion in IntelliJ doesn't pick this up...
    public boolean isCompleted() {
        return completed;
    }


    @Getter @Setter
    @Column(allowsNull = "false")
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

    @Mixin(method="act")
    public static class execute<
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > {

        private final Task task;
        public execute(final Task task ) {
            this.task = task;
        }

        @Action(semantics = SemanticsOf.IDEMPOTENT)
        @ActionLayout(contributed = Contributed.AS_ACTION)
        public DO act(
                @Nullable
                final String comment) {
            ST stateTransition = stateTransitionService.findFor(task);
            DO domainObject = stateTransition.getDomainObject();
            stateTransitionService.apply(stateTransition, comment);
            return domainObject;
        }
        public String disableAct() {
            return task.isCompleted()
                    ? String.format("Already completed (on %s by %s)", task.getCompletedBy(), task.getCompletedBy())
                    : null;
        }

        private STT transitionTypeFor(Task task) {
            return stateTransitionService.findFor(task);
        }

        @Inject
        StateTransitionService stateTransitionService;
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
                FluentIterable.from(transitions).transform(StateTransition::getTask).toList()
        );
    }


    @Programmatic
    public void completed(final String comment) {

        final LocalDateTime completedOn = clockService.nowAsLocalDateTime();
        final String completedBy = userService.getUser().getName();

        setCompleted(true);
        setCompletedOn(completedOn);
        setCompletedBy(completedBy);

        setComment(comment);
    }


    @Override
    public int compareTo(final Task other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "createdOn");
    }

    @Inject
    UserService userService;

    @Inject
    ClockService clockService;

}
