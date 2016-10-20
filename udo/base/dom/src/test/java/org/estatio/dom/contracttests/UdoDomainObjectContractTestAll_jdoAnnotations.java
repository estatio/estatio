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
package org.estatio.dom.contracttests;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Version;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.reflections.Reflections;

import org.incode.module.base.dom.TitledEnum;
import org.estatio.dom.UdoDomainObject;
import org.estatio.dom.UdoDomainObject2;

/**
 * Automatically tests all enums implementing {@link TitledEnum}.
 */
public class UdoDomainObjectContractTestAll_jdoAnnotations {

    @SuppressWarnings("rawtypes")
    @Test
    public void searchAndTest() {
        Reflections reflections = new Reflections("org.estatio.dom");

        Set<Class<? extends UdoDomainObject>> subtypes =
                reflections.getSubTypesOf(UdoDomainObject.class);
        for (Class<? extends UdoDomainObject> subtype : subtypes) {
            if (subtype.isAnonymousClass() || subtype.isLocalClass() || subtype.isMemberClass() || subtype.getName().endsWith("ForTesting")) {
                // skip (probably a testing class)
                continue;
            }
            if (UdoDomainObject.class == subtype || UdoDomainObject2.class == subtype) {
                // skip
                continue;
            }

            System.out.println(">>> " + subtype.getName());

            // must have a @PersistenceCapable(identityType=...) annotation
            final PersistenceCapable persistenceCapable = subtype.getAnnotation(PersistenceCapable.class);
            Assertions.assertThat(persistenceCapable).isNotNull();

            IdentityType identityType = persistenceCapable.identityType();
            Assertions.assertThat(identityType).isNotNull();

            if (identityType == IdentityType.DATASTORE) {
                // NOT mandatory to have a @DatastoreIdentity, but if does, then @DatastoreIdentity(..., column="id")
                final DatastoreIdentity datastoreIdentity = subtype.getAnnotation(DatastoreIdentity.class);
                if (datastoreIdentity != null) {
                    Assertions.assertThat(datastoreIdentity.column()).isEqualTo("id");
                }
            }

            Inheritance inheritance = subtype.getAnnotation(Inheritance.class);

            if (inheritance != null && inheritance.strategy() == InheritanceStrategy.SUPERCLASS_TABLE) {
                // must NOT have a @Discriminator(..., column="discriminator")
                final Annotation[] declaredAnnotations = subtype.getDeclaredAnnotations();
                for (Annotation declaredAnnotation : declaredAnnotations) {
                    if (declaredAnnotation.annotationType() == Discriminator.class) {
                        Assert.fail("Class " + subtype.getName() + " inherits from " + subtype.getSuperclass().getName()
                                + "and has (incorrectly) been annotated with @Discriminator");
                    }
                }

                // check if supertype has discriminator

                // must have a @Discriminator(..., column="discriminator") on one of its supertypes
                final Discriminator superDiscriminator = subtype.getSuperclass().getAnnotation(Discriminator.class);
                Assertions.assertThat(superDiscriminator).isNotNull();
                Assertions.assertThat(superDiscriminator.column()).isEqualTo("discriminator");

            }

            if (subtype.getSuperclass().equals(UdoDomainObject.class)) {
                // must have a @Version(..., column="version")
                final Version version = getAnnotationOfTypeOfItsSupertypes(subtype, Version.class);

                Assertions.assertThat(version).isNotNull();
                Assertions.assertThat(version.column()).isEqualTo("version");
            }

        }
    }

    private static <T extends Annotation> T getAnnotationOfTypeOfItsSupertypes(Class<?> cls, final Class<T> annotationClass) {
        while (cls != null) {
            T annotation = cls.getAnnotation(annotationClass);
            if (annotation != null) {
                return annotation;
            }
            cls = cls.getSuperclass();
        }
        return null;
    }

}
