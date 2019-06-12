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

package org.estatio.module.capex.app;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.numerator.dom.Numerator;
import org.estatio.module.numerator.dom.NumeratorAtPathRepository;
import org.estatio.module.party.dom.Organisation;

@DomainService(nature = NatureOfService.DOMAIN)
public class NumeratorForOrdersRepository {

    Numerator findOrderNumerator(final Organisation buyer) {
        return numeratorAtPathRepository.findNumerator("Order number", buyer, buyer.getApplicationTenancy());
    }

    @Inject
    NumeratorAtPathRepository numeratorAtPathRepository;
}
