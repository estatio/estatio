package org.estatio.dom;

import java.util.List;

import com.google.common.collect.Lists;


public class ComparableByCodeContractTester<T extends ComparableByCode<T>> {

    private final Class<T> cls;
    
    public ComparableByCodeContractTester(Class<T> cls) {
        this.cls = cls;
    }

    public void test() {
        System.out.println("ComparableByCodeContractTester: " + cls.getName());
        new ComparableContractTester<T>(orderedTuples()).test();
    }

    @SuppressWarnings("unchecked")
    protected List<List<T>> orderedTuples() {
        return listOf(
                listOf(
                        newWithCode(null), 
                        newWithCode("ABC"), 
                        newWithCode("ABC"), 
                        newWithCode("DEF")));
    }
    
    private T newWithCode(String reference) {
        final T wr = newWithCode();
        wr.setCode(reference);
        return wr;
    }

    private T newWithCode() {
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
