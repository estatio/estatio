package org.estatio.module.capex.dom.task;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.InvokeOn;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.metamodel.MetaModelService3;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.applib.types.DescriptionType;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;
import org.isisaddons.module.security.dom.tenancy.HasAtPath;

import org.estatio.module.base.dom.apptenancy.WithApplicationTenancy;
import org.estatio.module.capex.app.taskreminder.TaskOverview;
import org.estatio.module.capex.dom.state.State;
import org.estatio.module.capex.dom.state.StateTransition;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.capex.dom.state.StateTransitionType;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.party.dom.role.PartyRoleType;

import lombok.Builder;
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
                        + "FROM org.estatio.module.capex.dom.task.Task "),
        @Query(
                name = "findIncomplete", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.task.Task "
                        + "WHERE completedBy == null "
                        + "ORDER BY personAssignedTo, createdOn ASC "),
        @Query(
                name = "findIncompleteByPersonAssignedTo", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.task.Task "
                        + "WHERE completedBy      == null "
                        + "   && personAssignedTo == :personAssignedTo "
                        + "ORDER BY createdOn ASC "),
        @Query(
                name = "findIncompleteByPersonAssignedToAndCreatedOnBefore", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.task.Task "
                        + "WHERE completedBy      == null "
                        + "   && personAssignedTo == :personAssignedTo "
                        + "   && createdOn        <  :createdOn "
                        + "ORDER BY createdOn DESC "),
        @Query(
                name = "findIncompleteByUnassigned", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.task.Task "
                        + "WHERE completedBy      == null "
                        + "   && personAssignedTo == null "
                        + "ORDER BY createdOn DESC "),
        @Query(
                name = "findIncompleteByUnassignedForRoles", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.task.Task "
                        + "WHERE completedBy      == null "
                        + "   && personAssignedTo == null "
                        + "   && :roleTypes.contains(assignedTo) "
                        + "ORDER BY createdOn DESC "),
        @Query(
                name = "findIncompleteByUnassignedForRolesAndCreatedOnAfter", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.task.Task "
                        + "WHERE completedBy      == null "
                        + "   && personAssignedTo == null "
                        + "   && :roleTypes.contains(assignedTo) "
                        + "   && createdOn        >  :createdOn "
                        + "ORDER BY createdOn ASC "),
        @Query(
                name = "findIncompleteByUnassignedForRolesAndCreatedOnBefore", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.task.Task "
                        + "WHERE completedBy      == null "
                        + "   && personAssignedTo == null "
                        + "   && :roleTypes.contains(assignedTo) "
                        + "   && createdOn        <  :createdOn "
                        + "ORDER BY createdOn DESC "),
        @Query(
                name = "findIncompleteByRole", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.task.Task "
                        + "WHERE completedBy      == null "
                        + "   && assignedTo == :roleType "
                        + "ORDER BY createdOn DESC "),
        @Query(
                name = "findIncompleteByPersonAssignedToAndCreatedOnAfter", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.task.Task "
                        + "WHERE completedBy      == null "
                        + "   && personAssignedTo == :personAssignedTo "
                        + "   && createdOn        >  :createdOn "
                        + "ORDER BY createdOn ASC ")
})
@DomainObject(objectType = "task.Task")
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class Task implements Comparable<Task>, WithApplicationTenancy {

    @Builder
    public Task(
            final PartyRoleType assignedTo,
            final Person personAssignedTo,
            final String description,
            final LocalDateTime createdOn,
            final String transitionObjectType) {
        this.assignedTo = assignedTo;
        this.personAssignedTo = personAssignedTo;
        this.description = trimIfRequired(description, DescriptionType.Meta.MAX_LEN);
        this.createdOn = createdOn;
        this.transitionObjectType = transitionObjectType;
    }

    private static String trimIfRequired(final String str, final int maxLen) {
        return str != null && str.length() > maxLen
                ? str.substring(0, maxLen)
                : str;
    }

    public String title() {
        final StringBuilder buf = new StringBuilder();
        buf.append(getDescription()).append(": ");
        appendTitleOfObject(buf);
        final Person personAssignedTo = getPersonAssignedTo();
        buf.append(" - ");
        if (personAssignedTo != null) {
            buf.append(personAssignedTo.getUsername());
        } else {
            buf.append(getAssignedTo().getKey());
        }
        return buf.toString();
    }

    void appendTitleOfObject(final StringBuilder buf) {
        buf.append(titleService.titleOf(getObject()));
    }

    @Getter @Setter
    @Column(allowsNull = "false", name = "assignedToId")
    private PartyRoleType assignedTo;

    @Getter @Setter
    @Column(allowsNull = "true", name = "personAssignedToId")
    private Person personAssignedTo;

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
     * The value held is the {@link MetaModelService3#toObjectType(Class) object type} of the corresponding {@link StateTransition}.
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

    /**
     * Copy of the {@link StateTransition#getCompletedOn()}  completedOn} property of the {@link StateTransition} that {@link StateTransition#getTask() refers} to this task.
     */
    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDateTime completedOn;

    /**
     * Used to determine whether a person has been reminded of their overdue tasks recently through {@link TaskOverview#sendReminder() sendReminder}.
     */
    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(hidden = Where.EVERYWHERE)
    private LocalDate remindedOn;

    /**
     * Copy of the {@link StateTransition#getCompletedBy()} () completedBy} property of the {@link StateTransition} that {@link StateTransition#getTask() refers} to this task.
     */
    @Getter @Setter
    @Column(allowsNull = "true")
    private String completedBy;

    /**
     * Copy of the {@link StateTransition#getComment() comment} property of the {@link StateTransition} that {@link StateTransition#getTask() refers} to this task.
     */
    @Column(allowsNull = "true")
    @Getter @Setter
    private String comment;

    @Column(allowsNull = "true")
    @Getter @Setter
    @Property(maxLength = DescriptionType.Meta.MAX_LEN)
    @PropertyLayout(multiLine = 3)
    private String note;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Task editNote(final @Parameter(maxLength = DescriptionType.Meta.MAX_LEN, optionality = Optionality.OPTIONAL) String note) {
        setNote(note);
        return this;
    }

    @Property(hidden = Where.ALL_TABLES)
    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        final String atPath = getAtPath();
        return atPath != null
                ? securityApplicationTenancyRepository.findByPathCached(atPath)
                : null;
    }

    @PropertyLayout(named = "Path")
    @Override
    public String getAtPath() {
        final Object domainObject = getObject();
        if (domainObject instanceof HasAtPath) {
            final HasAtPath hasAtPath = (HasAtPath) domainObject;
            return hasAtPath.getAtPath();
        }
        return null;
    }

    @Programmatic
    public void setToHighestPriority() {
        if (getPersonAssignedTo() != null) {
            taskRepository.findIncompleteByPersonAssignedTo(getPersonAssignedTo())
                    .stream()
                    .map(Task::getCreatedOn)
                    .min(LocalDateTime::compareTo)
                    .ifPresent(highestPriority -> setCreatedOn(highestPriority.minusDays(1)));
        } else {
            taskRepository.findIncompleteByUnassignedForRoles(Collections.singletonList(getAssignedTo()))
                    .stream()
                    .map(Task::getCreatedOn)
                    .min(LocalDateTime::compareTo)
                    .ifPresent(highestPriority -> setCreatedOn(highestPriority.minusDays(1)));
        }
    }

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
                transitions.stream()
                        .map(StateTransition::getTask)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }

    private Object getObject() {
        final StateTransition stateTransition = stateTransitionService.findFor(this);
        return stateTransition != null ? stateTransition.getDomainObject() : null;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT, invokeOn = InvokeOn.COLLECTION_ONLY)
    public Task assignTasksToMe() {
        setPersonAssignedTo(personRepository.me());
        return this;
    }

    public String disableAssignTasksToMe() {
        return isCompleted() ? "Task has already been completed" : null;
    }

    public String validateAssignTasksToMe() {
        final Person meAsPerson = personRepository.me();
        if (meAsPerson == null) {
            return "Your login is not linked to a person in Estatio";
        }
        if (!meAsPerson.hasPartyRoleType(getAssignedTo())) {
            return "You do not have a role with of role type found on the task";
        }
        return null;
    }

    @Override
    public int compareTo(final Task other) {
        return ObjectContracts.compare(this, other, "createdOn,transitionObjectType,description,comment");
    }

    @Inject
    UserService userService;

    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    TitleService titleService;

    @Inject
    PersonRepository personRepository;

    @Inject
    private ApplicationTenancyRepository securityApplicationTenancyRepository;

    @Inject
    TaskRepository taskRepository;
}
