package org.estatio.dom;

import java.util.List;

import com.google.common.collect.Lists;


public class ComparableByNameContractTester<T extends ComparableByName<T>> {

    private final Class<T> cls;
    
    public ComparableByNameContractTester(Class<T> cls) {
        this.cls = cls;
    }

    public void test() {
        System.out.println("ComparableByNameContractTester: " + cls.getName());
        new ComparableContractTester<T>(orderedTuples()).test();
    }

    @SuppressWarnings("unchecked")
    protected List<List<T>> orderedTuples() {
        return listOf(
                listOf(
                        newWithName(null), 
                        newWithName("ABC"), 
                        newWithName("ABC"), 
                        newWithName("DEF")));
    }
    
    private T newWithName(String reference) {
        final T wr = newWithName();
        wr.setName(reference);
        return wr;
    }

    private T newWithName() {
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
