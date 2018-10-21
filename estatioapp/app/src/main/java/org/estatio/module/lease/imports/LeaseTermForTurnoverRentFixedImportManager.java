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
package org.estatio.module.lease.imports;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.incode.module.base.dom.utils.TitleBuilder;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemRepository;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTermForFixed;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.lease.turnoverrent.LeaseTermForTurnoverRentFixedImportManager"
)
public class LeaseTermForTurnoverRentFixedImportManager {

    public static final String LEASE_TERM_FOR_TURNOVER_RENT_SHEET_NAME = "lease terms";

    //region > constructor, title
    public LeaseTermForTurnoverRentFixedImportManager() {
    }

    public LeaseTermForTurnoverRentFixedImportManager(Property property, int year) {
        this.property = property;
        this.year = year;
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getProperty())
                .withName(getYear())
                .toString();
    }
    //endregion


    @Getter @Setter
    private Property property;

    @org.apache.isis.applib.annotation.Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private int year;

    //region > download (action)
    @Action(semantics = SemanticsOf.SAFE)
    public Blob download() {
        final String fileName = "TurnoverRentBulkUpdate-" + getProperty().getReference() + "@" + getYear() + ".xlsx";
        final List<LeaseTermForTurnOverRentFixedImport> lineItems = getTurnoverRentLines();
        return excelService.toExcel(lineItems, LeaseTermForTurnOverRentFixedImport.class,
                LEASE_TERM_FOR_TURNOVER_RENT_SHEET_NAME, fileName);
    }

    public List<LeaseTermForTurnOverRentFixedImport> getTurnoverRentLines(){

        List<LeaseTermForTurnOverRentFixedImport> result = new ArrayList<>();
        List<Lease> leasesForProperty = leaseRepository.findLeasesByProperty(getProperty());
        leasesForProperty.forEach(x->{
            List<LeaseItem> torItems = leaseItemRepository.findLeaseItemsByType(x, LeaseItemType.TURNOVER_RENT_FIXED);
            torItems.forEach(tor->{
                LeaseTermForTurnOverRentFixedImport line = new LeaseTermForTurnOverRentFixedImport();
                line.setLeaseReference(x.getReference());
                line.setLeaseExternalReference(x.getExternalReference());
                tor.getTerms().forEach(torTerm -> {
                    if (torTerm.getStartDate().getYear()==getYear()-2) {
                        LeaseTermForFixed term = (LeaseTermForFixed) torTerm;
                        line.setStartDatePrevious(term.getStartDate());
                        line.setValuePrevious(term.getValue());
                    }
                    if (torTerm.getStartDate().getYear()==getYear()-1) {
                        LeaseTermForFixed term = (LeaseTermForFixed) torTerm;
                        line.setStartDateCurrent(term.getStartDate());
                        line.setValueCurrent(term.getValue());
                    }
                    if (torTerm.getStartDate().getYear()==getYear()) {
                        LeaseTermForFixed term = (LeaseTermForFixed) torTerm;
                        line.setStartDate(term.getStartDate());
                        line.setEndDate(term.getEndDate());
                        line.setValue(term.getValue());
                    }
                });
                // every item should produce a line since autocreate is turned off (ECP-806)
                if (line.getStartDateCurrent()==null && line.getStartDate()==null) {
                    line.setStartDate(new LocalDate(getYear(), 1, 1));
                    line.setEndDate(new LocalDate(getYear(), 12, 31));
                }
                result.add(line);
            });
        });

        return result;
    }

    //endregion

    //region > upload (action)

    @Action(publishing = Publishing.DISABLED, semantics = SemanticsOf.IDEMPOTENT)
    public LeaseTermForTurnoverRentFixedImportManager upload(
            @Parameter(fileAccept = ".xlsx")
            @ParameterLayout(named = "Excel spreadsheet")
            final Blob spreadsheet) {
        List<LeaseTermForTurnOverRentFixedImport> lineItems =
                excelService.fromExcel(spreadsheet, LeaseTermForTurnOverRentFixedImport.class,
                        LEASE_TERM_FOR_TURNOVER_RENT_SHEET_NAME);
        lineItems.forEach(x->x.importData());
        return this;
    }

    //endregion

    //region > injected services

    @Inject
    LeaseItemRepository leaseItemRepository;

    @Inject
    LeaseRepository leaseRepository;

    @javax.inject.Inject
    private ExcelService excelService;

    //endregion

}
