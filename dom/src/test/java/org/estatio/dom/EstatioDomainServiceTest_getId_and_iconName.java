/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
