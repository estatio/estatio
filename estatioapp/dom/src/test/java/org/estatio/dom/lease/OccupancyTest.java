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

import java.util.List;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.PojoTester;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.WithIntervalMutableContractTestAbstract_changeDates;
import org.estatio.dom.lease.tags.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class OccupancyTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    Occupancy occupancy;

    @Mock
    DomainObjectContainer mockContainer;

    // TODO: the tests in here have all been commented out :-(
    public static class ActivityName extends OccupancyTest {

        @Mock
        private Sectors mockSectors;

        @Mock
        private Activities mockActivities;

        private Sector sector;
        private Activity activity;

//    @Before
//    public void setup() {
//        occupancy = new Occupancy();
//        occupancy.injectSectors(mockSectors);
//        occupancy.injectActivities(mockActivities);
//        occupancy.setContainer(mockContainer);
//        
//        sector = new Sector();
//        sector.setName("FOOD");
//        
//        activity = new Activity();
//        activity.setName("RESTAURANT");
//    }
//
//    @Test
//    public void getActivityName_whenNone() {
//        // given
//        assertThat(occupancy.getActivity(), is(nullValue()));
//        // then
//        assertThat(occupancy.getActivityName(), is(nullValue()));
//    }
//    
//    @Test
//    public void getActivityName_whenUnit() {
//        // given
//        occupancy.setActivity(activity);
//        assertThat(occupancy.getActivity(), is(activity));
//        // then
//        assertThat(occupancy.getActivityName(), is("RESTAURANT"));
//    }
//    
//    // //////////////////////////////////////
//
//
//    @Test
//    public void setActivityName_whenNull() {
//        
//        // given
//        occupancy.setActivity(activity);
//        assertThat(occupancy.getActivity(), is(not(nullValue())));
//
//        // when
//        occupancy.setActivityName(null);
//        
//        // then
//        assertThat(occupancy.getActivityName(), is(nullValue()));
//        assertThat(occupancy.getActivity(), is(nullValue()));
//    }
//
//    @Test
//    public void setActivityName_whenNotNull_alreadyExists() {
//        
//        // given
//        occupancy.setSector(sector);
//        occupancy.setActivity(activity);
//        assertThat(occupancy.getActivity(), is(not(nullValue())));
//
//        // when
//        final Activity existingActivity = new Activity();
//        existingActivity.setName("RESTAURANT");
//        context.checking(new Expectations() {
//            {
//                oneOf(mockActivities).findBySectorAndName(sector, "RESTAURANT");
//                will(returnValue(existingActivity));
//            }
//        });
//        
//        occupancy.setActivityName("RESTAURANT");
//        
//        // then
//        assertThat(occupancy.getActivityName(), is("RESTAURANT"));
//        assertThat(occupancy.getActivity(), is(existingActivity));
//    }
//    
//    @Test
//    public void setActivityName_whenNotNull_doesNotExist() {
//        
//        // given
//        occupancy.setSector(sector);
//        occupancy.setActivity(activity);
//        assertThat(occupancy.getActivity(), is(not(nullValue())));
//        
//        // when
//        final Activity newActivity = new Activity();
//        context.checking(new Expectations() {
//            {
//                oneOf(mockActivities).findBySectorAndName(sector, "RESTAURANT");
//                will(returnValue(null));
//                
//                oneOf(mockContainer).newTransientInstance(Activity.class);
//                will(returnValue(newActivity));
//                
//                oneOf(mockContainer).persistIfNotAlready(newActivity);
//            }
//        });
//        
//        occupancy.setActivityName("RESTAURANT");
//        
//        // then
//        assertThat(occupancy.getActivityName(), is("RESTAURANT"));
//        assertThat(occupancy.getActivity(), is(newActivity));
//        assertThat(newActivity.getSector(), is(sector));
//    }
//    
//    // //////////////////////////////////////
//
//    @Test
//    public void newActivity() {
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
//        occupancy.newActivity("FOOD", "RESTAURANT");
//        // then (delegates to the setSectorName and setActivityName)
//        assertThat(pSectorName[0], is("FOOD"));
//        assertThat(pActivityName[0], is("RESTAURANT"));
//    }
//
//    // //////////////////////////////////////
//
//    @Test
//    public void choicesNewActivity() {
//        final List<String> sectors = Lists.newArrayList("CLOTHES", "FOOD", "OTHER");
//        context.checking(new Expectations() {
//            {
//                oneOf(mockSectors).findUniqueNames();
//                will(returnValue(sectors));
//            }
//        });
//        assertThat(occupancy.choices0NewActivity(), is(sectors));
//    }
//
//    
//    // //////////////////////////////////////
//
//    @Test
//    public void disableNewActivity_whenLocked() {
//        occupancy.setLockable(Status.LOCKED);
//        assertThat(occupancy.disableNewActivity(null,null), is("Cannot modify when locked"));
//    }
//
//    @Test
//    public void disableNewActivity_whenUnlocked() {
//        occupancy.setLockable(Status.UNLOCKED);
//        assertThat(occupancy.disableNewActivity(null,null), is(nullValue()));
//    }


    }

    // TODO: the tests in here have all been commented out :-(
    public static class BrandName extends OccupancyTest {

        @Mock
        private Brands mockBrands;

        private Brand brand;

//    @Before
//    public void setup() {
//        occupancy = new Occupancy();
//        occupancy.injectBrands(mockBrands);
//        occupancy.setContainer(mockContainer);
//
//        brand = new Brand();
//        brand.setName("BOUTIQUE");
//    }
//
//    @Test
//    public void getBrandName_whenNone() {
//        // given
//        assertThat(occupancy.getBrand(), is(nullValue()));
//        // then
//        assertThat(occupancy.getBrandName(), is(nullValue()));
//    }
//
//    @Test
//    public void getBrandName_whenUnit() {
//        // given
//        occupancy.setBrand(brand);
//        assertThat(occupancy.getBrand(), is(brand));
//        // then
//        assertThat(occupancy.getBrandName(), is("BOUTIQUE"));
//    }
//
//    // //////////////////////////////////////
//
//
//    @Test
//    public void setBrandName_whenNull() {
//
//        // given
//        occupancy.setBrand(brand);
//        assertThat(occupancy.getBrand(), is(not(nullValue())));
//
//        // when
//        occupancy.setBrandName(null);
//
//        // then
//        assertThat(occupancy.getBrandName(), is(nullValue()));
//        assertThat(occupancy.getBrand(), is(nullValue()));
//    }

//    @Test
//    public void setBrandName_whenNotNull_alreadyExists() {
//
//        // given
//        occupancy.setBrand(brand);
//        assertThat(occupancy.getBrand(), is(not(nullValue())));
//
//        // when
//        final Brand existingBrand = new Brand();
//        existingBrand.setName("SUPERMAC");
//        context.checking(new Expectations() {
//            {
//                oneOf(mockBrands).findByName("SUPERMAC");
//                will(returnValue(existingBrand));
//            }
//        });
//
//        occupancy.setBrandName("SUPERMAC");
//
//        // then
//        assertThat(occupancy.getBrandName(), is("SUPERMAC"));
//        assertThat(occupancy.getBrand(), is(existingBrand));
//    }
//
//    @Test
//    public void setBrandName_whenNotNull_doesNotExist() {
//
//        // given
//        occupancy.setBrand(brand);
//        assertThat(occupancy.getBrand(), is(not(nullValue())));
//
//        // when
//        final Brand newBrand = new Brand();
//        context.checking(new Expectations() {
//            {
//                oneOf(mockBrands).findByName("SUPERMAC");
//                will(returnValue(null));
//
//                oneOf(mockContainer).newTransientInstance(Brand.class);
//                will(returnValue(newBrand));
//
//                oneOf(mockContainer).persistIfNotAlready(newBrand);
//            }
//        });
//
//        occupancy.setBrandName("SUPERMAC");
//
//        // then
//        assertThat(occupancy.getBrandName(), is("SUPERMAC"));
//        assertThat(occupancy.getBrand(), is(newBrand));
//    }
//
//    // //////////////////////////////////////
//
//    @Test
//    public void newBrand() {
//        // given
//        final String[] arg = new String[1];
//        occupancy = new Occupancy() {
//            @Override
//            public void setBrandName(String brandName) {
//                arg[0] = brandName;
//            }
//        };
//        // when
//        occupancy.newBrand("SUPERMAC");
//        // then (delegates to the setBrand)
//        assertThat(arg[0], is("SUPERMAC"));
//    }
//
//    // //////////////////////////////////////
//
//    @Test
//    public void disableNewBrand_whenLocked() {
//        occupancy.setLockable(Status.LOCKED);
//        assertThat(occupancy.disableNewBrand(null), is("Cannot modify when locked"));
//    }
//
//    @Test
//    public void disableNewBrand_whenUnlocked() {
//        occupancy.setLockable(Status.UNLOCKED);
//        assertThat(occupancy.disableNewBrand(null), is(nullValue()));
//    }
//
//
    }

    // TODO: the tests in here have all been commented out :-(
    public static class SectorName {

        @Mock
        private Sectors mockSectors;

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

    // TODO: the tests in here have all been commented out :-(
    public static class UnitSizeName extends OccupancyTest {

        @Mock
        private UnitSizes mockUnitSizes;

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

    // TODO: the tests in here have all been commented out :-(
    public static class UpdateTags extends OccupancyTest {

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

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(Lease.class))
                    .withFixture(pojos(Unit.class))
                    .withFixture(pojos(UnitSize.class))
                    .withFixture(pojos(Sector.class))
                    .withFixture(pojos(Activity.class))
                    .withFixture(pojos(Brand.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new Occupancy(),
                            PojoTester.FilterSet.excluding("unitSizeName", "sectorName", "activityName", "brandName"));
        }
    }

    public static class ChangeDates extends WithIntervalMutableContractTestAbstract_changeDates<Occupancy> {

        Occupancy occupancy;

        @Before
        public void setUp() throws Exception {
            occupancy = withIntervalMutable;
        }

        protected Occupancy doCreateWithIntervalMutable(final WithIntervalMutable.Helper<Occupancy> mockChangeDates) {
            return new Occupancy() {
                @Override
                org.estatio.dom.WithIntervalMutable.Helper<Occupancy> getChangeDates() {
                    return mockChangeDates;
                }
            };
        }

        // //////////////////////////////////////

        @Test
        public void changeDatesDelegate() {
            occupancy = new Occupancy();
            assertThat(occupancy.getChangeDates(), is(not(nullValue())));
        }

    }

    public static class CompareTo extends ComparableContractTest_compareTo<Occupancy> {

        private Lease lease1;
        private Lease lease2;

        private Unit unit1;
        private Unit unit2;

        @Before
        public void setUp() throws Exception {
            lease1 = new Lease();
            lease1.setReference("ABC");

            lease2 = new Lease();
            lease2.setReference("DEF");

            unit1 = new Unit();
            unit1.setName("ABC");

            unit2 = new Unit();
            unit2.setName("DEF");
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<List<Occupancy>> orderedTuples() {
            return listOf(
                    listOf(
                            newLeaseUnit(null, null, null),
                            newLeaseUnit(lease1, null, null),
                            newLeaseUnit(lease1, null, null),
                            newLeaseUnit(lease2, null, null))
                    , listOf(
                            newLeaseUnit(lease1, new LocalDate(2012, 4, 2), unit1),
                            newLeaseUnit(lease1, new LocalDate(2012, 3, 1), unit1),
                            newLeaseUnit(lease1, new LocalDate(2012, 3, 1), unit1),
                            newLeaseUnit(lease1, null, unit1))
                    , listOf(
                            newLeaseUnit(lease1, new LocalDate(2012, 3, 1), null),
                            newLeaseUnit(lease1, new LocalDate(2012, 3, 1), unit1),
                            newLeaseUnit(lease1, new LocalDate(2012, 3, 1), unit1),
                            newLeaseUnit(lease1, new LocalDate(2012, 3, 1), unit2)));
        }

        private Occupancy newLeaseUnit(
                Lease lease,
                LocalDate startDate,
                Unit unit) {
            final Occupancy ib = new Occupancy();
            ib.setLease(lease);
            ib.setUnit(unit);
            ib.setStartDate(startDate);
            return ib;
        }

    }

}