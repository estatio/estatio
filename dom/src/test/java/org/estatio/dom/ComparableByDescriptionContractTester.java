package org.estatio.dom;

import java.util.List;

import com.google.common.collect.Lists;


public class ComparableByDescriptionContractTester<T extends ComparableByDescription<T>> {

    private final Class<T> cls;
    
    public ComparableByDescriptionContractTester(Class<T> cls) {
        this.cls = cls;
    }

    public void test() {
        System.out.println("ComparableByDescriptionContractTester: " + cls.getName());
        new ComparableContractTester<T>(orderedTuples()).test();
    }

    @SuppressWarnings("unchecked")
    protected List<List<T>> orderedTuples() {
        return listOf(
                listOf(
                        newWithDescription(null), 
                        newWithDescription("ABC"), 
                        newWithDescription("ABC"), 
                        newWithDescription("DEF")));
    }
    
    private T newWithDescription(String reference) {
        final T wr = newWithDescription();
        wr.setDescription(reference);
        return wr;
    }

    private T newWithDescription() {
        try {
            return cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <E> List<E> listOf(E... elements) {
        return Lists.newArrayList(elements);
    }

}
