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

import javax.transaction.Status;

import org.junit.Before;
import org.junit.Test;

import org.estatio.dom.EstatioMutableAndLockableObjectTest_disabled.SomeMutableAndLockableObject;
import org.estatio.dom.EstatioMutableAndLockableObjectTest_disabled.SomeMutableObjectStatus;

public class EstatioMutableAndLockableObjectTest_lockable {

    private SomeMutableAndLockableObject someObject;
    
    @Before
    public void setUp() throws Exception {
        someObject = new SomeMutableAndLockableObject();
    }
    
    @Test
    public void whenNull() {
        // when
        assertThat(someObject.getLockable(), is(nullValue()));
        assertThat(someObject.isLocked(), is(true));
        
        assertThat(someObject.hideLock(), is(true));
        assertThat(someObject.hideUnlock(), is(false));
    }

    @Test
    public void lock() {
        
        // when
        someObject.lock();
        
        // then
        assertThat(someObject.getLockable(), is(SomeMutableObjectStatus.LOCKED));
        assertThat(someObject.isLocked(), is(true));
        
        assertThat(someObject.hideLock(), is(true));
        assertThat(someObject.hideUnlock(), is(false));
    }

    @Test
    public void unlock() {
        
        // when
        someObject.unlock();
        
        // then
        assertThat(someObject.getLockable(), is(SomeMutableObjectStatus.UNLOCKED));
        assertThat(someObject.isLocked(), is(false));

        assertThat(someObject.hideLock(), is(false));
        assertThat(someObject.hideUnlock(), is(true));
    }
    
    @Test
    public void setLockable_locked() {
        
        // when
        someObject.setLockable(SomeMutableObjectStatus.LOCKED);
        
        // then
        assertThat(someObject.getLockable(), is(SomeMutableObjectStatus.LOCKED));
        assertThat(someObject.isLocked(), is(true));
        
        assertThat(someObject.hideLock(), is(true));
        assertThat(someObject.hideUnlock(), is(false));
    }
    
    @Test
    public void setLockable_unlocked() {
        
        // when
        someObject.setLockable(SomeMutableObjectStatus.UNLOCKED);
        
        // then
        assertThat(someObject.getLockable(), is(SomeMutableObjectStatus.UNLOCKED));
        assertThat(someObject.isLocked(), is(false));
        
        assertThat(someObject.hideLock(), is(false));
        assertThat(someObject.hideUnlock(), is(true));
    }
    

}
