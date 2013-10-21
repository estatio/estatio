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
package org.estatio.dom.lease;

import org.jmock.auto.Mock;
import org.junit.Rule;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.lease.tags.Sector;
import org.estatio.dom.lease.tags.Sectors;

public class OccupancyTest_sectorName {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private Sectors mockSectors;
    
    @Mock
    private DomainObjectContainer mockContainer;

    private Occupancy occupancy;
    private Sector sector;
    
//    @Before
//    public void setup() {
//        occupancy = new Occupancy();
//        occupancy.injectSectors(mockSectors);
//        occupancy.setContainer(mockContainer);
//        
//        sector = new Sector();
//        sector.setName("FOOD");
//    }
//
//    @Test
//    public void getSectorName_whenNone() {
//        // given
//        assertThat(occupancy.getSector(), is(nullValue()));
//        // then
//        assertThat(occupancy.getSectorName(), is(nullValue()));
//    }
//    
//    @Test
//    public void getSectorName_whenUnit() {
//        // given
//        occupancy.setSector(sector);
//        assertThat(occupancy.getSector(), is(sector));
//        // then
//        assertThat(occupancy.getSectorName(), is("FOOD"));
//    }
//    
//    // //////////////////////////////////////
//
//
//    @Test
//    public void setSectorName_whenNull() {
//        
//        // given
//        occupancy.setSector(sector);
//        assertThat(occupancy.getSector(), is(not(nullValue())));
//
//        // when
//        occupancy.setSectorName(null);
//        
//        // then
//        assertThat(occupancy.getSectorName(), is(nullValue()));
//        assertThat(occupancy.getSector(), is(nullValue()));
//    }
//
//    @Test
//    public void setSectorName_whenNotNull_alreadyExists() {
//        
//        // given
//        occupancy.setSector(sector);
//        assertThat(occupancy.getSector(), is(not(nullValue())));
//
//        // when
//        final Sector existingSector = new Sector();
//        existingSector.setName("FOOD");
//        context.checking(new Expectations() {
//            {
//                oneOf(mockSectors).findByName("FOOD");
//                will(returnValue(existingSector));
//            }
//        });
//        
//        occupancy.setSectorName("FOOD");
//        
//        // then
//        assertThat(occupancy.getSectorName(), is("FOOD"));
//        assertThat(occupancy.getSector(), is(existingSector));
//    }
//    
//    @Test
//    public void setSectorName_whenNotNull_doesNotExist() {
//        
//        // given
//        occupancy.setSector(sector);
//        assertThat(occupancy.getSector(), is(not(nullValue())));
//        
//        // when
//        final Sector newSector = new Sector();
//        context.checking(new Expectations() {
//            {
//                oneOf(mockSectors).findByName("FOOD");
//                will(returnValue(null));
//                
//                oneOf(mockContainer).newTransientInstance(Sector.class);
//                will(returnValue(newSector));
//                
//                oneOf(mockContainer).persistIfNotAlready(newSector);
//            }
//        });
//        
//        occupancy.setSectorName("FOOD");
//        
//        // then
//        assertThat(occupancy.getSectorName(), is("FOOD"));
//        assertThat(occupancy.getSector(), is(newSector));
//    }
//    
//    // //////////////////////////////////////
//
//    @Test
//    public void newSector() {
//        // given
//        final String[] pSectorName = new String[1];
//        final String[] pActivityName = new String[1];
//        occupancy = new Occupancy() {
//            @Override
//            public void setSectorName(String sectorName) {
//                pSectorName[0] = sectorName;
//            }
//            @Override
//            public void setActivityName(String activityName) {
//                pActivityName[0] = activityName;
//            }
//        };
//        // when
//        occupancy.newSector("FOOD", "RESTAURANT");
//        // then (delegates to the setSectorName and setActivityName)
//        assertThat(pSectorName[0], is("FOOD"));
//        assertThat(pActivityName[0], is("RESTAURANT"));
//    }
//
//    // //////////////////////////////////////
//
//    @Test
//    public void disableNewSector_whenLocked() {
//        occupancy.setLockable(Status.LOCKED);
//        assertThat(occupancy.disableNewSector(null, null), is("Cannot modify when locked"));
//    }
//
//    @Test
//    public void disableNewSector_whenUnlocked() {
//        occupancy.setLockable(Status.UNLOCKED);
//        assertThat(occupancy.disableNewSector(null, null), is(nullValue()));
//    }
//    
//    
}
