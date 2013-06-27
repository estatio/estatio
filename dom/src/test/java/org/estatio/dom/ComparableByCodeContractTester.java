package org.estatio.dom;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTester;


public class ComparableByCodeContractTester<T extends WithCodeComparable<T>> {

    private final Class<T> cls;
    
    public ComparableByCodeContractTester(Class<T> cls) {
        this.cls = cls;
    }

    public void test() {
        System.out.println("ComparableByCodeContractTester: " + cls.getName());
        new ComparableContractTester<T>(orderedTuples()).test();

        testToString();
        
    }

    protected void testToString() {
        final String str = "ABC";
        
        final T withCode = newWithCode(str);
        String expectedToString = Objects.toStringHelper(withCode).add("code", "ABC").toString();
        
        assertThat(withCode.toString(), is(expectedToString));
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
