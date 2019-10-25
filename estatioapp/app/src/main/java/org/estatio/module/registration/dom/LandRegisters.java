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
package org.estatio.module.registration.dom;

import java.math.BigDecimal;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.*;
import org.joda.time.LocalDate;
import org.apache.isis.applib.services.factory.FactoryService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.registration.FixedAssetRegistrationTypeRepository;

@DomainService(menuOrder = "10", repositoryFor = LandRegister.class)
public class LandRegisters extends UdoDomainRepositoryAndFactory<LandRegister> {

    public LandRegisters() {
        super(LandRegisters.class, LandRegister.class);
    }

    // //////////////////////////////////////

    @PostConstruct
    @Programmatic
    public void init(Map<String, String> properties) {
        super.init(properties);
        fixedAssetRegistrationTypeRepository.findOrCreate(LandRegisterConstants.FART_LAND_REGISTER, LandRegister.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public LandRegister newRegistration(
            final FixedAsset subject,
            final LandRegister previous,
            final @ParameterLayout(named = "Comune amministrativo") @Parameter(optionality = Optionality.OPTIONAL) String comuneAmministrativo,
            final @ParameterLayout(named = "Comune catastale") @Parameter(optionality = Optionality.OPTIONAL) String comuneCatastale,
            final @ParameterLayout(named = "Codice comuneCatastale") @Parameter(optionality = Optionality.OPTIONAL) String codiceComuneCatastale,
            final @ParameterLayout(named = "Rendita") @Parameter(optionality = Optionality.OPTIONAL) BigDecimal rendita,
            final @ParameterLayout(named = "Foglio") @Parameter(optionality = Optionality.OPTIONAL) String foglio,
            final @ParameterLayout(named = "Particella") @Parameter(optionality = Optionality.OPTIONAL) String particella,
            final @ParameterLayout(named = "Subalterno") @Parameter(optionality = Optionality.OPTIONAL) String subalterno,
            final @ParameterLayout(named = "Categoria") @Parameter(optionality = Optionality.OPTIONAL) String categoria,
            final @ParameterLayout(named = "Classe") @Parameter(optionality = Optionality.OPTIONAL) String classe,
            final @ParameterLayout(named = "Consistenza") @Parameter(optionality = Optionality.OPTIONAL) String consistenza,
            final @ParameterLayout(named = "Start date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @ParameterLayout(named = "Description") @Parameter(optionality = Optionality.OPTIONAL) String description) {
        LandRegister obj = (LandRegister) fixedAssetRegistrationTypeRepository.findByTitle(LandRegisterConstants.FART_LAND_REGISTER).create(factoryService);
        obj.setSubject(subject);
        obj.setComuneAmministrativo(comuneAmministrativo);
        obj.setComuneCatastale(comuneCatastale);
        obj.setCodiceComuneCatastale(codiceComuneCatastale);
        obj.setRendita(rendita);
        obj.setFoglio(foglio);
        obj.setParticella(particella);
        obj.setSubalterno(subalterno);
        obj.setCategoria(categoria);
        obj.setClasse(classe);
        obj.setConsistenza(consistenza);
        obj.setDescription(description);
        persist(obj);
        if (previous != null) {
            previous.setNext(obj);
            obj.setPrevious(previous);
        }
        obj.changeDates(startDate, null);
        return obj;
    }

    // //////////////////////////////////////

    @Inject
    private FixedAssetRegistrationTypeRepository fixedAssetRegistrationTypeRepository;

    @Inject
    FactoryService factoryService;

}
