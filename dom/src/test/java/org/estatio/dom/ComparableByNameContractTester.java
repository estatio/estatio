package org.estatio.dom;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTester;


public class ComparableByNameContractTester<T extends ComparableByName<T>> {

    private final Class<T> cls;
    
    public ComparableByNameContractTester(Class<T> cls) {
        this.cls = cls;
    }

    public void test() {
        System.out.println("ComparableByNameContractTester: " + cls.getName());
        new ComparableContractTester<T>(orderedTuples()).test();
        
        testToString();
        
    }

    protected void testToString() {
        final String str = "ABC";
        
        final T withName = newWithName(str);
        String expectedToString = Objects.toStringHelper(withName).add("name", "ABC").toString();
        
        assertThat(withName.toString(), is(expectedToString));
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
