package org.incode.module.docfragment.dom.spi;

import org.apache.isis.applib.annotation.Programmatic;

public interface ApplicationTenancyService {

    @Programmatic
    String atPathFor(final Object domainObjectToRender);


}