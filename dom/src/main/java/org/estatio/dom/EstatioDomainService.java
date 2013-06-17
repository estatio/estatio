package org.estatio.dom;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.core.commons.lang.StringUtils;

public abstract class EstatioDomainService extends AbstractFactoryAndRepository {

    private final Class<? extends EstatioDomainService> serviceType;
    private final Class<?> entityType;

    protected EstatioDomainService(Class<? extends EstatioDomainService> serviceType, Class<?> objectType) {
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

    
}
