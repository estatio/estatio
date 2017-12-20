package org.incode.module.document.dom.spi;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.services.ClassNameViewModel;

public interface RendererModelFactoryClassNameService {

    @Programmatic
    public List<ClassNameViewModel> rendererModelFactoryClassNames();

}
