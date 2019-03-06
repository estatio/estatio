package org.estatio.module.capex.dom.task;

import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.PartyRoleType;

import static org.assertj.core.api.Assertions.assertThat;

public class Task_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    TaskRepository mockTaskRepository;

    PartyRoleType partyRoleType;
    Person person;
    Task task1;
    Task task2;
    Task task3;

    @Before
    public void setUp() throws Exception {
        partyRoleType = PartyRoleType.builder().key("PROJECT_MANAGER").build();
        person = Person.builder().username("jcapriata@abc.loc").build();
        task1 = new Task(partyRoleType, person, "Feed the cat", LocalDateTime.now().minusWeeks(2), "cat.Cat") {
            @Override
            void appendTitleOfObject(final StringBuilder buf) {
                buf.append("Tiddles");
            }
        };
        task2 = new Task(partyRoleType, person, "Feed the dog", LocalDateTime.now().minusWeeks(1), "dog.Dog") {
            @Override
            void appendTitleOfObject(final StringBuilder buf) {
                buf.append("Spot");
            }
        };
        task3 = new Task(partyRoleType, person, "Feed the parrot", LocalDateTime.now(), "parrot.Parrot") {
            @Override
            void appendTitleOfObject(final StringBuilder buf) {
                buf.append("Lory");
            }
        };
    }

    @Test
    public void setToHighestPriority_happyCase() {
        // given
        task3.taskRepository = mockTaskRepository;
        assertThat(task1.getCreatedOn().isBefore(task3.getCreatedOn())).isTrue();

        // expecting
        context.checking(new Expectations() {{
            oneOf(mockTaskRepository).findIncompleteByPersonAssignedTo(person);
            will(returnValue(Arrays.asList(task1, task2, task3)));
        }});

        // when
        task3.setToHighestPriority();

        // then
        assertThat(task3.getCreatedOn()).isEqualTo(task1.getCreatedOn().minusDays(1));
    }
}
