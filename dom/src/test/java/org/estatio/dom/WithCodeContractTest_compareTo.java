package org.estatio.dom;

import java.util.List;

import org.junit.Test;


public class WithCodeContractTest_compareTo<T extends WithCode<T>> extends ComparableContractTest_compareTo<T>{

    private Class<T> cls;

    /**
     * For {@link WithCodeContractAutoTest_compareTo auto-testing}.
     */
    public WithCodeContractTest_compareTo<T> with(Class<T> cls) {
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
                        newWithCode(null), 
                        newWithCode("ABC"), 
                        newWithCode("ABC"), 
                        newWithCode("DEF")
                    ));
    }

    private T newWithCode(String code) {
        final T wc = newWithCode();
        wc.setCode(code);
        return wc;
    }

    /**
     * Manual tests should override this method.
     */
    protected T newWithCode() {
        try {
            return cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
