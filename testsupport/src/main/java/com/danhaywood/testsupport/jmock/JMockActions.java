package com.danhaywood.testsupport.jmock;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

public final class JMockActions {
    
    private JMockActions() {
    }
    
    public static <T> Action returnEach(final T... values) {
        return new ReturnEachAction<T>(values);
    }

    public static Action returnArgument(final int i) {
        return new ReturnArgumentJMockAction(i);
    }

    private static class ReturnEachAction<T> implements Action {
        
        private final Collection<T> collection;
        private final Iterator<T> iterator;
        
        ReturnEachAction(Collection<T> collection) {
            this.collection = collection;
            this.iterator = collection.iterator();
        }
        
        private ReturnEachAction(T... array) {
            this(Arrays.asList(array));
        }
        
        public T invoke(Invocation invocation) throws Throwable {
            return iterator.next();
        }
        
        public void describeTo(Description description) {
            description.appendValueList("return iterator.next() over ", ", ", "", collection);
        }
    }

    private static final class ReturnArgumentJMockAction implements Action {
        private final int i;

        private ReturnArgumentJMockAction(final int i) {
            this.i = i;
        }

        public void describeTo(final Description description) {
            description.appendText("parameter #" + i + " ");
        }

        public Object invoke(final Invocation invocation) throws Throwable {
            return invocation.getParameter(i);
        }
    }
}
