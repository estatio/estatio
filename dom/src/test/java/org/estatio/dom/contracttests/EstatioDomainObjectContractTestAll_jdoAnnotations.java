/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.Set;

import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Version;

import org.junit.Test;
import org.reflections.Reflections;

import org.apache.isis.applib.annotation.Immutable;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.TitledEnum;


/**
 * Automatically tests all enums implementing {@link TitledEnum}.
 */
public class EstatioDomainObjectContractTestAll_jdoAnnotations {

    @SuppressWarnings("rawtypes")
    @Test
    public void searchAndTest() {
        Reflections reflections = new Reflections("org.estatio.dom");

        System.out.println("EstatioDomainObjectContractTestAll_jdoAnnotations");

        Set<Class<? extends EstatioDomainObject>> subtypes = 
                reflections.getSubTypesOf(EstatioDomainObject.class);
        for (Class<? extends EstatioDomainObject> subtype : subtypes) {
            if(subtype.isAnonymousClass() || subtype.isLocalClass() || subtype.isMemberClass() || subtype.getName().endsWith("ForTesting")) {
                // skip (probably a testing class)
                continue;
            }
            if(EstatioRefDataObject.class == subtype || EstatioTransactionalObject.class == subtype) {
                // skip
                continue;
            }
            
            System.out.println(">>> " + subtype.getName());

            // must have a @PersistenceCapable(identityType=...) annotation
            final PersistenceCapable persistenceCapable = subtype.getAnnotation(PersistenceCapable.class);
            assertThat("Class " + subtype.getName() + " inherits from EstatioDomainObject "
                    + "but is not annotated with @PersistenceCapable", 
                    persistenceCapable, is(not(nullValue())));
            IdentityType identityType = persistenceCapable.identityType();
            assertThat("Class " + subtype.getName() + " @PersistenceCapable annotation "
                    + "does not specify the identityType", identityType, is(not(nullValue())));

            if(identityType == IdentityType.DATASTORE) {
                // must have a @DatastoreIdentity(..., column="id")
                final DatastoreIdentity datastoreIdentity = subtype.getAnnotation(DatastoreIdentity.class);
                assertThat("Class " + subtype.getName() + " @PersistenceCapable annotation specifies identityType=\"DATASTORE\" but there is no @DatastoreIdentity annotation", 
                        datastoreIdentity, is(not(nullValue())));
                
                assertThat("Class " + subtype.getName() + " @DataStoreIdentity annotation does not specify column=\"id\"", 
                        datastoreIdentity.column(), is("id"));
            }

            // must have a @Discriminator(..., column="discriminator")
            final Discriminator discriminator = subtype.getAnnotation(Discriminator.class);
            assertThat("Class " + subtype.getName() + " inherits from EstatioDomainObject "
                    + "but is not annotated with @Discriminator", 
                    discriminator, is(not(nullValue())));
            
            assertThat("Class " + subtype.getName() + " @Discriminator annotation does not specify column=\"discriminator\"", 
                    discriminator.column(), is("discriminator"));


            if(EstatioRefDataObject.class.isAssignableFrom(subtype)) {
                // must have @Immutable
                final Immutable version = subtype.getAnnotation(Immutable.class);
                assertThat("Class " + subtype.getName() + " inherits from EstatioRefDataObject "
                        + "but is not annotated with @Immutable", 
                        version, is(not(nullValue())));
            }
            
            if(EstatioTransactionalObject.class.isAssignableFrom(subtype)) {
                // must have a @Version(..., column="version")
                final Version version = subtype.getAnnotation(Version.class);
                assertThat("Class " + subtype.getName() + " inherits from EstatioTransactionalObject "
                        + "but is not annotated with @Version", 
                        version, is(not(nullValue())));

                assertThat("Class " + subtype.getName() + " @Version annotation does not specify have column=\"version\"", 
                        version.column(), is("version"));
            }

        
        }
    }

}
