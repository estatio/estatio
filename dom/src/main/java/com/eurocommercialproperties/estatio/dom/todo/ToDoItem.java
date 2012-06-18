package com.eurocommercialproperties.estatio.dom.todo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;

public class ToDoItem extends AbstractDomainObject {

    public static final List<String> CATEGORIES = Collections.unmodifiableList(Arrays.asList("Professional", "Domestic", "Other"));

    // {{ Title
    public String title() {
        return getDescription();
    }

    // }}

    // {{ Description
    private String description;

    @MemberOrder(sequence = "1")
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
    // }}

    // {{ Category
    private String category;

    @MemberOrder(sequence = "2")
    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }
    public List<String> choicesCategory() {
        return CATEGORIES;
    }
    // }}

    // {{ Done
    private boolean done;

    @Disabled
    @MemberOrder(sequence = "3")
    public boolean isDone() {
        return done;
    }

    public void setDone(final boolean done) {
        this.done = done;
    }

    // }}
    
    // {{ OwnedBy (property)
    private String ownedBy;

    @Hidden
    public String getOwnedBy() {
        return ownedBy;
    }

    public void setOwnedBy(final String ownedBy) {
        this.ownedBy = ownedBy;
    }
    // }}


    // {{ markAsDone
    @MemberOrder(sequence = "1")
    public ToDoItem markAsDone() {
        setDone(true);
        return this;
    }

    public String disableMarkAsDone() {
        return done ? "Already done" : null;
    }
    // }}

    // {{ markAsNotDone
    @MemberOrder(sequence = "2")
    public ToDoItem markAsNotDone() {
        setDone(false);
        return this;
    }

    public String disableMarkAsNotDone() {
        return !done ? "Not yet done" : null;
    }
    // }}

    // {{ injected: ToDoItems
    @SuppressWarnings("unused")
	private ToDoItems toDoItems;

    public void setToDoItems(final ToDoItems toDoItems) {
        this.toDoItems = toDoItems;
    }
    // }}

}
