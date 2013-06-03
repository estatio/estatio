package org.estatio.dom;

import java.util.List;


public abstract class WithNameContractTest_compareTo<T extends WithName<T>> extends ComparableContractTest_compareTo<T>{

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

    protected abstract T newWithName();

}
