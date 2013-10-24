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
package org.estatio.dom;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.Identifier.Type;

public class EstatioTransactionalObjectTest_disabled {

    public static class SomeTransactionalObject extends EstatioMutableAndLockableObject<SomeTransactionalObject, Status>  {
        public SomeTransactionalObject() {
            super("status", Status.UNLOCKED, Status.LOCKED);
        }

        private Status status;
        
        @Override
        public Status getLockable() {
            return status;
        }

        @Override
        public void setLockable(final Status lockable) {
            this.status = lockable;
        }

    }
    
    private SomeTransactionalObject someTransactionalObject;
    
    @Before
    public void setUp() throws Exception {
        someTransactionalObject = new SomeTransactionalObject();
    }
    
    @Test
    public void whenLocked_propertyOrIdentifier() {
        
        // given
        assertThat(someTransactionalObject.isLocked(), is(true));

        // then
        assertThat(someTransactionalObject.disabled(Type.PROPERTY_OR_COLLECTION), is("Cannot modify when locked"));
    }
    
    @Test
    public void whenLocked_other() {
        
        // given
        assertThat(someTransactionalObject.isLocked(), is(true));
        
        // then
        assertThat(someTransactionalObject.disabled(Type.ACTION), is(nullValue()));
        assertThat(someTransactionalObject.disabled(Type.CLASS), is(nullValue()));
    }


    @Test
    public void whenUnlocked_any() {
        
        // given
        someTransactionalObject.unlock();
        assertThat(someTransactionalObject.isLocked(), is(false));
        
        // then
        assertThat(someTransactionalObject.disabled(Type.PROPERTY_OR_COLLECTION), is(nullValue()));
        assertThat(someTransactionalObject.disabled(Type.ACTION), is(nullValue()));
        assertThat(someTransactionalObject.disabled(Type.CLASS), is(nullValue()));
    }
    
    
}
