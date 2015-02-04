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

package org.estatio.services.links;

import javax.jdo.annotations.IdentityType;

import org.apache.isis.applib.annotation.MemberGroupLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        table = "Link")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByClassName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.services.links.Link "
                        + "WHERE className == :className")
})
@javax.jdo.annotations.Unique(members = { "className", "name" })
@MemberGroupLayout(columnSpans = { 12, 0, 0, 12 })
public class Link extends EstatioDomainObject<Link> {

    public Link() {
        super("name");
    }

    // //////////////////////////////////////

    private String className;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.FQCN)
    @MemberOrder(sequence = "1")
    public String getClassName() {
        return className;
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    // //////////////////////////////////////

    private String name;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.NAME)
    @MemberOrder(sequence = "2")
    @Title
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // //////////////////////////////////////

    private String urlTemplate;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.Link.URL_TEMPLATE)
    @MemberOrder(sequence = "3")
    public String getUrlTemplate() {
        return urlTemplate;
    }

    public void setUrlTemplate(final String urlTemplate) {
        this.urlTemplate = urlTemplate;
    }

}
