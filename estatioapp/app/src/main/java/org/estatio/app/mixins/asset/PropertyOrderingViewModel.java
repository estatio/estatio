/*
 *  Copyright 2015-2016 Eurocommercial Properties NV
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
package org.estatio.app.mixins.asset;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.ViewModel;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.MemberGroupLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;

import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        objectType = "org.estatio.app.mixins.asset.PropertyOrderingViewModel"
)
@MemberGroupLayout(
        columnSpans = {6,0,0,6},
        left = {"Group", "Ordering"}
)
@DomainObjectLayout(
        cssClassFa = "sort-alpha-asc"
)
public class PropertyOrderingViewModel implements ViewModel {

    @Inject
    BookmarkService bookmarkService;
    @Inject
    DomainObjectContainer container;

    @Override
    public String viewModelMemento() {
        final Property property = getProperty();
        final Bookmark bookmark = bookmarkService.bookmarkFor(property);
        return bookmark.getIdentifier();
    }

    @Override
    public void viewModelInit(final String memento) {
        final Bookmark bookmark = bookmarkService.bookmarkFor(Property.class, memento);
        this.property = (Property) bookmarkService.lookup(bookmark);
    }


    public PropertyOrderingViewModel() {
    }

    public PropertyOrderingViewModel(final Property property) {
        setProperty(property);
    }

    //@XmlElement
    @org.apache.isis.applib.annotation.Property
    @PropertyLayout(
            hidden = Where.PARENTED_TABLES
    )
    @Getter @Setter
    @MemberOrder(name="Group", sequence = "1")
    private Property property;


    // @XmlTransient
    private Integer displayOrder;

    @org.apache.isis.applib.annotation.Property
    @PropertyLayout()
    @MemberOrder(name = "Ordering", sequence = "2")
    public Integer getDisplayOrder() {
        return property.getDisplayOrder();
    }



    @Title
    @org.apache.isis.applib.annotation.Property
    @PropertyLayout(
            hidden = Where.OBJECT_FORMS
    )
    @MemberOrder(name="Ordering", sequence = "3")
    public String getName() {
        return container.titleOf(property);
    }




    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(
            cssClassFa = "arrow-up"
    )
    @MemberOrder(name = "displayOrder", sequence = "1")
    public PropertyOrderingViewModel moveUp() {
        return move(-15);
    }


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(
            cssClassFa = "arrow-down"
    )
    @MemberOrder(name = "displayOrder", sequence = "2")
    public PropertyOrderingViewModel moveDown() {
        return move(+15);
    }

    @Programmatic
    private PropertyOrderingViewModel move(int diff) {
        int fallback = Integer.MAX_VALUE;
        if(diff < 0) fallback = Integer.MIN_VALUE;

        List<Property> properties = repository.allProperties();
        Collections.sort(properties);
        reorder(properties);
        spreadOut(properties, 10);
        updateCurrent(diff, fallback);
        Collections.sort(properties);
        reorder(properties);
        return this;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(
            cssClassFa = "ban"
    )
    @MemberOrder(name = "displayOrder", sequence = "3")
    public PropertyOrderingViewModel clear() {
        property.setDisplayOrder(null);
        reorder(repository.allProperties());
        return this;
    }


    private void spreadOut(final List<Property> propertiesBefore, final int factor) {
        for (Property property : propertiesBefore) {
            final Integer displayOrder = property.getDisplayOrder();
            if(displayOrder != null) {
                property.setDisplayOrder(displayOrder * factor);
            } else {
                return;
            }
        }
    }
    private void updateCurrent(final int adjust, final int fallback) {
        final Integer currDisplayOrder = property.getDisplayOrder();
        property.setDisplayOrder(
                currDisplayOrder != null
                        ? currDisplayOrder + adjust
                        : fallback);
    }

    private void reorder(final List<Property> propertiesAfter) {
        int num = 0;
        for (Property property : propertiesAfter) {
            if(property.getDisplayOrder() != null) {
                property.setDisplayOrder(++num);
            } else {
                return;
            }
        }
    }

    @Collection(
            notPersisted = true // so that Apache Isis ignores when remapping
    )
    @CollectionLayout(
            defaultView = "table",
            paged = 100
    )
    public List<PropertyOrderingViewModel> getProperties() {
        List<Property> properties = repository.allProperties();
        Collections.sort(properties);
        return Lists.newArrayList(
                    FluentIterable.from(properties)
                                  .transform(toViewModel())
                 );
    }

    private Function<Property, PropertyOrderingViewModel> toViewModel() {
        return new Function<Property, PropertyOrderingViewModel>() {
            @Override public PropertyOrderingViewModel apply(final Property property) {
                final PropertyOrderingViewModel vm = PropertyOrderingViewModel.this;
                return property == vm.property
                        ? vm // can't have two view models both representing the same contact group at same time
                        : container.injectServicesInto(new PropertyOrderingViewModel(property));
            }
        };
    }


    // @XmlTransient
    @Inject
    PropertyRepository repository;

    static class Functions {
        private Functions(){}

    }
}
