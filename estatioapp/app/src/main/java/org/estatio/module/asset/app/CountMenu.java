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
package org.estatio.module.asset.app;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;

import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.asset.dom.counts.Type;
import org.estatio.module.asset.imports.CountImport;
import org.estatio.module.countryapptenancy.dom.CountryServiceForCurrentUser;

@DomainService(
        objectType = "org.estatio.module.asset.app.CountMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY
)
public class CountMenu {

    public Blob exportCountImportTemplate() {

        List<CountData> data = new ArrayList<>();
        countryServiceForCurrentUser.countriesForCurrentUser().forEach(c->{
            propertyRepository.allProperties().stream().filter(p->p.getCountry().equals(c)).forEach(p->{
                data.add(new CountData(p.getReference(), new LocalDate(clockService.now().getYear(), clockService.now().getMonthOfYear(), 1), null, null));
            });
        });
        return excelService.toExcel(data, CountData.class, "counts", "counts.xlsx");
    }

    public List<CountData> importCounts(final Blob sheet){
        List<CountData> result = new ArrayList<>();
        final List<CountData> counts = excelService.fromExcel(sheet, CountData.class, "counts");
        counts.stream().forEach(c->{
            final CountData impResult = importCount(c);
            if (impResult!=null && (impResult.getPedestrialCount()!=null || impResult.getCarCount()!=null)) result.add(impResult);
        });
        return result;
    }

    private CountData importCount(final CountData c) {
        List<Object> footfall = null;
        List<Object> carcount = null;

        if (c.getDate()==null) {
            messageService.warnUser(String.format("No or invalid date used for %s", c.getPropertyReference()));
            return null;
        }

        if (c.getPedestrialCount() != null) {
            CountImport footfallImport = new CountImport(c.getPropertyReference(), Type.PEDESTRIAL.name(), c.getDate(), c.getPedestrialCount());
            serviceRegistry2.injectServicesInto(footfallImport);
            footfall = footfallImport.importData();
        }
        if (c.getCarCount() != null) {
            CountImport carCountImport = new CountImport(c.getPropertyReference(), Type.CAR.name(), c.getDate(), c.getCarCount());
            serviceRegistry2.injectServicesInto(carCountImport);
            carcount = carCountImport.importData();
        }
        if (footfall == null || footfall.isEmpty())
            c.setPedestrialCount(null);
        if (carcount == null || carcount.isEmpty())
            c.setCarCount(null);
        return c;
    }

    @Inject ExcelService excelService;

    @Inject PropertyRepository propertyRepository;

    @Inject CountryServiceForCurrentUser countryServiceForCurrentUser;

    @Inject ServiceRegistry2 serviceRegistry2;

    @Inject MessageService messageService;

    @Inject ClockService clockService;
}
