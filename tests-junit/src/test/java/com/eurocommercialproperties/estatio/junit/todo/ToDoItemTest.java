package com.eurocommercialproperties.estatio.junit.todo;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.progmodel.wrapper.applib.DisabledException;
import org.apache.isis.viewer.junit.Fixture;
import org.apache.isis.viewer.junit.Fixtures;

import com.eurocommercialproperties.estatio.dom.todo.ToDoItem;
import com.eurocommercialproperties.estatio.fixture.LogonAsSvenFixture;
import com.eurocommercialproperties.estatio.fixture.todo.ToDoItemsFixture;
import com.eurocommercialproperties.estatio.junit.AbstractTest;

@Fixtures({ @Fixture(ToDoItemsFixture.class), @Fixture(LogonAsSvenFixture.class) })
public class ToDoItemTest extends AbstractTest {

    private ToDoItem toDoItem;

    @Override
    @Before
    public void setUp() {
        toDoItem = toDoItems.notYetDone().get(0);
        toDoItem = wrapped(toDoItem);
    }

    @Test
    public void canMarkAsDone() throws Exception {
        toDoItem.markAsDone();
        assertThat(toDoItem.isDone(), is(true));
    }

    @Test
    public void cannotMarkAsDoneTwice() throws Exception {
        toDoItem.markAsDone();
        try {
            toDoItem.markAsDone();
            fail("Should have been disabled");
        } catch (final DisabledException e) {
            assertThat(e.getMessage(), is("Already done"));
        }
    }

}
