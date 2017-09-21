package org.estatio.capex.dom.task;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.party.Person;
import org.estatio.dom.party.role.PartyRole;
import org.estatio.dom.party.role.PartyRoleRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@DomainObject(
        // WORKAROUND: using fqcn as objectType because Isis' invalidation of cache in prototyping mode causing NPEs in some situations
        objectType = "org.estatio.capex.dom.task.TransferTaskManager"
)
@XmlRootElement(name = "paymentBatchManager")
@XmlType(
        propOrder = {

        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
@RequiredArgsConstructor
public class TransferTaskManager {


    public String title() {
        return "Transfer tasks";
    }


    @Property(editing = Editing.ENABLED)
    @Getter @Setter @lombok.NonNull
    Person person1;
    public String validatePerson1(Person proposedPerson) {
        return validatePersons(proposedPerson, this.person2);
    }

    @Property(editing = Editing.ENABLED)
    @Getter @Setter @lombok.NonNull
    Person person2;
    public String validatePerson2(Person proposedPerson) {
        return validatePersons(proposedPerson, this.person1);
    }

    public enum Mode {
        SAME_ROLES,
        ANY_ROLES
    }


    @Property(editing = Editing.ENABLED)
    @Getter @Setter @lombok.NonNull
    private Mode mode;



    private String validatePersons(final Person proposedPerson, final Person otherPerson) {
        if(proposedPerson == otherPerson) {
            return "The two persons must be different from each other.";
        }
        return null;
    }


    public List<Task> getTasks1() {
        return taskRepository.findIncompleteByPersonAssignedTo(person1);
    }

    public List<PartyRole> getRoles1() {
        return partyRoleRepository.findByParty(person1);
    }

    public List<Task> getTasks2() {
        return taskRepository.findIncompleteByPersonAssignedTo(person2);
    }

    public List<PartyRole> getRoles2() {
        return partyRoleRepository.findByParty(person2);
    }



    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public TransferTaskManager pushTo(@Nullable final List<Task> tasks) {
        if(tasks != null) {
            for (Task task : tasks) {
                task.setPersonAssignedTo(person2);
            }
        }
        return this;
    }

    public List<Task> choices0PushTo() {
        return findTasks(this.person1, this.person2);
    }

    public List<Task> default0PushTo() {
        return choices0PushTo();
    }

    public String disablePushTo() {
        final List<Task> choices = choices0PushTo();
        return disableIfNone(choices);
    }


    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public TransferTaskManager pullFrom(@Nullable final List<Task> tasks) {
        if(tasks != null) {
            for (Task task : tasks) {
                task.setPersonAssignedTo(person1);
            }
        }
        return this;
    }

    public List<Task> choices0PullFrom() {
        return findTasks(this.person2, this.person1);
    }

    public List<Task> default0PullFrom() {
        return choices0PullFrom();
    }

    public String disablePullFrom() {
        return disableIfNone(choices0PullFrom());
    }



    private List<Task> findTasks(final Person from, final Person to) {
        return taskRepository.findIncompleteByPersonAssignedTo(from).stream()
                .filter(task -> checkRole(task, to))
                .collect(Collectors.toList());
    }

    private boolean checkRole(final Task task, final Person otherPerson) {
        return mode == Mode.ANY_ROLES || otherPerson.hasPartyRoleType(task.getAssignedTo());
    }

    private String disableIfNone(final List<Task> choices) {
        if (!choices.isEmpty()) {
            return null;
        }
        return mode == Mode.SAME_ROLES
                ? "No tasks (for same roles) to assign"
                : "No tasks to assign";

    }


    public TransferTaskManager switchSides() {
        Person personTemp = person2;
        person2 = person1;
        person1 = personTemp;
        return this;
    }



    @XmlTransient
    @Inject
    PartyRoleRepository partyRoleRepository;

    @XmlTransient
    @Inject
    TaskRepository taskRepository;


}
