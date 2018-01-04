package org.estatio.module.settings.dom;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.security.UserMemento;

public interface UserSetting extends Setting {

    String getKey();
    
    /**
     * Typically as obtained from the {@link UserMemento#getName() UserMemento} class
     * (accessible in turn from the {@link DomainObjectContainer#getUser() DomainObjectContainer}).
     */
    String getUser();
    
}
