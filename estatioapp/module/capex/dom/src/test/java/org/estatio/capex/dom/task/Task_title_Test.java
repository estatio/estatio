package org.estatio.capex.dom.task;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.PartyRoleType;

import static org.assertj.core.api.Assertions.assertThat;

public class Task_title_Test {

    PartyRoleType partyRoleType;
    Person person;
    Task task;

    @Before
    public void setUp() throws Exception {
        partyRoleType = PartyRoleType.builder().key("PROJECT_MANAGER").build();
        person = Person.builder().username("jcapriata@abc.loc").build();
        task = new Task(partyRoleType, person, "Feed the cat", LocalDateTime.now(), "cat.Cat") {
            @Override
            void appendTitleOfObject(final StringBuilder buf) {
                buf.append("Tiddles");
            }
        };
    }

    @Test
    public void when_assigned_to_person() throws Exception {

        // given
        assertThat(task.getPersonAssignedTo()).isNotNull();

        // when
        final String title = task.title();

        // then
        assertThat(title).isEqualTo("Feed the cat: Tiddles - jcapriata@abc.loc");
    }

    @Test
    public void when_assigned_to_noone() throws Exception {

        // given
        task.setPersonAssignedTo(null);

        // when
        final String title = task.title();

        // then
        assertThat(title).isEqualTo("Feed the cat: Tiddles - PROJECT_MANAGER");
    }

}