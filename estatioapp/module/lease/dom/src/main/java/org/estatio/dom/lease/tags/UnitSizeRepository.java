/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

import java.util.List;

import javax.jdo.Query;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(repositoryFor = UnitSize.class, nature = NatureOfService.DOMAIN)
public class UnitSizeRepository extends UdoDomainRepositoryAndFactory<UnitSize> {

    public UnitSizeRepository() {
        super(UnitSizeRepository.class, UnitSize.class);
    }

    @SuppressWarnings({ "unchecked" })
    @Programmatic
    public List<String> findUniqueNames() {
        final Query query = newQuery("SELECT name FROM org.estatio.dom.lease.tags.UnitSize");
        return (List<String>) query.execute();
    }

    @Programmatic
    public UnitSize findByName(final String name) {
        return uniqueMatch("findByName", "name", name);
    }

    @Programmatic
    public UnitSize findOrCreate(final String name) {
        if (name == null) {
            return null;
        }
        UnitSize unitSize = findByName(name);
        if (unitSize == null) {
            unitSize = newTransientInstance(UnitSize.class);
            unitSize.setName(name);
        }
        return unitSize;
    }
}
