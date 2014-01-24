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

import org.estatio.dom.lease.tags.Brand;
import org.estatio.dom.lease.tags.Brands;

public class OccupancyTest_brandName {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private Brands mockBrands;
    
    @Mock
    private DomainObjectContainer mockContainer;

    private Occupancy occupancy;
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
