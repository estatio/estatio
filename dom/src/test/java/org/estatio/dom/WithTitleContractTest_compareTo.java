package org.estatio.dom;

import java.util.List;

import org.junit.Test;

public class WithTitleContractTest_compareTo<T extends WithTitle<T>> extends ComparableContractTest_compareTo<T>{

    private Class<T> cls;

    /**
     * For {@link WithTitleContractAutoTest_compareTo auto-testing}.
     */
    public WithTitleContractTest_compareTo<T> with(Class<T> cls) {
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
                        newWithTitle(null), 
                        newWithTitle("ABC"), 
                        newWithTitle("ABC"), 
                        newWithTitle("DEF")
                    ));
    }
    
    private T newWithTitle(String title) {
        final T wt = newWithTitle();
        wt.setTitle(title);
        return wt;
    }

    /**
     * Manual tests should override this method.
     */
    protected T newWithTitle() {
        try {
            return cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
