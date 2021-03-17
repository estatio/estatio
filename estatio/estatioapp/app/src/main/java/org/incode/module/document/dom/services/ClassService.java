package org.incode.module.document.dom.services;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.i18n.TranslatableString;

@DomainService(nature = NatureOfService.DOMAIN)
public class ClassService {

    public String getId() {
        return "incodeDocuments.ClassService";
    }

    @Programmatic
    public TranslatableString validateClassHasAccessibleNoArgConstructor(final String fqcn) {
        return validateClassHasAccessibleNoArgConstructor(fqcn, null);
    }

    @Programmatic
    public TranslatableString validateClassHasAccessibleNoArgConstructor(final String fqcn, final Class<?> superType) {
        final Class<?> cls;
        try {
            cls = loadClass(fqcn);
        } catch (ClassNotFoundException e) {
            return TranslatableString.tr("No such class");
        }
        if(superType != null) {
            if(!superType.isAssignableFrom(cls)) {
                return TranslatableString.tr(
                        "Class is not a subtype of '{superType}'",
                        "superType", superType.getName());
            }
        }
        final Constructor<?> constructor;
        try {
            constructor = cls.getConstructor();
        } catch (NoSuchMethodException e) {
            return TranslatableString.tr(
                    "Class does not have a no-arg constructor");
        }
        if(!Modifier.isPublic(constructor.getModifiers())) {
            return TranslatableString.tr(
                    "The no-arg constructor is not public");

        }
        return null;
    }

    @Programmatic
    public Object instantiate(final String fqcn) {
        final Class<?> cls = load(fqcn);
        return instantiate(cls);
    }

    @Programmatic
    public Object instantiate(final Class<?> cls) {
        try {
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ApplicationException(e);
        }
    }

    @Programmatic
    public Class<?> load(final String fqcn) {
        try {
            return loadClass(fqcn);
        } catch (ClassNotFoundException e) {
            throw new ApplicationException(e);
        }
    }

    private static Class<?> loadClass(final String fqcn) throws ClassNotFoundException {
        return Thread.currentThread().getContextClassLoader().loadClass(fqcn);
    }

}
