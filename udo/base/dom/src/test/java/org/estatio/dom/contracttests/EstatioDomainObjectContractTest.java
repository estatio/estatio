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
import org.junit.Assert;
import org.junit.Test;
import org.reflections.Reflections;
import org.estatio.dom.TitledEnum;
import org.estatio.dom.UdoDomainObject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Automatically tests all enums implementing {@link TitledEnum}.
 */
public class EstatioDomainObjectContractTest {

    public static class JdoAnnotations extends EstatioDomainObjectContractTest {

        @SuppressWarnings("rawtypes")
        @Test
        public void searchAndTest() {
            final Reflections reflections = new Reflections("org.estatio.dom");

            final Set<Class<? extends UdoDomainObject>> subtypes =
                    reflections.getSubTypesOf(UdoDomainObject.class);
            for (final Class<? extends UdoDomainObject> subtype : subtypes) {
                if (subtype.isAnonymousClass() || subtype.isLocalClass() || subtype.isMemberClass() || subtype.getName().endsWith("ForTesting")) {
                    // skip (probably a testing class)
                    continue;
                }
                if (UdoDomainObject.class == subtype) {
                    // skip
                    continue;
                }

                // TODO: nasty: special case, but really need some formalized way for UDO to know that this particular subclass is ok to not be annotated as @PersistenceCapable
                if (subtype.getName().equals("org.estatio.dom.EstatioDomainObject")) {
                    // skip
                    continue;
                }

                System.out.println(">>> " + subtype.getName());

                // must have a @PersistenceCapable(identityType=...) annotation
                final PersistenceCapable persistenceCapable = subtype.getAnnotation(PersistenceCapable.class);
                assertThat("Class " + subtype.getName() + " inherits from UdoDomainObject "
                                + "but is not annotated with @PersistenceCapable",
                        persistenceCapable, is(not(nullValue())));
                final IdentityType identityType = persistenceCapable.identityType();
                assertThat("Class " + subtype.getName() + " @PersistenceCapable annotation "
                        + "does not specify the identityType", identityType, is(not(nullValue())));

                if (identityType == IdentityType.DATASTORE) {
                    // NOT mandatory to have a @DatastoreIdentity, but if does, then @DatastoreIdentity(..., column="id")
                    final DatastoreIdentity datastoreIdentity = subtype.getAnnotation(DatastoreIdentity.class);
                    if(datastoreIdentity != null) {
                        assertThat("Class " + subtype.getName() + " @DataStoreIdentity annotation does not specify column=\"id\"",
                                datastoreIdentity.column(), is("id"));
                    }
                }

                final Inheritance inheritance = subtype.getAnnotation(Inheritance.class);

                if (inheritance != null && inheritance.strategy() == InheritanceStrategy.SUPERCLASS_TABLE) {
                    // must NOT have a @Discriminator(..., column="discriminator")
                    final Annotation[] declaredAnnotations = subtype.getDeclaredAnnotations();
                    for (final Annotation declaredAnnotation : declaredAnnotations) {
                        if(declaredAnnotation.annotationType() == Discriminator.class) {
                            Assert.fail("Class " + subtype.getName() + " inherits from " + subtype.getSuperclass().getName()
                                    + "and has (incorrectly) been annotated with @Discriminator");
                        }
                    }

                    // check if supertype has discriminator

                    // must have a @Discriminator(..., column="discriminator") on one of its supertypes
                    final Discriminator superDiscriminator = subtype.getSuperclass().getAnnotation(Discriminator.class);
                    assertThat("Class " + subtype.getSuperclass().getName() + " is inherited by "+ subtype.getName()
                                    + "but is not annotated with @Discriminator",
                            superDiscriminator, is(not(nullValue())));

                    assertThat("Class " + subtype.getName() + " @Discriminator annotation does not specify column=\"discriminator\"",
                            superDiscriminator.column(), is("discriminator"));

                }

                if (subtype.getSuperclass().equals(UdoDomainObject.class)) {
                    // must have a @Version(..., column="version")
                    final Version version = getAnnotationOfTypeOfItsSupertypes(subtype, Version.class);

                    assertThat("Class " + subtype.getName() + " inherits from EstatioMutableObject "
                                    + "but is not annotated with @Version",
                            version, is(not(nullValue())));

                    assertThat("Class " + subtype.getName() + " @Version annotation does not specify have column=\"version\"",
                            version.column(), is("version"));
                }

            }
        }

        private static <T extends Annotation> T getAnnotationOfTypeOfItsSupertypes (Class<?> cls, final Class<T> annotationClass) {
            while(cls != null) {
                final T annotation = cls.getAnnotation(annotationClass);
                if(annotation!=null) {
                    return annotation;
                }
                cls = cls.getSuperclass();
            }
            return null;
        }

    }

}
