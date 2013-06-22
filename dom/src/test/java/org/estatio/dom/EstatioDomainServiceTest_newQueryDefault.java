package org.estatio.dom;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.query.QueryDefault;

public class EstatioDomainServiceTest_newQueryDefault {

    private SomeDomainService someDomainService;
    static class SomeDomainObject extends EstatioDomainObject<SomeDomainObject> {
        public SomeDomainObject() {
            super(null);
        }
    }

    static class SomeDomainService extends EstatioDomainService<SomeDomainObject> {
        Class<?> entityType;
        
        protected SomeDomainService() {
            super(SomeDomainService.class, SomeDomainObject.class);
        }
    }

    static class SomeDomainServiceImpl extends SomeDomainService {
    }

    
    @Before
    public void setUp() throws Exception {
        someDomainService = new SomeDomainService();
    }
    
    @Test
    public void test() {
        final QueryDefault<SomeDomainObject> query = someDomainService.newQueryDefault("foo", "bar", 1);
        assertEquals(SomeDomainObject.class, query.getResultType());
        assertThat(query.getQueryName(), is("foo"));
        assertThat(query.getArgumentsByParameterName().size(), is(1));
        assertThat(query.getArgumentsByParameterName().containsKey("bar"), is(true));
        assertThat(query.getArgumentsByParameterName().get("bar"), is((Object)1));
    }
    

}
