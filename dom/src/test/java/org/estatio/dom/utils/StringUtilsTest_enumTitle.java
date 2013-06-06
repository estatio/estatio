package org.estatio.dom.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class StringUtilsTest_enumTitle {

    @Test
    public void enumTitle() {
        assertThat(StringUtils.enumTitle("FOO"), is("Foo"));
        assertThat(StringUtils.enumTitle("FOO_BAR"), is("Foo Bar"));
    }

    @Test
    public void enumDeTitle() {
        assertThat(StringUtils.enumDeTitle("Foo"), is("FOO"));
        assertThat(StringUtils.enumDeTitle("Foo Bar"), is("FOO_BAR"));
    }

}
