/*
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
package org.estatio.app.bulkupdate;

import java.util.List;

import org.estatio.dom.EstatioService;
import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.LeaseTerms;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotContributed.As;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.memento.MementoService.Memento;

@Immutable
public class PropertyServiceChargeBulkUpdateContributions extends EstatioService<PropertyServiceChargeBulkUpdateContributions> {

    public PropertyServiceChargeBulkUpdateContributions() {
        super(PropertyServiceChargeBulkUpdateContributions.class);
    }

    // //////////////////////////////////////

    
    @NotContributed(As.ASSOCIATION) // ie *is* contributed as action
    @NotInServiceMenu
    public PropertyServiceChargeBulkUpdate bulkUpdate(
            final Property property, 
            @Named("Start date") final LocalDate startDate) {
        final String memento = mementoFor(property, startDate);
        return getContainer().newViewModelInstance(PropertyServiceChargeBulkUpdate.class, memento);
    }
    public List<LocalDate> choices1BulkUpdate(Property property) {
        return leaseTerms.findServiceChargeDatesByProperty(property);
    }


    
    // //////////////////////////////////////

    String mementoFor(final Property property, final LocalDate startDate) {
        final Memento memento = getMementoService().create();
        final Bookmark propertyBookmark = getBookmarkService().bookmarkFor(property);
        memento.set("property", propertyBookmark);
        memento.set("startDate", startDate);
        return memento.asString();
    }
    
    void init(String mementoStr, PropertyServiceChargeBulkUpdate propertyServiceChargeBulkUpdate) {
        final Memento memento = getMementoService().parse(mementoStr);
        final Bookmark propertyBookmark = memento.get("property", Bookmark.class);
        propertyServiceChargeBulkUpdate.setProperty(getBookmarkService().lookup(propertyBookmark, Property.class));
        propertyServiceChargeBulkUpdate.setStartDate(memento.get("startDate", LocalDate.class));
    }

    // //////////////////////////////////////

    private LeaseTerms leaseTerms;
    public final void injectLeaseTerms(LeaseTerms leaseTerms) {
        this.leaseTerms = leaseTerms;
    }

}
