/*
 *  Copyright 2016 Dan Haywood
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
package org.incode.module.documents.dom.services;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.i18n.TranslatableString;

@DomainService(nature = NatureOfService.DOMAIN)
public class ClassService {

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
