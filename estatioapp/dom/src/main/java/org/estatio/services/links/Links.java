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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.estatio.dom.EstatioDomainService;

@DomainService(menuOrder = "99", repositoryFor = Link.class)
@Hidden
public class Links extends EstatioDomainService<Link> {
    
    /**
     * Cache link count (across sessions), so can quickly know whether any given class has any links for it.
     * 
     * <p>
     * The count held here is for the class plus all its superclasses in the hierarchy.
     * 
     * <p>
     * Note that we cannot (safely) cache the reports themselves between sessions, however
     * (would require detaching and reattaching from underlying JDO session, and issues of threadsafety).
     * So we make do with caching of reports themselves only within a sesssion, using {@link QueryResultsCache}.
     */
    private Map<Class<?>, Integer> linksByClass = Maps.newHashMap();
    
    public Links() {
        super(Links.class, Link.class);
    }

    @Programmatic
    public List<Link> findAllForClassHierarchy(final Object domainObject) {
        if(domainObject == null) {
            return Collections.emptyList();
        }
        return findAllForClassHierarchy(domainObject.getClass());
    }

    @Programmatic
    public List<Link> findAllForClassHierarchy(final Class<?> cls) {
        return queryResultsCache.execute(
                new Callable<List<Link>>(){
                    @Override
                    public List<Link> call() throws Exception {

                        // do we already know that there are no links for this class?
                        Integer numLinks = linksByClass.get(cls);
                        if(numLinks != null && numLinks == 0) {
                            return Collections.emptyList();
                        }
                        
                        // combine the links for this class and those of its superclass
                        // (calling recursively)
                        final List<Link> links = Lists.newArrayList();

                        // ... find the superclass' links
                        // (taking into account might be at top of the class hierarchy)
                        List<Link> linksForSuperClass = 
                                cls != Object.class 
                                    ? findAllForClassHierarchy(cls.getSuperclass()) 
                                    : Collections.<Link>emptyList();
                        links.addAll(linksForSuperClass);

                        // ... and now the class itself
                        List<Link> linksForClass = findByClassName(cls.getName());
                        links.addAll(linksForClass);

                        // cache for next time
                        linksByClass.put(cls, links.size());

                        return links;
                    }
                }, Links.class, "findAllForClassHierarchy", cls);
    }
    
    public List<Link> findByClassName(final String className) {
        return allMatches(
                new QueryDefault<Link>(Link.class, 
                        "findByClassName", 
                        "className", className));
    }

    
    // //////////////////////////////////////

    @Programmatic
    public Link newLink(Class<?> cls, String name, String urlTemplate) {
        return newLink(cls.getName(), name, urlTemplate);
    }
    private Link newLink(String className, String name, String urlTemplate) {
        Link link = newTransientInstance();
        link.setName(name);
        link.setUrlTemplate(urlTemplate);
        link.setClassName(className);
        persist(link);
        return link;
    }

    
    // //////////////////////////////////////

    @javax.inject.Inject
    QueryResultsCache queryResultsCache;


}
