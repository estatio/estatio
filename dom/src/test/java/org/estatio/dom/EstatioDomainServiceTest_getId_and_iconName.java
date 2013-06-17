package org.estatio.dom;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class EstatioDomainServiceTest_getId_and_iconName {

    static class SomeDomainObject extends EstatioDomainObject {
    }

    static class SomeDomainService extends EstatioDomainService {
        protected SomeDomainService() {
            super(SomeDomainService.class, SomeDomainObject.class);
        }
    }

    static class SomeDomainServiceImpl extends SomeDomainService {
    }

    @Test
    public void test() {
        final SomeDomainService someDomainService = new SomeDomainService();
        
        assertThat(someDomainService.getId(), is("someDomainService"));
        assertThat(someDomainService.iconName(), is("SomeDomainObject"));
    }

    @Test
    public void testImpl() {
        final SomeDomainService someDomainService = new SomeDomainServiceImpl();
        
        assertThat(someDomainService.getId(), is("someDomainService"));
        assertThat(someDomainService.iconName(), is("SomeDomainObject"));
    }


}
