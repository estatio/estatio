/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
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

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.classdiscovery.ClassDiscoveryService2;

public abstract class ClassNameServiceAbstract<C> {

    private final Class<C> cls;
    private final String packagePrefix;

    protected ClassNameServiceAbstract(final Class<C> cls, final String packagePrefix) {
        this.cls = cls;
        this.packagePrefix = packagePrefix;
    }


    @Programmatic
    public Class<C> asClass(final String className) {
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
