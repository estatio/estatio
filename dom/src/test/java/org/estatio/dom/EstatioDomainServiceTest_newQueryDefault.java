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
