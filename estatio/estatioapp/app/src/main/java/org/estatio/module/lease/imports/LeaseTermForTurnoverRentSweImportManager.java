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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
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
import org.estatio.module.lease.dom.LeaseTermForTurnoverRent;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.app.services.lease.turnoverrent.LeaseTermForTurnoverRentSweImportManager"
)
public class LeaseTermForTurnoverRentSweImportManager {

    public static final String LEASE_TERM_FOR_TURNOVER_RENT_SHEET_NAME = "lease terms";

    //region > constructor, title
    public LeaseTermForTurnoverRentSweImportManager() {
    }

    public LeaseTermForTurnoverRentSweImportManager(Property property, int year) {
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

    private LocalDate startOfTheYear(){
        return new LocalDate(getYear(), 1, 1);
    }

    private LocalDate endOfTheYear(){
        return new LocalDate(getYear(), 12, 31);
    }

    //region > download (action)
    @Action(semantics = SemanticsOf.SAFE)
    public Blob download() {
        final String fileName = "TurnoverRentBulkUpdate-" + getProperty().getReference() + "@" + getYear() + ".xlsx";
        final List<LeaseTermForTurnOverRentSweImport> lineItems = getTurnoverRentLines();
        return excelService.toExcel(lineItems, LeaseTermForTurnOverRentSweImport.class,
                LEASE_TERM_FOR_TURNOVER_RENT_SHEET_NAME, fileName);
    }

    public List<LeaseTermForTurnOverRentSweImport> getTurnoverRentLines(){

        List<LeaseTermForTurnOverRentSweImport> result = new ArrayList<>();
        List<Lease> leasesForProperty = leaseRepository.findLeasesByProperty(getProperty())
                .stream()
                .filter(l->l.getEffectiveInterval().contains(startOfTheYear()))
                .collect(Collectors.toList());
        leasesForProperty.forEach(x->{
            List<LeaseItem> torItems = leaseItemRepository.findLeaseItemsByType(x, LeaseItemType.TURNOVER_RENT);
            torItems.forEach(tor->{
                tor.verifyUntil(endOfTheYear());
                LeaseTermForTurnOverRentSweImport line = new LeaseTermForTurnOverRentSweImport();
                line.setYear(getYear());
                line.setLeaseReference(x.getReference());
                line.setLeaseExternalReference(x.getExternalReference());
                tor.getTerms().forEach(torTerm -> {
                    if (torTerm.getStartDate().getYear()==getYear()-1) {
                        LeaseTermForTurnoverRent term = (LeaseTermForTurnoverRent) torTerm;
                        line.setStartDatePreviousYear(term.getStartDate());
                        line.setEndDatePreviousYear(term.getEndDate());
                        line.setValuePreviousYear(term.getManualTurnoverRent());
                    }
                    if (torTerm.getStartDate().getYear()==getYear()) {
                        LeaseTermForTurnoverRent term = (LeaseTermForTurnoverRent) torTerm;
                        line.setStartDate(term.getStartDate());
                        line.setEndDate(term.getEndDate());
                        line.setValue(term.getManualTurnoverRent());
                        line.setPercentage(turnoverRentRuleStringToPercentage(term.getTurnoverRentRule()));
                    }
                });
                // TODO: evaluate this - it will change when we return to regular turnover rent items !!
                // every item should produce a line since autocreate is turned off (ECP-806)
                if (line.getEndDatePreviousYear()==null && line.getStartDate()==null) {
                    line.setStartDate(startOfTheYear());
                    line.setEndDate(endOfTheYear());
                }
                if (line.getEndDatePreviousYear()!=null && line.getStartDate()==null){
                    line.setStartDate(determineNextTermStartDate(line.getEndDatePreviousYear()));
                    line.setEndDate(endOfTheYear());
                }
                result.add(line);
            });
        });

        return result;
    }

    @Programmatic
    public static BigDecimal turnoverRentRuleStringToPercentage(final String rentRule){
        if (rentRule == null ) return null;
        if (rentRule == "") return null;
        return new BigDecimal(rentRule.replaceFirst(";.*$", ""));
    }

    LocalDate determineNextTermStartDate(final LocalDate endDatePreviousYear){
        return endDatePreviousYear.isBefore(startOfTheYear()) ? startOfTheYear() : endDatePreviousYear.plusDays(1);

    }

    //endregion

    //region > upload (action)

    @Action(publishing = Publishing.DISABLED, semantics = SemanticsOf.IDEMPOTENT)
    public LeaseTermForTurnoverRentSweImportManager upload(
            @Parameter(fileAccept = ".xlsx")
            @ParameterLayout(named = "Excel spreadsheet")
            final Blob spreadsheet) {
        List<LeaseTermForTurnOverRentSweImport> lineItems =
                excelService.fromExcel(spreadsheet, LeaseTermForTurnOverRentSweImport.class,
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
