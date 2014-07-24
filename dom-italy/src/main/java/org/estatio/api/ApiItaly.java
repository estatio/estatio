/*
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.inject.Inject;

import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.dom.asset.registration.FixedAssetRegistration;
import org.estatio.dom.asset.registration.FixedAssetRegistrationTypes;
import org.estatio.dom.asset.registration.FixedAssetRegistrations;
import org.estatio.dom.asset.registration.LandRegister;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;

public class ApiItaly extends AbstractFactoryAndRepository {

    @Override
    public String getId() {
        return "apiItaly";
    }

    public String iconName() {
        return "Api";
    }

    // //////////////////////////////////////

    @MemberOrder(name = "Migration", sequence = "90")
    @ActionSemantics(Of.IDEMPOTENT)
    public void putLandRegister(
            final @Named("propertyReference") String propertyReference,
            final @Named("unitReference") String unitReference,
            final @Named("comuneAmministrativo") @Optional String comuneAmministrativo,
            final @Named("comuneCatastale") @Optional String comuneCatastale,
            final @Named("codiceComuneCatastale") @Optional String codiceComuneCatastale,
            final @Named("rendita") @Optional BigDecimal rendita,
            final @Named("foglio") @Optional String foglio,
            final @Named("particella") @Optional String particella,
            final @Named("subalterno") @Optional String subalterno,
            final @Named("categoria") @Optional String categoria,
            final @Named("classe") @Optional String classe,
            final @Named("consistenza") @Optional String consistenza) {
        Unit unit = units.findUnitByReference(unitReference);
        if (unit == null) {
            unit = units.findUnitByReference(unitReference.replace(" ", "+"));
        }
        if (unit == null) {
            throw new IllegalArgumentException("Unknown unit: ".concat(unitReference));
        }
        LandRegister landRegister = null;
        List<FixedAssetRegistration> farts = fixedAssetRegistrations.findBySubject(unit);
        if (!farts.isEmpty()) {
            landRegister = (LandRegister) farts.get(0);
        }
        if (landRegister == null) {
            landRegister = (LandRegister) fixedAssetRegistrationTypes.findByTitle("LandRegister").create(getContainer());
            landRegister.setSubject(unit);
            landRegister.setComuneAmministrativo(comuneAmministrativo);
            landRegister.setComuneCatastale(codiceComuneCatastale);
            landRegister.setCodiceComuneCatastale(codiceComuneCatastale);
            landRegister.setRendita(rendita == null ? null : rendita.setScale(2, RoundingMode.HALF_EVEN));
            landRegister.setFoglio(foglio);
            landRegister.setParticella(particella);
            landRegister.setSubalterno(subalterno);
            landRegister.setCategoria(categoria);
            landRegister.setClasse(classe);
            landRegister.setConsistenza(consistenza);
            persistIfNotAlready(landRegister);
        }
    }

    // //////////////////////////////////////

    @Inject
    private Units units;

    @Inject
    private FixedAssetRegistrations fixedAssetRegistrations;

    @Inject
    private FixedAssetRegistrationTypes fixedAssetRegistrationTypes;

}
