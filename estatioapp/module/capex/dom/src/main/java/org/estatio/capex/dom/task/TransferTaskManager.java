package org.estatio.capex.dom.task;

import java.util.List;

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
import org.apache.isis.applib.services.title.TitleService;

import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
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
public class TransferTaskManager {


    public TransferTaskManager(final Person person1, final Person person2) {
        this.person1 = person1;
        this.person2 = person2;
    }


    public String title() {
        return "Transfer tasks";//titleService.titleOf(person1) + "  ~  " + titleService.titleOf(person2);
    }




    @Property(editing = Editing.ENABLED)
    @Getter @Setter
    Person person1;
    public String validatePerson1(Person proposedPerson) {
        return validatePersons(proposedPerson, this.person2);
    }

    @Property(editing = Editing.ENABLED)
    @Getter @Setter
    Person person2;
    public String validatePerson2(Person proposedPerson) {
        return validatePersons(proposedPerson, this.person1);
    }


    private String validatePersons(final Person proposedPerson, final Person otherPerson) {
        if(proposedPerson == otherPerson) {
            return "The two persons must be different from each other.";
        }
        return null;
    }


    public List<Task> getTasks1() {
        return taskRepository.findIncompleteByPersonAssignedTo(person1);
    }

    public List<Task> getTasks2() {
        return taskRepository.findIncompleteByPersonAssignedTo(person2);
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
        return taskRepository.findIncompleteByPersonAssignedTo(person1);
    }

    public List<Task> default0PushTo() {
        return choices0PushTo();
    }

    public String disablePushTo() {
        return choices0PushTo().isEmpty()  ? "No tasks to assign" : null;
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
        return taskRepository.findIncompleteByPersonAssignedTo(person2);
    }

    public List<Task> default0PullFrom() {
        return choices0PullFrom();
    }

    public String disablePullFrom() {
        return choices0PullFrom().isEmpty()  ? "No tasks to assign" : null;
    }




    public TransferTaskManager switchSides() {
        Person personTemp = person2;
        person2 = person1;
        person1 = personTemp;
        return this;
    }



    @XmlTransient
    @Inject
    PersonRepository personRepository;

    @XmlTransient
    @Inject
    TaskRepository taskRepository;

    @XmlTransient
    @Inject
    TitleService titleService;


}
