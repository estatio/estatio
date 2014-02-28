/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.services.links;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.concurrent.Callable;

import com.google.common.collect.Lists;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

public class LinksTest_findAll {

    static class Animal {}
    
    static class Mammal extends Animal {}
    
    static class Lion extends Mammal {}
    
    
    @Mock
    private DomainObjectContainer mockContainer;
    
    private QueryResultsCache queryResultsCache;
    private Links links;

    @Before
    public void setUp() throws Exception {
        queryResultsCache = new QueryResultsCache() {
            @Override
            public <T> T execute(Callable<T> callable, Key cacheKey)  {
                try {
                    return callable.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        
        links = new Links() {
            @Override
            public List<Link> findByClassName(String className) {
                if(Lion.class.getName().equals(className)) {
                    return Lists.newArrayList(new Link(), new Link(), new Link());
                }
                if(Mammal.class.getName().equals(className)) {
                    return Lists.newArrayList();
                }
                if(Animal.class.getName().equals(className)) {
                    return Lists.newArrayList(new Link(), new Link());
                }
                return Lists.newArrayList();
            }
        };
        links.queryResultsCache = queryResultsCache;
    }
    
    @Test
    public void size() throws Exception {
        assertThat(links.findAllForClassHierarchy(Animal.class).size(), is(2));
        assertThat(links.findAllForClassHierarchy(Mammal.class).size(), is(2));
        assertThat(links.findAllForClassHierarchy(Lion.class).size(), is(5));
    }
    
}
