package org.estatio.dom;

import java.util.List;

import com.google.common.collect.Lists;


public class ComparableByReferenceContractTester<T extends ComparableByReference<T>> {

    private final Class<T> cls;
    
    public ComparableByReferenceContractTester(Class<T> cls) {
        this.cls = cls;
    }

    public void test() {
        System.out.println("ComparableByReferenceContractTester: " + cls.getName());
        new ComparableContractTester<T>(orderedTuples()).test();
    }

    @SuppressWarnings("unchecked")
    protected List<List<T>> orderedTuples() {
        return listOf(
                listOf(
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

    private T newWithReference() {
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
