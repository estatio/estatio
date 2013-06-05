package org.estatio.dom;

import java.util.List;

import org.junit.Test;


public class WithReferenceContractTest_compareTo<T extends WithReference<T>> extends ComparableContractTest_compareTo<T>{

    private Class<T> cls;

    /**
     * For {@link WithReferenceContractAutoTest_compareTo auto-testing}.
     */
    public WithReferenceContractTest_compareTo<T> with(Class<T> cls) {
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
                        newWithReference(null), 
                        newWithReference("ABC"), 
                        newWithReference("ABC"), 
                        newWithReference("DEF")));
    }
    
    private T newWithReference(String reference) {
        final T wr = newWithReference();
        wr.setReference(reference);
        return wr;
    }

    /**
     * Manual tests should override this method.
     */
    protected T newWithReference() {
        try {
            return cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
