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
package org.estatio.dom.instance;

import java.util.SortedSet;
import java.util.TreeSet;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.estatio.dom.EstatioMutableObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.geography.Country;

@javax.jdo.annotations.PersistenceCapable(identityType= IdentityType.APPLICATION)
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByPath", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.instance.EstatioInstance "
                        + "WHERE path == :path"),
        @javax.jdo.annotations.Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.instance.EstatioInstance "
                        + "WHERE applicationTenancy.name == :name")
})
@Immutable
@Bounded
public class EstatioInstance extends EstatioMutableObject<EstatioInstance> {

    public EstatioInstance() {
        super("path");
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.NotPersistent
    @NotPersisted
    @Disabled
    @Title
    public String getName() {
        return applicationTenancy.getName();
    }

    // //////////////////////////////////////

    private String path;

    @javax.jdo.annotations.PrimaryKey
    @javax.jdo.annotations.Column(length = JdoColumnLength.EstatioInstance.PATH, allowsNull = "false")
    @Disabled
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // //////////////////////////////////////

    private ApplicationTenancy applicationTenancy;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Disabled
    //@Hidden
    public ApplicationTenancy getApplicationTenancy() {
        return applicationTenancy;
    }

    public void setApplicationTenancy(ApplicationTenancy applicationTenancy) {
        this.applicationTenancy = applicationTenancy;
    }

    // //////////////////////////////////////

    private Country country;

    @javax.jdo.annotations.Column(name = "countryId", allowsNull = "true")
    @Disabled
    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }

    // //////////////////////////////////////

    private EstatioInstance parent;

    @javax.jdo.annotations.Column(name = "parentPath", allowsNull = "true")
    @Hidden(where = Where.PARENTED_TABLES)
    @Disabled
    public EstatioInstance getParent() {
        return parent;
    }

    public void setParent(EstatioInstance estatioInstance) {
        this.parent = estatioInstance;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "parent")
    private SortedSet<EstatioInstance> children = new TreeSet<EstatioInstance>();

    @CollectionLayout(
        render = CollectionLayout.RenderType.EAGERLY
    )
    @Disabled
    public SortedSet<EstatioInstance> getChildren() {
        return children;
    }

    public void setChildren(SortedSet<EstatioInstance> children) {
        this.children = children;
    }

    // //////////////////////////////////////

}
