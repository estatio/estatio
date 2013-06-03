package org.estatio.dom;

import java.util.List;

public abstract class WithTitleContractTest_compareTo<T extends WithTitle<T>> extends ComparableContractTest_compareTo<T>{

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

    protected abstract T newWithTitle();

}
