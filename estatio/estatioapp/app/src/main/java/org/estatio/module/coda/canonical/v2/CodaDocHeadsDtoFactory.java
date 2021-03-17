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
package org.estatio.module.coda.canonical.v2;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.canonical.coda.v2.CodaDocHeadsDto;
import org.estatio.module.base.platform.applib.DtoFactoryAbstract;
import org.estatio.module.coda.dom.doc.CodaDocHead;

@DomainService(nature = NatureOfService.DOMAIN)
public class CodaDocHeadsDtoFactory extends DtoFactoryAbstract<List, CodaDocHeadsDto> {

    public CodaDocHeadsDtoFactory() {
        super(List.class, CodaDocHeadsDto.class);
    }

    @Override
    protected CodaDocHeadsDto newDto(final List codaDocHeads) {
        return internalNewDto(codaDocHeads);
    }

    CodaDocHeadsDto internalNewDto(final List<CodaDocHead> codaDocHeads) {
        final CodaDocHeadsDto codaDocHeadsDto = new CodaDocHeadsDto();
        codaDocHeadsDto.setMajorVersion("2");
        codaDocHeadsDto.setMinorVersion("0");

        codaDocHeads.forEach(codaDocHead -> codaDocHeadsDto.getCodaDocHeads().add(codaDocHeadDtoFactory.newType(codaDocHead)));
        return codaDocHeadsDto;
    }

    @Inject
    CodaDocHeadDtoFactory codaDocHeadDtoFactory;

}
