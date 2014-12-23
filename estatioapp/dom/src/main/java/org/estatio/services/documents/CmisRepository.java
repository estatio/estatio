/*
 *  Copyright 2013~2014 Dan Haywood
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
package org.estatio.services.documents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

import org.apache.isis.applib.annotation.Programmatic;

public class CmisRepository {

    private Map<String, String> properties;
    private SessionFactory sessionFactory;
    private Repository repository;
    
    @Programmatic
    @PostConstruct
    public void init(final Map<String,String> properties) {
        this.properties = properties;

        // TODO: set up in isis.properties
        properties.put(SessionParameter.ATOMPUB_URL, "http://cmis.demo.nuxeo.org/nuxeo/atom/cmis");
        properties.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        properties.put(SessionParameter.USER, "Administrator");
        properties.put(SessionParameter.PASSWORD, "Administrator");

        sessionFactory = SessionFactoryImpl.newInstance();

        // find all the repositories at this URL - there should only be one.
        List<Repository> repositories = new ArrayList<Repository>();
        repositories = sessionFactory.getRepositories(properties);

        repository = repositories.get(0);
        properties.put(SessionParameter.REPOSITORY_ID, repository.getId());
        
        ping();
    }

    // fail-fast if the properties are wrong etc etc
    private void ping() {
        Session session = sessionFactory.createSession(properties);

        @SuppressWarnings("unused")
        Folder root = session.getRootFolder();
    }

    
    /**
     * @param queryString eg: "SELECT * FROM cmis:document where cmis:name like '%Adventure%'"
     */
    @Programmatic
    public ItemIterable<QueryResult> query(String queryString) {

        final Session session = sessionFactory.createSession(properties);
        return session.query(queryString, true);
    }

    @Programmatic
    public CmisObject findById(String cmisId) {
        Session session = sessionFactory.createSession(properties);
        return session.getObject(cmisId);
    }
}


