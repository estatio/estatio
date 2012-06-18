package com.eurocommercialproperties.estatio.objstore.dflt.todo;

import java.util.List;

import com.eurocommercialproperties.estatio.dom.todo.ToDoItem;
import com.eurocommercialproperties.estatio.dom.todo.ToDoItems;
import com.google.common.base.Objects;


import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.filter.Filter;

public class ToDoItemsDefault extends AbstractFactoryAndRepository implements ToDoItems {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "toDoItems";
    }

    public String iconName() {
        return "ToDoItem";
    }

    // }}

    @Override
    public List<ToDoItem> notYetDone() {
        final String userName = getContainer().getUser().getName();
        return allMatches(ToDoItem.class, new Filter<ToDoItem>() {
            @Override
            public boolean accept(final ToDoItem t) {
                return Objects.equal(t.getOwnedBy(), userName) && !t.isDone();
            }
        });
    }

    // {{ NewToDo  (action)
    @Override
    public ToDoItem newToDo(final String description, String category) {
        final String ownedBy = getContainer().getUser().getName();
        return newToDo(description, category, ownedBy);
    }
    public List<String> choices1NewToDo() {
        return ToDoItem.CATEGORIES;
    }
    // }}

    // {{ NewToDo  (hidden)
    @Override
    public ToDoItem newToDo(final String description, String category, String ownedBy) {
        final ToDoItem toDoItem = newTransientInstance(ToDoItem.class);
        toDoItem.setDescription(description);
        toDoItem.setCategory(category);
        toDoItem.setOwnedBy(ownedBy);
        persist(toDoItem);
        return toDoItem;
    }
    // }}

    // {{ SimilarTo (action)
    @Override
    public List<ToDoItem> similarTo(final ToDoItem toDoItem) {
        return allMatches(ToDoItem.class, new Filter<ToDoItem>() {
            @Override
            public boolean accept(ToDoItem t) {
                return t != toDoItem && Objects.equal(toDoItem.getCategory(), t.getCategory()) && Objects.equal(toDoItem.getOwnedBy(), t.getOwnedBy());
            }
        });
    }
    // }}


}
