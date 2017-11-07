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
package org.estatio.module.lease.dom.occupancy.tags;

import java.util.Collections;
import java.util.List;

import javax.jdo.Query;

import com.google.common.collect.ImmutableMap;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(menuOrder = "99", repositoryFor = Activity.class, nature = NatureOfService.DOMAIN)
public class ActivityRepository extends UdoDomainRepositoryAndFactory<Activity> {

    public ActivityRepository() {
        super(ActivityRepository.class, Activity.class);
    }

    // //////////////////////////////////////
    
    public List<String> findUniqueNames(final Sector sector) {
        if(sector == null) {
            return Collections.emptyList();
        }
        final Query query = newQuery("SELECT name FROM org.estatio.module.lease.dom.occupancy.tags.Activity WHERE sector == :sector");
        return (List<String>) query.executeWithMap(ImmutableMap.of("sector", sector));
    }

    public List<Activity> findBySector(final Sector sector) {
        if(sector == null) {
            return Collections.emptyList();
        }
        final Query query = newQuery("SELECT FROM org.estatio.module.lease.dom.occupancy.tags.Activity WHERE sector == :sector");
        return (List<Activity>) query.executeWithMap(ImmutableMap.of("sector", sector));
    }

    public Activity findBySectorAndName(final Sector sector, final String name) {
        return firstMatch("findBySectorAndName", "sector", sector, "name", name);
    }

    @Programmatic
    public Activity findOrCreate(final Sector sector, final String name) {
        if (name == null) {
            return null;
        }
        Activity activity = findBySectorAndName(sector, name);
        if (activity == null) {
            activity = newTransientInstance(Activity.class);
            activity.setSector(sector);
            activity.setName(name);
        }
        return activity;
    }

    
}
