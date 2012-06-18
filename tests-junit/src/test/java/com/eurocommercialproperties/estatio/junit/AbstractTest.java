package com.eurocommercialproperties.estatio.junit;


import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.progmodel.wrapper.applib.WrapperFactory;
import org.apache.isis.progmodel.wrapper.applib.WrapperObject;
import org.apache.isis.viewer.junit.ConfigDir;
import org.apache.isis.viewer.junit.IsisTestRunner;
import org.apache.isis.viewer.junit.Service;
import org.apache.isis.viewer.junit.Services;

import com.eurocommercialproperties.estatio.dom.todo.ToDoItems;
import com.eurocommercialproperties.estatio.objstore.dflt.todo.ToDoItemsDefault;

@RunWith(IsisTestRunner.class)
@ConfigDir("../webapp/src/main/webapp/WEB-INF") // acts as default, but can be overridden by annotations
@Services({ @Service(ToDoItemsDefault.class) })
public abstract class AbstractTest {

    private DomainObjectContainer domainObjectContainer;
    private WrapperFactory wrapperFactory;

    /**
     * The {@link WrapperFactory#wrap(Object) wrapped} equivalent of the
     * {@link #setToDoItems(ToDoItems) injected} {@link ToDoItems}.
     */
    protected ToDoItems toDoItems;

    @Before
    public void wrapInjectedServices() throws Exception {
        toDoItems = wrapped(toDoItems);
    }

    @Before
    public void setUp() throws Exception {
    }

    protected <T> T wrapped(final T obj) {
        return wrapperFactory.wrap(obj);
    }

    @SuppressWarnings("unchecked")
    protected <T> T unwrapped(final T obj) {
        if (obj instanceof WrapperObject) {
            final WrapperObject wrapperObject = (WrapperObject) obj;
            return (T) wrapperObject.wrapped();
        }
        return obj;
    }

    @After
    public void tearDown() throws Exception {
    }

    // //////////////////////////////////////////////////////
    // Injected.
    // //////////////////////////////////////////////////////

    protected WrapperFactory getWrapperFactory() {
        return wrapperFactory;
    }

    public void setWrapperFactory(final WrapperFactory wrapperFactory) {
        this.wrapperFactory = wrapperFactory;
    }

    protected DomainObjectContainer getDomainObjectContainer() {
        return domainObjectContainer;
    }

    public void setDomainObjectContainer(final DomainObjectContainer domainObjectContainer) {
        this.domainObjectContainer = domainObjectContainer;
    }

    public void setToDoItems(final ToDoItems toDoItems) {
        this.toDoItems = toDoItems;
    }

}
