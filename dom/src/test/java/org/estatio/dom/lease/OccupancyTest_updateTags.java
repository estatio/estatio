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

import org.estatio.dom.lease.tags.Activities;
import org.estatio.dom.lease.tags.Activity;
import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.lease.tags.Brands;
import org.estatio.dom.lease.tags.Sector;
import org.estatio.dom.lease.tags.Sectors;
import org.estatio.dom.lease.tags.UnitSize;
import org.estatio.dom.lease.tags.UnitSizes;

public class OccupancyTest_updateTags {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private UnitSizes mockUnitSizes;
    @Mock
    private Sectors mockSectors;
    @Mock
    private Activities mockActivities;
    @Mock
    private Brands mockBrands;
    
    @Mock
    private DomainObjectContainer mockContainer;

    private Occupancy occupancy;
    private Sector sector;
    private Activity activity;
    private Brand brand;

    private UnitSize unitSize;

    
//    @Before
//    public void setup() {
//        occupancy = new Occupancy();
//        occupancy.injectUnitSizes(mockUnitSizes);
//        occupancy.injectSectors(mockSectors);
//        occupancy.injectActivities(mockActivities);
//        occupancy.injectBrands(mockBrands);
//        occupancy.setContainer(mockContainer);
//        
//        unitSize = new UnitSize();
//        unitSize.setName("LARGE");
//        
//        sector = new Sector();
//        sector.setName("FOOD");
//        
//        activity = new Activity();
//        activity.setName("RESTAURANT");
//        activity.setSector(sector);
//        
//        brand = new Brand();
//        brand.setName("SUPERMAC");
//        
//        occupancy.setUnitSize(unitSize);
//        occupancy.setSector(sector);
//        occupancy.setActivity(activity);
//        occupancy.setBrand(brand);
//    }
//
//
//    @Test
//    public void newSector() {
//        // given
//        final String[] pUnitSizeName = new String[1];
//        final String[] pBrandName = new String[1];
//        final String[] pSectorName = new String[1];
//        final String[] pActivityName = new String[1];
//        occupancy = new Occupancy() {
//            @Override
//            public void setUnitSizeName(String unitSizeName) {
//                pUnitSizeName[0] = unitSizeName;
//            }
//            @Override
//            public void setBrandName(String brandName) {
//                pBrandName[0] = brandName;
//            }
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
//        occupancy.updateTags("LARGE","FOOD", "RESTAURANT", "SUPERMAC");
//        // then (delegates to the setters)
//        assertThat(pUnitSizeName[0], is("LARGE"));
//        assertThat(pSectorName[0], is("FOOD"));
//        assertThat(pActivityName[0], is("RESTAURANT"));
//        assertThat(pBrandName[0], is("SUPERMAC"));
//    }
//
//    // //////////////////////////////////////
//
//    @Test
//    public void disableUpdateTags_whenLocked() {
//        occupancy.setLockable(Status.LOCKED);
//        assertThat(occupancy.disableUpdateTags(null, null, null, null), is("Cannot modify when locked"));
//    }
//
//    @Test
//    public void disableUpdateTags_whenUnlocked() {
//        occupancy.setLockable(Status.UNLOCKED);
//        assertThat(occupancy.disableUpdateTags(null, null, null, null), is(nullValue()));
//    }
//
//    // //////////////////////////////////////
//
//    @Test
//    public void default0UpdateTags() {
//        assertThat(occupancy.default0UpdateTags(), is("LARGE"));
//    }
//    
//    @Test
//    public void default1UpdateTags() {
//        assertThat(occupancy.default1UpdateTags(), is("FOOD"));
//    }
//    
//    @Test
//    public void default2UpdateTags() {
//        assertThat(occupancy.default2UpdateTags(), is("RESTAURANT"));
//    }
//    
//    @Test
//    public void default3UpdateTags() {
//        assertThat(occupancy.default3UpdateTags(), is("SUPERMAC"));
//    }
//
//    // //////////////////////////////////////
//
//    @Test
//    public void choices0UpdateTags() {
//        final List<String> unitSizes = Lists.newArrayList("LARGE", "SMALL", "BOUTIQUE");
//        context.checking(new Expectations() {
//            {
//                oneOf(mockUnitSizes).findUniqueNames();
//                will(returnValue(unitSizes));
//            }
//        });
//        assertThat(occupancy.choices0UpdateTags(), is(unitSizes));
//    }
//
//    @Test
//    public void choices1UpdateTags() {
//        final List<String> sectors = Lists.newArrayList("CLOTHES", "FOOD", "OTHER");
//        context.checking(new Expectations() {
//            {
//                oneOf(mockSectors).findUniqueNames();
//                will(returnValue(sectors));
//            }
//        });
//        assertThat(occupancy.choices1UpdateTags(), is(sectors));
//    }
//    
//    @Test
//    public void choices2UpdateTags() {
//        final List<String> activities = Lists.newArrayList("RESTAURANT", "CAFE", "RETAIL");
//        context.checking(new Expectations() {
//            {
//                oneOf(mockSectors).findByName("FOOD");
//                will(returnValue(sector));
//                
//                oneOf(mockActivities).findUniqueNames(sector);
//                will(returnValue(activities));
//            }
//        });
//        assertThat(occupancy.choices2UpdateTags(null, "FOOD"), is(activities));
//    }
//
//
//    @Test
//    public void choices3UpdateTags() {
//        final List<String> brands = Lists.newArrayList("SUPERMAC", "BK", "PRET");
//        context.checking(new Expectations() {
//            {
//                oneOf(mockBrands).findUniqueNames();
//                will(returnValue(brands));
//            }
//        });
//        assertThat(occupancy.choices3UpdateTags(), is(brands));
//    }

}
