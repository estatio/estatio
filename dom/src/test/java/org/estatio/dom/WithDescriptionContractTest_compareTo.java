package org.estatio.dom;

import java.util.List;


public abstract class WithDescriptionContractTest_compareTo<T extends WithDescription<T>> extends ComparableContractTest_compareTo<T>{

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

    protected abstract T newWithDescription();

}
