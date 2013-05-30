package org.estatio.dom;

import java.util.List;

import com.google.common.collect.Lists;


public abstract class WithReferenceContractTest_compareTo<T extends WithReference<T>> extends ComparableContractTest_compareTo<T>{

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<T>> orderedTuples() {
        return Lists.<List<T>>newArrayList(
                Lists.newArrayList(
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

    protected abstract T newWithReference();

}
