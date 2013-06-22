package org.estatio.dom;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EstatioDomainServiceTest_getId_and_iconName {

    private SomeDomainService someDomainService;

    static class SomeDomainObject extends EstatioDomainObject<SomeDomainObject> {
        public SomeDomainObject() {
            super(null);
        }
    }

    static class SomeDomainService extends EstatioDomainService<SomeDomainObject> {
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
    public void getId() {
        assertThat(someDomainService.getId(), is("someDomainService"));
    }

    @Test
    public void iconName() {
        assertThat(someDomainService.iconName(), is("SomeDomainObject"));
    }

    @Test
    public void getServiceType() {
        assertEquals(SomeDomainService.class, someDomainService.getServiceType());
    }
    

}
