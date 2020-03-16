/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.module.capex.dom.project;

import java.math.BigInteger;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.party.dom.NumeratorAtPathRepository;
import org.estatio.module.numerator.dom.NumeratorRepository;

/**
 * TODO: refactor to call {@link NumeratorRepository} directly, rather than the legacy {@link NumeratorAtPathRepository}.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class NumeratorForProjectsRepository {

    public final static String NUMERATOR_NAME = "Project number";

    /**
     *
     * @param atPath - this should instead be refactored so that the caller provides the country
     * @param format
     * @return
     */
    public Numerator findProjectNumerator(final String atPath, final String format) {
        return numeratorAtPathRepository.findOrCreateNumerator(
                NUMERATOR_NAME,
                null,
                format,
                BigInteger.ZERO,
                applicationTenancyRepository.findByPath(atPath));
    }

    @Inject
    NumeratorAtPathRepository numeratorAtPathRepository;

    @Inject ApplicationTenancyRepository applicationTenancyRepository;

}
