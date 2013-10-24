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

public class EstatioMutableAndLockableObjectTest_disabled {

    public static enum SomeMutableObjectStatus implements Lockable {
        UNLOCKED,
        LOCKED;

        @Override
        public boolean isUnlocked() {
            return this == UNLOCKED;
        }
    }

    public static class SomeMutableAndLockableObject extends EstatioMutableAndLockableObject<SomeMutableAndLockableObject, SomeMutableObjectStatus>  {
        
        public SomeMutableAndLockableObject() {
            super("status", SomeMutableObjectStatus.UNLOCKED, SomeMutableObjectStatus.LOCKED);
        }

        private SomeMutableObjectStatus status;
        
        @Override
        public SomeMutableObjectStatus getLockable() {
            return status;
        }

        @Override
        public void setLockable(final SomeMutableObjectStatus lockable) {
            this.status = lockable;
        }
    }
    
    private SomeMutableAndLockableObject someObject;
    
    @Before
    public void setUp() throws Exception {
        someObject = new SomeMutableAndLockableObject();
    }
    
    @Test
    public void whenLocked_propertyOrIdentifier() {
        
        // given
        assertThat(someObject.isLocked(), is(true));

        // then
        assertThat(someObject.disabled(Type.PROPERTY_OR_COLLECTION), is("Cannot modify when locked"));
    }
    
    @Test
    public void whenLocked_other() {
        
        // given
        assertThat(someObject.isLocked(), is(true));
        
        // then
        assertThat(someObject.disabled(Type.ACTION), is(nullValue()));
        assertThat(someObject.disabled(Type.CLASS), is(nullValue()));
    }


    @Test
    public void whenUnlocked_any() {
        
        // given
        someObject.unlock();
        assertThat(someObject.isLocked(), is(false));
        
        // then
        assertThat(someObject.disabled(Type.PROPERTY_OR_COLLECTION), is(nullValue()));
        assertThat(someObject.disabled(Type.ACTION), is(nullValue()));
        assertThat(someObject.disabled(Type.CLASS), is(nullValue()));
    }
    
    
}
