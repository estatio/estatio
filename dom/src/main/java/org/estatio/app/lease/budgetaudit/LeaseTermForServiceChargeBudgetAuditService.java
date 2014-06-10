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
package org.estatio.app.lease.budgetaudit;

import java.math.BigDecimal;
import java.util.List;
import javax.annotation.PostConstruct;
import com.danhaywood.isis.domainservice.excel.applib.ExcelService;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.annotation.NotContributed.As;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.applib.services.memento.MementoService.Memento;
import org.estatio.dom.EstatioService;
import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.LeaseTerms;

@DomainService(menuOrder = "00")
@Immutable
public class LeaseTermForServiceChargeBudgetAuditService extends EstatioService<LeaseTermForServiceChargeBudgetAuditService> {

    public LeaseTermForServiceChargeBudgetAuditService() {
        super(LeaseTermForServiceChargeBudgetAuditService.class);
    }

    // //////////////////////////////////////

    @PostConstruct
    public void init() {
        if(bookmarkService == null) {
            throw new IllegalStateException("Require BookmarkService to be configured");
        }
        if(excelService == null) {
            throw new IllegalStateException("Require ExcelService to be configured");
        }
    }

    // //////////////////////////////////////
    
    @NotContributed(As.ASSOCIATION) // ie *is* contributed as action
    @NotInServiceMenu
    public LeaseTermForServiceChargeBudgetAuditManager maintainServiceCharges(
            final Property property, 
            @Named("Start date") final LocalDate startDate) {
        LeaseTermForServiceChargeBudgetAuditManager template = new LeaseTermForServiceChargeBudgetAuditManager();
        template.setProperty(property);
        template.setStartDate(startDate);
        return newManager(template);
    }

    public List<LocalDate> choices1MaintainServiceCharges(final Property property) {
        return leaseTerms.findServiceChargeDatesByProperty(property);
    }

    
    // //////////////////////////////////////
    // memento for manager
    // //////////////////////////////////////
    
    String mementoFor(final LeaseTermForServiceChargeBudgetAuditManager manager) {
        final Memento memento = getMementoService().create();
        final Bookmark propertyBookmark = getBookmarkService().bookmarkFor(manager.getProperty());
        memento.set("property", propertyBookmark);
        memento.set("startDate", manager.getStartDate());
        return memento.asString();
    }
    
    void initOf(final String mementoStr, final LeaseTermForServiceChargeBudgetAuditManager manager) {
        final Memento memento = getMementoService().parse(mementoStr);
        final Bookmark propertyBookmark = memento.get("property", Bookmark.class);
        manager.setProperty(getBookmarkService().lookup(propertyBookmark, Property.class));
        manager.setStartDate(memento.get("startDate", LocalDate.class));
    }

    LeaseTermForServiceChargeBudgetAuditManager newManager(LeaseTermForServiceChargeBudgetAuditManager manager) {
        final String memento = mementoFor(manager);
        return getContainer().newViewModelInstance(LeaseTermForServiceChargeBudgetAuditManager.class, memento);
    }

    // //////////////////////////////////////
    // memento for lease term "line item"
    // //////////////////////////////////////
    
    String mementoFor(final LeaseTermForServiceChargeBudgetAuditLineItem lineItem) {
        final Memento memento = getMementoService().create();
        
        memento.set("leaseTerm", bookmarkService.bookmarkFor(lineItem.getLeaseTerm()));
        memento.set("budgetedValue", lineItem.getBudgetedValue());
        memento.set("auditedValue", lineItem.getAuditedValue());
        memento.set("nextLeaseTerm", bookmarkService.bookmarkFor(lineItem.getNextLeaseTerm()));
        memento.set("nextBudgetedValue", lineItem.getNextBudgetedValue());

        return memento.asString();
    }
    
    void initOf(final String mementoStr, final LeaseTermForServiceChargeBudgetAuditLineItem lineItem) {
        final Memento memento = getMementoService().parse(mementoStr);

        lineItem.setLeaseTerm(bookmarkService.lookup(memento.get("leaseTerm", Bookmark.class), LeaseTermForServiceCharge.class));
        lineItem.setBudgetedValue(memento.get("budgetedValue", BigDecimal.class));
        lineItem.setAuditedValue(memento.get("auditedValue", BigDecimal.class));
        lineItem.setNextLeaseTerm(bookmarkService.lookup(memento.get("nextLeaseTerm", Bookmark.class), LeaseTermForServiceCharge.class));
        lineItem.setNextBudgetedValue(memento.get("nextBudgetedValue", BigDecimal.class));
    }

    LeaseTermForServiceChargeBudgetAuditLineItem newLineItem(LeaseTermForServiceChargeBudgetAuditLineItem lineItem) {
        final String memento = mementoFor(lineItem);
        return getContainer().newViewModelInstance(LeaseTermForServiceChargeBudgetAuditLineItem.class, memento);
    }

    // //////////////////////////////////////
    // Injected Services
    // //////////////////////////////////////

    @javax.inject.Inject
    private ExcelService excelService;

    @javax.inject.Inject
    private BookmarkService bookmarkService;
    
    @javax.inject.Inject
    private LeaseTerms leaseTerms;
    
}
