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
package org.estatio.dom.lease.tags;

import java.util.List;

import javax.jdo.Query;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(menuOrder = "99", repositoryFor = Sector.class, nature = NatureOfService.DOMAIN)
public class SectorRepository extends UdoDomainRepositoryAndFactory<Sector> {

    public SectorRepository() {
        super(SectorRepository.class, Sector.class);
    }

    public List<String> findUniqueNames() {

        final Query query = newQuery("SELECT name FROM " + getEntityType().getName());
        return (List<String>) query.execute();
    }

    public Sector findByName(final String name) {
        return uniqueMatch("findByName", "name", name);
    }

    @Programmatic
    public Sector findOrCreate(final String name) {
        if (name == null) {
            return null;
        }
        Sector sector = findByName(name);
        if (sector == null) {
            sector = newTransientInstance(Sector.class);
            sector.setName(name);
        }
        return sector;
    }

}
