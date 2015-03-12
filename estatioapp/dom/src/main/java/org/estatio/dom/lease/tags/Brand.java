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

import java.math.BigDecimal;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.wicket.markup.parser.XmlTag.TagType;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionInteraction;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.services.eventbus.ActionInteractionEvent;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.WithNameComparable;
import org.estatio.dom.WithNameUnique;
import org.estatio.dom.party.Party;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRate;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE, 
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Unique(name = "Brand_name_UNQ", members = "name")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.tags.Brand "
                        + "WHERE name == :name"),
        @javax.jdo.annotations.Query(
                name = "matchByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.tags.Brand "
                        + "WHERE name.matches(:name)"),
        @javax.jdo.annotations.Query(
                name = "findUniqueNames", language = "JDOQL",
                value = "SELECT name "
                        + "FROM org.estatio.dom.lease.tags.Brand")
})
@AutoComplete(repository = Brands.class, action = "autoComplete")
@Immutable
public class Brand
        extends EstatioDomainObject<Brand>
        implements WithNameUnique, WithNameComparable<Brand> {

    public Brand() {
        super("name");
    }

    // //////////////////////////////////////

    private String name;

    @javax.jdo.annotations.Column(allowsNull = "false", length=JdoColumnLength.NAME)
    @Title
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Brand change(
            final @Named("Name") String name) {

        setName(name);
        return this;
    }

    public String default0Change() {
        return getName();
    }

    // //////////////////////////////////////
    
    public static class RemoveEvent extends ActionInteractionEvent<Brand> {
        private static final long serialVersionUID = 1L;

        public RemoveEvent(
                final Brand source,
                final Identifier identifier,
                final Object... arguments) {
            super(source, identifier, arguments);
        }

        public Brand getReplacement() {
            return (Brand) (this.getArguments().isEmpty() ? null : getArguments().get(0));
        }
    }

    @Action(domainEvent = Brand.RemoveEvent.class)
    public void remove() {
        removeAndReplace(null);
    }

    @Action(domainEvent = Brand.RemoveEvent.class)
    public void removeAndReplace(@ParameterLayout(named = "Replace with") @Parameter(optionality = Optionality.OPTIONAL) Brand replacement) {
        getContainer().remove(this);
        getContainer().flush();
    }
    
    public String validateRemoveAndReplace(final Brand brand) {
        return brand != this ? null : "Cannot replace a brand with itself";
   }
    
    
}
