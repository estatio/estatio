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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import com.google.common.collect.Lists;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.Status;
import org.estatio.dom.lease.tags.Activities;
import org.estatio.dom.lease.tags.Activity;
import org.estatio.dom.lease.tags.Sector;
import org.estatio.dom.lease.tags.Sectors;

public class OccupancyTest_activityName {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private Sectors mockSectors;

    @Mock
    private Activities mockActivities;
    
    @Mock
    private DomainObjectContainer mockContainer;

    private Occupancy occupancy;
    private Sector sector;
    private Activity activity;
    
    @Before
    public void setup() {
        occupancy = new Occupancy();
        occupancy.injectSectors(mockSectors);
        occupancy.injectActivities(mockActivities);
        occupancy.setContainer(mockContainer);
        
        sector = new Sector();
        sector.setName("FOOD");
        
        activity = new Activity();
        activity.setName("RESTAURANT");
    }

    @Test
    public void getActivityName_whenNone() {
        // given
        assertThat(occupancy.getActivity(), is(nullValue()));
        // then
        assertThat(occupancy.getActivityName(), is(nullValue()));
    }
    
    @Test
    public void getActivityName_whenUnit() {
        // given
        occupancy.setActivity(activity);
        assertThat(occupancy.getActivity(), is(activity));
        // then
        assertThat(occupancy.getActivityName(), is("RESTAURANT"));
    }
    
    // //////////////////////////////////////


    @Test
    public void setActivityName_whenNull() {
        
        // given
        occupancy.setActivity(activity);
        assertThat(occupancy.getActivity(), is(not(nullValue())));

        // when
        occupancy.setActivityName(null);
        
        // then
        assertThat(occupancy.getActivityName(), is(nullValue()));
        assertThat(occupancy.getActivity(), is(nullValue()));
    }

    @Test
    public void setActivityName_whenNotNull_alreadyExists() {
        
        // given
        occupancy.setSector(sector);
        occupancy.setActivity(activity);
        assertThat(occupancy.getActivity(), is(not(nullValue())));

        // when
        final Activity existingActivity = new Activity();
        existingActivity.setName("RESTAURANT");
        context.checking(new Expectations() {
            {
                oneOf(mockActivities).findBySectorAndName(sector, "RESTAURANT");
                will(returnValue(existingActivity));
            }
        });
        
        occupancy.setActivityName("RESTAURANT");
        
        // then
        assertThat(occupancy.getActivityName(), is("RESTAURANT"));
        assertThat(occupancy.getActivity(), is(existingActivity));
    }
    
    @Test
    public void setActivityName_whenNotNull_doesNotExist() {
        
        // given
        occupancy.setSector(sector);
        occupancy.setActivity(activity);
        assertThat(occupancy.getActivity(), is(not(nullValue())));
        
        // when
        final Activity newActivity = new Activity();
        context.checking(new Expectations() {
            {
                oneOf(mockActivities).findBySectorAndName(sector, "RESTAURANT");
                will(returnValue(null));
                
                oneOf(mockContainer).newTransientInstance(Activity.class);
                will(returnValue(newActivity));
                
                oneOf(mockContainer).persistIfNotAlready(newActivity);
            }
        });
        
        occupancy.setActivityName("RESTAURANT");
        
        // then
        assertThat(occupancy.getActivityName(), is("RESTAURANT"));
        assertThat(occupancy.getActivity(), is(newActivity));
        assertThat(newActivity.getSector(), is(sector));
    }
    
    // //////////////////////////////////////

    @Test
    public void newActivity() {
        // given
        final String[] pSectorName = new String[1];
        final String[] pActivityName = new String[1];
        occupancy = new Occupancy() {
            @Override
            public void setSectorName(String sectorName) {
                pSectorName[0] = sectorName;
            }
            @Override
            public void setActivityName(String activityName) {
                pActivityName[0] = activityName;
            }
        };
        // when
        occupancy.newActivity("FOOD", "RESTAURANT");
        // then (delegates to the setSectorName and setActivityName)
        assertThat(pSectorName[0], is("FOOD"));
        assertThat(pActivityName[0], is("RESTAURANT"));
    }

    // //////////////////////////////////////

    @Test
    public void choicesNewActivity() {
        final List<String> sectors = Lists.newArrayList("CLOTHES", "FOOD", "OTHER");
        context.checking(new Expectations() {
            {
                oneOf(mockSectors).findUniqueNames();
                will(returnValue(sectors));
            }
        });
        assertThat(occupancy.choices0NewActivity(), is(sectors));
    }

    
    // //////////////////////////////////////

    @Test
    public void disableNewActivity_whenLocked() {
        occupancy.setLockable(Status.LOCKED);
        assertThat(occupancy.disableNewActivity(null,null), is("Cannot modify when locked"));
    }

    @Test
    public void disableNewActivity_whenUnlocked() {
        occupancy.setLockable(Status.UNLOCKED);
        assertThat(occupancy.disableNewActivity(null,null), is(nullValue()));
    }
    
    
}
