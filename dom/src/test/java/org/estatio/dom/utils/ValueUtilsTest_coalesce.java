package org.estatio.dom.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ValueUtilsTest_coalesce {

    @Test
    public void firstNonNull() throws Exception {
        assertThat(ValueUtils.coalesce("foo", "bar"), is("foo"));
    }
    
    @Test
    public void secondNonNull() throws Exception {
        assertThat(ValueUtils.coalesce(null, "bar"), is("bar"));
    }
    
    @Test
    public void bothNull() throws Exception {
        assertThat(ValueUtils.<String>coalesce(null, null), is((String)null));
    }
}
