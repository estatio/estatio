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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.query.QueryDefault;

public class EstatioDomainServiceTest_allInstances_etc {

    private SomeDomainService someDomainService;
    static class SomeDomainObject extends EstatioDomainObject<SomeDomainObject> {
        public SomeDomainObject() {
            super(null);
        }
    }

    static class SomeDomainService extends EstatioDomainService<SomeDomainObject> {
        Class<?> entityType;
        
        private String queryName;
        private Object[] paramArgs;
        private QueryDefault<SomeDomainObject> queryToReturn;
        
        private Query<?> query;

        protected SomeDomainService() {
            super(SomeDomainService.class, SomeDomainObject.class);
        }

        @Override
        @Hidden
        protected <T> T newTransientInstance(Class<T> ofType) {
            entityType = ofType;
            return null;
        }
        
        @Override
        protected <T> List<T> allInstances(Class<T> ofType, long... range) {
            this.entityType = ofType;
            return null;
        }
        
        @Override
        protected <T> List<T> allMatches(Query<T> query) {
            this.query = query;
            this.entityType = query.getResultType();
            return null;
        }

        @Override
        protected <T> T firstMatch(Query<T> query) {
            this.query = query;
            this.entityType = query.getResultType();
            return null;
        }
        
        @Override
        protected QueryDefault<SomeDomainObject> newQueryDefault(String queryName, Object... paramArgs) {
            this.queryName = queryName;
            this.paramArgs = paramArgs;
            return queryToReturn;
        }
    }

    static class SomeDomainServiceImpl extends SomeDomainService {
    }

    
    @Before
    public void setUp() throws Exception {
        someDomainService = new SomeDomainService();
    }
    
    @Test
    public void allInstances() {
        someDomainService.allInstances();
        assertEquals(SomeDomainObject.class, someDomainService.entityType);
    }
    
    @Test
    public void allMatches() {
        
        final String queryName = "foo";
        final QueryDefault<SomeDomainObject> query = new QueryDefault<SomeDomainObject>(SomeDomainObject.class, queryName, "bar", 1);
        
        someDomainService.queryToReturn = query;
        
        someDomainService.allMatches(queryName, "bar", 1);
        assertEquals(SomeDomainObject.class, someDomainService.entityType);
        assertEquals(queryName, someDomainService.queryName);
        assertEquals("bar", someDomainService.paramArgs[0]);
        assertEquals(1, someDomainService.paramArgs[1]);
        assertEquals(query, someDomainService.query);
    }
    
    @Test
    public void firstMatch() {
        
        final String queryName = "foo";
        final QueryDefault<SomeDomainObject> query = new QueryDefault<SomeDomainObject>(SomeDomainObject.class, queryName, "bar", 1);
        
        someDomainService.queryToReturn = query;
        
        someDomainService.firstMatch(queryName, "bar", 1);
        assertEquals(SomeDomainObject.class, someDomainService.entityType);
        assertEquals(queryName, someDomainService.queryName);
        assertEquals("bar", someDomainService.paramArgs[0]);
        assertEquals(1, someDomainService.paramArgs[1]);
        assertEquals(query, someDomainService.query);
    }

    @Test
    public void newTransientInstance() {
        someDomainService.newTransientInstance();
        assertEquals(SomeDomainObject.class, someDomainService.entityType);
    }

}
