package org.estatio.module.capex.dom.order;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class OrderRepository_withTrailingSlash_Test {

    @Test
    public void when_null() throws Exception {
        assertThat(OrderRepository.withTrailingSlash(null), is(nullValue()));
    }

    @Test
    public void when_ends_with_null() throws Exception {
        assertThat(OrderRepository.withTrailingSlash("xxx/"), is("xxx/"));
    }

    @Test
    public void when_does_not_end_with_null() throws Exception {
        assertThat(OrderRepository.withTrailingSlash("xxx"), is("xxx/"));
    }

    @Test
    public void when_has_white_space() throws Exception {
        assertThat(OrderRepository.withTrailingSlash("  xxx\t "), is("xxx/"));
    }
}