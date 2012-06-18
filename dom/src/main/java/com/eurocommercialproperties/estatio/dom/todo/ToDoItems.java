package com.eurocommercialproperties.estatio.dom.todo;

import java.util.List;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.QueryOnly;

@Named("ToDos")
public interface ToDoItems {

    @QueryOnly
    @MemberOrder(sequence = "1")
    public List<ToDoItem> notYetDone();

    @MemberOrder(sequence = "2")
    public ToDoItem newToDo(
            @Named("Description") String description, 
            @Named("Category") String category);

    @Hidden // for use by fixtures
    public ToDoItem newToDo(
            String description, 
            String category, 
            String ownedBy);

    @QueryOnly
    @MemberOrder(sequence = "3")
    public List<ToDoItem> similarTo(ToDoItem toDoItem);

}
