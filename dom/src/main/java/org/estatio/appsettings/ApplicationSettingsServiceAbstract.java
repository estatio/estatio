package org.estatio.appsettings;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.filter.Filter;

/**
 * A convenience service to be subclassed from, allowing an
 * application to find their (one and only) 'Setting' domain object.
 */
@Hidden
public abstract class ApplicationSettingsServiceAbstract<T> {
    
    private Class<T> settingClass;

    protected ApplicationSettingsServiceAbstract(Class<T> settingCls) {
        this.settingClass = settingCls;
    }

    public T fetchSetting() {
        T setting = container.firstMatch(settingClass, new Filter<T>(){
            @Override
            public boolean accept(T t) {
                return true; // one and only
            }
        });
        if(setting==null) {
            setting = container.newPersistentInstance(settingClass);
        }
        return setting;
    }

    // {{ injected: DomainObjectContainer
    private DomainObjectContainer container;

    public void setDomainObjectContainer(final DomainObjectContainer container) {
        this.container = container;
    }
    // }}

}
