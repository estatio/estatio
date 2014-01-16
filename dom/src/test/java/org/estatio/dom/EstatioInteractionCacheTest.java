package org.estatio.dom;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.concurrent.Callable;

import org.junit.Test;

public class EstatioInteractionCacheTest {

    @Test
    public void nesting() {
        assertThat(EstatioInteractionCache.get(), is(nullValue()));
        
        boolean outer = EstatioInteractionCache.startInteraction();
        assertThat(EstatioInteractionCache.get(), is(not(nullValue())));
        assertThat(outer, is(true));

        boolean inner = EstatioInteractionCache.startInteraction();
        assertThat(EstatioInteractionCache.get(), is(not(nullValue())));
        assertThat(inner, is(false));

        EstatioInteractionCache.endInteraction(inner);
        assertThat(EstatioInteractionCache.get(), is(not(nullValue())));

        EstatioInteractionCache.endInteraction(outer);
        assertThat(EstatioInteractionCache.get(), is(nullValue()));
    }

    
    @Test
    public void cachingWhenNoCache() {
        
        String value = EstatioInteractionCache.execute(new Callable<String>(){

            @Override
            public String call() throws Exception {
                return "foo";
            }
            
        }, EstatioInteractionCacheTest.class, "cachingWhenNoCache");
        
        assertThat(value, is("foo"));
    }

    @Test
    public void cachingWhenCacheAndNoKey() {
        
        final int[] i = new int[]{0};
        
        try {
            EstatioInteractionCache.startInteraction();
            Callable<String> callable = new Callable<String>(){
                
                @Override
                public String call() throws Exception {
                    i[0]++;
                    return "foo";
                }
                
            };
            assertThat(i[0], is(0));
            assertThat(EstatioInteractionCache.execute(callable, EstatioInteractionCacheTest.class, "cachingWhenCacheAndNoKey", "a"), is("foo"));
            assertThat(i[0], is(1));

            // should be a cache hit
            assertThat(EstatioInteractionCache.execute(callable, EstatioInteractionCacheTest.class, "cachingWhenCacheAndNoKey", "a"), is("foo"));
            assertThat(i[0], is(1));
            
            // changing any of the keys rsults in a cache miss
            assertThat(EstatioInteractionCache.execute(callable, EstatioInteractionCacheTest.class, "XXcachingWhenCacheAndNoKey", "a"), is("foo"));
            assertThat(i[0], is(2));
            assertThat(EstatioInteractionCache.execute(callable, EstatioInteractionCache.class, "cachingWhenCacheAndNoKey", "a"), is("foo"));
            assertThat(i[0], is(3));
            assertThat(EstatioInteractionCache.execute(callable, EstatioInteractionCacheTest.class, "cachingWhenCacheAndNoKey", "b"), is("foo"));
            assertThat(i[0], is(4));
            assertThat(EstatioInteractionCache.execute(callable, EstatioInteractionCacheTest.class, "cachingWhenCacheAndNoKey", "a", "x"), is("foo"));
            assertThat(i[0], is(5));
        } finally {
            EstatioInteractionCache.endInteraction();
        }
    }
    
}
