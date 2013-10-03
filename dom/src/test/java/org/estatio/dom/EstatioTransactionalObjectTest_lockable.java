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

public class EstatioTransactionalObjectTest_lockable {

    public static class SomeTransactionalObject extends EstatioTransactionalObject<SomeTransactionalObject, Status>  {
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
    public void whenNull() {
        // when
        assertThat(someTransactionalObject.getLockable(), is(nullValue()));
        assertThat(someTransactionalObject.isLocked(), is(true));
        
        assertThat(someTransactionalObject.hideLock(), is(true));
        assertThat(someTransactionalObject.hideUnlock(), is(false));
    }

    @Test
    public void lock() {
        
        // when
        someTransactionalObject.lock();
        
        // then
        assertThat(someTransactionalObject.getLockable(), is(Status.LOCKED));
        assertThat(someTransactionalObject.isLocked(), is(true));
        
        assertThat(someTransactionalObject.hideLock(), is(true));
        assertThat(someTransactionalObject.hideUnlock(), is(false));
    }

    @Test
    public void unlock() {
        
        // when
        someTransactionalObject.unlock();
        
        // then
        assertThat(someTransactionalObject.getLockable(), is(Status.UNLOCKED));
        assertThat(someTransactionalObject.isLocked(), is(false));

        assertThat(someTransactionalObject.hideLock(), is(false));
        assertThat(someTransactionalObject.hideUnlock(), is(true));
    }
    
    @Test
    public void setLockable_locked() {
        
        // when
        someTransactionalObject.setLockable(Status.LOCKED);
        
        // then
        assertThat(someTransactionalObject.getLockable(), is(Status.LOCKED));
        assertThat(someTransactionalObject.isLocked(), is(true));
        
        assertThat(someTransactionalObject.hideLock(), is(true));
        assertThat(someTransactionalObject.hideUnlock(), is(false));
    }
    
    @Test
    public void setLockable_unlocked() {
        
        // when
        someTransactionalObject.setLockable(Status.UNLOCKED);
        
        // then
        assertThat(someTransactionalObject.getLockable(), is(Status.UNLOCKED));
        assertThat(someTransactionalObject.isLocked(), is(false));
        
        assertThat(someTransactionalObject.hideLock(), is(false));
        assertThat(someTransactionalObject.hideUnlock(), is(true));
    }
    

}
