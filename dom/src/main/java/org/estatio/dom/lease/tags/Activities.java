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
package org.estatio.dom.lease.tags;

import java.util.Collections;
import java.util.List;

import javax.jdo.Query;

import com.google.common.collect.ImmutableMap;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;

import org.estatio.dom.EstatioDomainService;

@Hidden
public class Activities extends EstatioDomainService<Activity> {

    public Activities() {
        super(Activities.class, Activity.class);
    }

    // //////////////////////////////////////
    
    @SuppressWarnings({ "unchecked" })
    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<String> findUniqueNames(final Sector sector) {
        if(sector == null) {
            return Collections.emptyList();
        }
        final Query query = newQuery("SELECT name FROM org.estatio.dom.lease.tags.Activity WHERE sector == :sector");
        return (List<String>) query.executeWithMap(ImmutableMap.of("sector", sector));
    }

    @Hidden
    public Activity findBySectorAndName(final Sector sector, final String name) {
        return firstMatch("findBySectorAndName", "sector", sector, "name", name);
    }


}
