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

import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;

import org.estatio.dom.asset.Unit;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Queries({ 
    @javax.jdo.annotations.Query(
            name = "findByReferenceOrName", language = "JDOQL", 
            value = "SELECT "
                    + "FROM org.estatio.dom.lease.UnitForLease "
                    + "WHERE (reference.matches(:referenceOrName) "
                    + "    || name.matches(:referenceOrName))"), 
    @javax.jdo.annotations.Query(
            name = "findByReference", language = "JDOQL", 
            value = "SELECT "
                    + "FROM org.estatio.dom.lease.UnitForLease "
                    + "WHERE reference.matches(:reference)") 
})
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class UnitForLease extends Unit {

    @javax.jdo.annotations.Persistent(mappedBy = "unit", defaultFetchGroup = "false")
    private SortedSet<LeaseUnit> leases = new TreeSet<LeaseUnit>();

    @Render(Type.EAGERLY)
    @MemberOrder(sequence = "2.2")
    public SortedSet<LeaseUnit> getLeases() {
        return leases;
    }

    public void setLeases(final SortedSet<LeaseUnit> leases) {
        this.leases = leases;
    }

}
