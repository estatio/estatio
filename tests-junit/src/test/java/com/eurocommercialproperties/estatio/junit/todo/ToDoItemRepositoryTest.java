package com.eurocommercialproperties.estatio.junit.todo;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;


import org.junit.Test;

import org.apache.isis.viewer.junit.Fixture;
import org.apache.isis.viewer.junit.Fixtures;

import com.eurocommercialproperties.estatio.dom.todo.ToDoItem;
import com.eurocommercialproperties.estatio.fixture.LogonAsSvenFixture;
import com.eurocommercialproperties.estatio.fixture.todo.ToDoItemsFixture;
import com.eurocommercialproperties.estatio.junit.AbstractTest;

@Fixtures({ @Fixture(ToDoItemsFixture.class), @Fixture(LogonAsSvenFixture.class) })
public class ToDoItemRepositoryTest extends AbstractTest {

    @Test
    public void canFindAllItemsNotYetDone() throws Exception {
        final List<ToDoItem> foobarList = toDoItems.notYetDone();
        assertThat(foobarList.size(), is(5));
    }

    @Test
    public void canCreateToDoItem() throws Exception {
        final ToDoItem newItem = toDoItems.newToDo("item description", "Professional");
        assertThat(newItem, is(not(nullValue())));
        assertThat(newItem.getDescription(), is("item description"));
        assertThat(getDomainObjectContainer().isPersistent(newItem), is(true));
    }

}
