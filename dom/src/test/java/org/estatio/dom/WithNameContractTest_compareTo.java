package org.estatio.dom;

import java.util.List;

import org.junit.Test;


public class WithNameContractTest_compareTo<T extends WithName<T>> extends ComparableContractTest_compareTo<T>{

    private Class<T> cls;

    /**
     * For {@link WithNameContractAutoTest_compareTo auto-testing}.
     */
    public WithNameContractTest_compareTo<T> with(Class<T> cls) {
        this.cls = cls;
        return this;
    }

    @Test
    public void compareAllOrderedTuples() {
        if(cls == null) {
            return;
        }
        super.compareAllOrderedTuples();
    }


    @SuppressWarnings("unchecked")
    @Override
    protected List<List<T>> orderedTuples() {
        return listOf(
                listOf(
                        newWithName(null), 
                        newWithName("ABC"), 
                        newWithName("ABC"), 
                        newWithName("DEF")
                    ));
    }
    
    private T newWithName(String name) {
        final T wn = newWithName();
        wn.setName(name);
        return wn;
    }

    /**
     * Manual tests should override this method.
     */
    protected T newWithName() {
        try {
            return cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
