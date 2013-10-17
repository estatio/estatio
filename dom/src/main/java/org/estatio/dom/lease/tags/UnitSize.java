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

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;

import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.WithNameComparable;
import org.estatio.dom.WithNameUnique;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=IdGeneratorStrategy.NATIVE, 
        column="id")
@javax.jdo.annotations.Uniques({
    @javax.jdo.annotations.Unique(
            name = "UnitSize_name_UNQ", members="name")
})
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
            name = "findByName", language = "JDOQL", 
            value = "SELECT "
                    + "FROM org.estatio.dom.lease.tags.UnitSize "
                    + "WHERE name == :name"),
    @javax.jdo.annotations.Query(
            name = "findUniqueNames", language = "JDOQL", 
            value = "SELECT name "
                    + "FROM org.estatio.dom.lease.tags.UnitSize") 
})
@Immutable
public class UnitSize 
        extends EstatioRefDataObject<UnitSize> 
        implements WithNameUnique, WithNameComparable<UnitSize> {

    public UnitSize() {
        super("name");
    }
    

    // //////////////////////////////////////

    private String name;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Title
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

}
