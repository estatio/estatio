package org.estatio.dom;

import java.util.List;


public abstract class WithCodeContractTest_compareTo<T extends WithCode<T>> extends ComparableContractTest_compareTo<T>{

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

    protected abstract T newWithCode();

}
