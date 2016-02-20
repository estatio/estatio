package org.estatio.app.services.properties;

import java.util.Set;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(menuBar = DomainServiceLayout.MenuBar.SECONDARY, named = "Administration")
public class ApplicationPropertyMenu {

    @CollectionLayout(paged = 999)
    public Set<ApplicationProperty> allApplicationProperties(){
        return applicationPropertyService.allApplicationProperties();
    }

    @Inject
    private ApplicationPropertyService applicationPropertyService;
}
