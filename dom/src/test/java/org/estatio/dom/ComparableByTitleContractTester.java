package org.estatio.dom;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTester;


public class ComparableByTitleContractTester<T extends WithTitleComparable<T>> {

    private final Class<T> cls;
    
    public ComparableByTitleContractTester(Class<T> cls) {
        this.cls = cls;
    }

    public void test() {
        System.out.println("ComparableByTitleContractTester: " + cls.getName());
        new ComparableContractTester<T>(orderedTuples()).test();

        testToString();
        
    }

    protected void testToString() {
        final String str = "ABC";
        
        final T withTitle = newWithTitle(str);
        String expectedToString = Objects.toStringHelper(withTitle).add("title", "ABC").toString();
        
        assertThat(withTitle.toString(), is(expectedToString));
    }

    @SuppressWarnings("unchecked")
    protected List<List<T>> orderedTuples() {
        return listOf(
                listOf(
                        newWithTitle(null), 
                        newWithTitle("ABC"), 
                        newWithTitle("ABC"), 
                        newWithTitle("DEF")));
    }
    
    private T newWithTitle(String reference) {
        final T wr = newWithTitle();
        wr.setTitle(reference);
        return wr;
    }

    private T newWithTitle() {
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
