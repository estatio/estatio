package org.estatio.dom.utils;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class StringUtilsTest_enumTitle {

    private String from;
    private String to;

    @Parameters
    public static Collection<Object[]> values() {
        return Arrays.asList(
                new Object[][]{
                    {"FOO", "Foo"},
                    {"FOO_BAR", "Foo Bar"},
                    {null, null},
                }
            );
    }
    
    public StringUtilsTest_enumTitle(String from, String to) {
        this.from = from;
        this.to = to;
    }
    
    @Test
    public void nonNull() throws Exception {
        assertEquals(to, StringUtils.enumTitle(from));
    }
    
}
