package com.eurocommercialproperties.estatio.fixture.todo;


import org.apache.isis.applib.fixtures.AbstractFixture;

import com.eurocommercialproperties.estatio.dom.todo.ToDoItem;
import com.eurocommercialproperties.estatio.dom.todo.ToDoItems;

public class ToDoItemsFixture extends AbstractFixture {

    @Override
    public void install() {
        createToDoItem("Buy milk", "Domestic", "sven");
        createToDoItem("Pick up laundry", "Domestic", "sven");
        createToDoItem("Buy stamps", "Domestic", "sven");
        createToDoItem("Write blog post", "Professional", "sven");
        createToDoItem("Organize brown bag", "Professional", "sven");
        
        createToDoItem("Book car in for service", "Domestic", "dick");
        createToDoItem("Buy birthday present for sven", "Domestic", "dick");
        createToDoItem("Write presentation for conference", "Professional", "dick");

        createToDoItem("Write thank you notes", "Domestic", "bob");
        createToDoItem("Look into solar panels", "Domestic", "bob");

        createToDoItem("Pitch book idea to publisher", "Professional", "joe");
    }

    private ToDoItem createToDoItem(final String description, String category, String ownedBy) {
        return toDoItems.newToDo(description, category, ownedBy);
    }

    private ToDoItems toDoItems;

    public void setToDoItemRepository(final ToDoItems toDoItems) {
        this.toDoItems = toDoItems;
    }

}
