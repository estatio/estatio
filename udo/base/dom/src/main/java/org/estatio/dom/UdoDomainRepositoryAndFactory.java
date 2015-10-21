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
package org.estatio.dom;

import java.util.List;
import javax.jdo.Query;
import org.apache.isis.applib.RepositoryException;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

public abstract class UdoDomainRepositoryAndFactory<T> extends UdoDomainService<T> {

    private final Class<T> entityType;

    protected UdoDomainRepositoryAndFactory(
            final Class<? extends UdoDomainRepositoryAndFactory<T>> serviceType,
            final Class<T> entityType) {
        super(serviceType);
        this.entityType = entityType;
    }

    @Override
    public String iconName() {
        // eg "AgreementRole";
        return entityType.getSimpleName();
    }

    // //////////////////////////////////////

    protected Class<T> getEntityType() {
        return entityType;
    }
    
    protected QueryDefault<T> newQueryDefault(final String queryName, final Object... paramArgs) {
        return new QueryDefault<T>(getEntityType(), queryName, paramArgs);
    }
    
    // //////////////////////////////////////

    protected T newTransientInstance() {
        return newTransientInstance(getEntityType());
    }
    
    protected T firstMatch(final String queryName, final Object... paramArgs) {
        return firstMatch(newQueryDefault(queryName, paramArgs));
    }
    
    protected T uniqueMatch(final String queryName, final Object... paramArgs) {
        return uniqueMatch(newQueryDefault(queryName, paramArgs));
    }
    
    protected List<T> allMatches(final String queryName, final Object... paramArgs) {
        return allMatches(newQueryDefault(queryName, paramArgs));
    }

    protected List<T> allInstances() {
        return allInstances(getEntityType());
    }

    // //////////////////////////////////////
    
    protected Query newQuery(final String jdoql) {
        return isisJdoSupport.getJdoPersistenceManager().newQuery(jdoql);
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    IsisJdoSupport isisJdoSupport;

    protected IsisJdoSupport getIsisJdoSupport() {
        return isisJdoSupport;
    }

}
