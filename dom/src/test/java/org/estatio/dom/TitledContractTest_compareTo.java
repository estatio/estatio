package org.estatio.dom;

import java.util.List;

import com.google.common.collect.Lists;


public abstract class TitledContractTest_compareTo<T extends Titled<T>> extends ComparableContractTest_compareTo<T>{

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<T>> orderedTuples() {
        return Lists.<List<T>>newArrayList(
                Lists.newArrayList(
                        newTitled(null), 
                        newTitled(1), 
                        newTitled(2), 
                        newTitled(3)
                    )
                );
    }
    
    protected abstract T newTitled(Integer order);

}
