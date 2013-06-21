package org.estatio.dom;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.core.commons.lang.StringUtils;

public abstract class EstatioDomainService<T> extends AbstractFactoryAndRepository {

    private final Class<? extends EstatioDomainService<T>> serviceType;
    private final Class<T> entityType;

    protected EstatioDomainService(Class<? extends EstatioDomainService<T>> serviceType, Class<T> objectType) {
        this.serviceType = serviceType;
        this.entityType = objectType;
    }
    
    @Override
    public String getId() {
        // eg "agreementRoles";
        return StringUtils.camelLowerFirst(serviceType.getSimpleName());
    }

    public String iconName() {
        // eg "AgreementRole";
        return entityType.getSimpleName();
    }

    // //////////////////////////////////////

    protected Class<? extends EstatioDomainService<T>> getServiceType() {
        return serviceType;
    }
    
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
    
    protected List<T> allMatches(final String queryName, final Object... paramArgs) {
        return allMatches(newQueryDefault(queryName, paramArgs));
    }

    protected List<T> allInstances() {
        return allInstances(getEntityType());
    }

}
