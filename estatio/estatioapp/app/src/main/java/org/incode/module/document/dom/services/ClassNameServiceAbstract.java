package org.incode.module.document.dom.services;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.classdiscovery.ClassDiscoveryService2;

import org.incode.module.document.dom.spi.AttachmentAdvisorClassNameService;
import org.incode.module.document.dom.spi.RendererClassNameService;

/**
 * Provided as a convenience absdtract superclass for implementing the
 * {@link org.incode.module.document.dom.spi.RendererModelFactoryClassNameService}, the
 * {@link AttachmentAdvisorClassNameService} and the {@link RendererClassNameService} SPIs.
 */
public abstract class ClassNameServiceAbstract<C> {

    private final Class<C> cls;
    private final String packagePrefix;

    protected ClassNameServiceAbstract(final Class<C> cls, final String packagePrefix) {
        this.cls = cls;
        this.packagePrefix = packagePrefix;
    }


    @Programmatic
    protected Class<C> asClass(final String className) {
        return (Class<C>) classService.load(className);
    }


    // cached
    private List<Class<? extends C>> cachedClasses;

    @Programmatic
    public List<ClassNameViewModel> classNames() {
        return classNames(x -> true);
    }

    @Programmatic
    public List<ClassNameViewModel> classNames(Predicate<Class<? extends C>> predicate) {
        if(cachedClasses == null) {
            final Set<Class<? extends C>> rendererClasses = classDiscoveryService2
                    .findSubTypesOfClasses(cls, packagePrefix);

            cachedClasses = rendererClasses.stream()
                    .filter(x -> !Modifier.isAbstract(x.getModifiers()))
                    .collect(Collectors.toList());

        }
        return Lists.newArrayList(
                cachedClasses.stream()
                        .filter(predicate)
                        .map(x -> new ClassNameViewModel(x) )
                        .collect(Collectors.toList()));
    }

    @Inject
    private ClassDiscoveryService2 classDiscoveryService2;

    @Inject
    private ClassService classService;


}
