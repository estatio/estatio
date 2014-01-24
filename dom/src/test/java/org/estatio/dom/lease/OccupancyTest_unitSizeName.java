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
package org.estatio.dom.lease;

import org.jmock.auto.Mock;
import org.junit.Rule;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.lease.tags.UnitSize;
import org.estatio.dom.lease.tags.UnitSizes;

public class OccupancyTest_unitSizeName {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private UnitSizes mockUnitSizes;
    
    @Mock
    private DomainObjectContainer mockContainer;

    private Occupancy occupancy;
    private UnitSize unitSize;
    
//    @Before
//    public void setup() {
//        occupancy = new Occupancy();
//        occupancy.injectUnitSizes(mockUnitSizes);
//        occupancy.setContainer(mockContainer);
//        
//        unitSize = new UnitSize();
//        unitSize.setName("BOUTIQUE");
//    }
//
//    @Test
//    public void getUnitSizeName_whenNone() {
//        // given
//        assertThat(occupancy.getUnitSize(), is(nullValue()));
//        // then
//        assertThat(occupancy.getUnitSizeName(), is(nullValue()));
//    }
//    
//    @Test
//    public void getUnitSizeName_whenUnit() {
//        // given
//        occupancy.setUnitSize(unitSize);
//        assertThat(occupancy.getUnitSize(), is(unitSize));
//        // then
//        assertThat(occupancy.getUnitSizeName(), is("BOUTIQUE"));
//    }
//    
//    // //////////////////////////////////////
//
//
//    @Test
//    public void setUnitSizeName_whenNull() {
//        
//        // given
//        occupancy.setUnitSize(unitSize);
//        assertThat(occupancy.getUnitSize(), is(not(nullValue())));
//
//        // when
//        occupancy.setUnitSizeName(null);
//        
//        // then
//        assertThat(occupancy.getUnitSizeName(), is(nullValue()));
//        assertThat(occupancy.getUnitSize(), is(nullValue()));
//    }
//
//    @Test
//    public void setUnitSizeName_whenNotNull_alreadyExists() {
//        
//        // given
//        occupancy.setUnitSize(unitSize);
//        assertThat(occupancy.getUnitSize(), is(not(nullValue())));
//
//        // when
//        final UnitSize existingUnitSize = new UnitSize();
//        existingUnitSize.setName("LARGE");
//        context.checking(new Expectations() {
//            {
//                oneOf(mockUnitSizes).findByName("LARGE");
//                will(returnValue(existingUnitSize));
//            }
//        });
//        
//        occupancy.setUnitSizeName("LARGE");
//        
//        // then
//        assertThat(occupancy.getUnitSizeName(), is("LARGE"));
//        assertThat(occupancy.getUnitSize(), is(existingUnitSize));
//    }
//    
//    @Test
//    public void setUnitSizeName_whenNotNull_doesNotExist() {
//        
//        // given
//        occupancy.setUnitSize(unitSize);
//        assertThat(occupancy.getUnitSize(), is(not(nullValue())));
//        
//        // when
//        final UnitSize newUnitSize = new UnitSize();
//        context.checking(new Expectations() {
//            {
//                oneOf(mockUnitSizes).findByName("LARGE");
//                will(returnValue(null));
//                
//                oneOf(mockContainer).newTransientInstance(UnitSize.class);
//                will(returnValue(newUnitSize));
//                
//                oneOf(mockContainer).persistIfNotAlready(newUnitSize);
//            }
//        });
//        
//        occupancy.setUnitSizeName("LARGE");
//        
//        // then
//        assertThat(occupancy.getUnitSizeName(), is("LARGE"));
//        assertThat(occupancy.getUnitSize(), is(newUnitSize));
//    }
//    
//    // //////////////////////////////////////
//
//    @Test
//    public void newUnitSize() {
//        // given
//        final String[] arg = new String[1];
//        occupancy = new Occupancy() {
//            @Override
//            public void setUnitSizeName(String unitSizeName) {
//                arg[0] = unitSizeName;
//            }
//        };
//        // when
//        occupancy.newUnitSize("LARGE");
//        // then (delegates to the setUnitSize)
//        assertThat(arg[0], is("LARGE"));
//    }
//
//    // //////////////////////////////////////
//
//    @Test
//    public void disableNewUnitSize_whenLocked() {
//        occupancy.setLockable(Status.LOCKED);
//        assertThat(occupancy.disableNewUnitSize(null), is("Cannot modify when locked"));
//    }
//
//    @Test
//    public void disableNewUnitSize_whenUnlocked() {
//        occupancy.setLockable(Status.UNLOCKED);
//        assertThat(occupancy.disableNewUnitSize(null), is(nullValue()));
//    }
//    
//    
}
