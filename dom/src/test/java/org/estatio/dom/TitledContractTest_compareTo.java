package org.estatio.dom;

import java.util.List;


public abstract class TitledContractTest_compareTo<T extends Titled<T>> extends ComparableContractTest_compareTo<T>{

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<T>> orderedTuples() {
        return listOf(
                listOf(
                        newTitled(null), 
                        newTitled(1), 
                        newTitled(2), 
                        newTitled(3)
                    )
                );
    }
    
    protected abstract T newTitled(Integer order);

}
