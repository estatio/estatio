package org.estatio.dom;

import java.util.List;

import org.junit.Test;


public class WithDescriptionContractTest_compareTo<T extends WithDescription<T>> extends ComparableContractTest_compareTo<T>{

    private Class<T> cls;

    /**
     * For {@link WithDescriptionContractAutoTest_compareTo auto-testing}.
     */
    public WithDescriptionContractTest_compareTo<T> with(Class<T> cls) {
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
                        newWithDescription(null), 
                        newWithDescription("ABC"), 
                        newWithDescription("ABC"), 
                        newWithDescription("DEF")
                    ));
    }
    
    private T newWithDescription(String description) {
        final T wc = newWithDescription();
        wc.setDescription(description);
        return wc;
    }

    /**
     * Manual tests should override this method.
     */
    protected T newWithDescription() {
        try {
            return cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
