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
package org.estatio.module.turnover.app;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.turnover.dom.Frequency;
import org.estatio.module.turnover.dom.Turnover;
import org.estatio.module.turnover.dom.TurnoverRepository;
import org.estatio.module.turnover.dom.Type;
import org.estatio.module.turnover.dom.entry.TurnoverEntryService;
import org.estatio.module.turnover.imports.TurnoverImportManager;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.module.turnover.app.TurnoverMenu"
)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY)
public class TurnoverMenu  {

    @Action(restrictTo = RestrictTo.PROTOTYPING)
    public void createEmptyTurnoversForPeriod(final LocalDate startDate, final LocalDate endDate){
        if (!endDate.isBefore(startDate)){
            LocalDate date = startDate;
            while (!date.isAfter(endDate)){
                turnoverEntryService.produceEmptyTurnoversFor(date);
                date = date.plusDays(1);
            }
        }
    }

    public TurnoverImportManager importTurnovers(
            final Property property,
            final Type type,
            final Frequency frequency,
            final LocalDate turnoverDate
    ){
        TurnoverImportManager manager = new TurnoverImportManager();
        serviceRegistry2.injectServicesInto(manager);
        manager.setPropertyReference(property.getReference());
        manager.setType(type);
        manager.setFrequency(frequency);
        manager.setDate(turnoverDate);
        manager.setLines(manager.getLines());
        return manager;
    }

    @Action(restrictTo = RestrictTo.PROTOTYPING, semantics = SemanticsOf.SAFE)
    public List<Turnover> allTurnovers(){
        return turnoverRepository.listAll();
    }

    @Inject TurnoverRepository turnoverRepository;

    @Inject TurnoverEntryService turnoverEntryService;

    @Inject ServiceRegistry2 serviceRegistry2;

}
