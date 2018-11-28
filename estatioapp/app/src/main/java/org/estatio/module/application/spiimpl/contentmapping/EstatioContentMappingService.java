/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.module.application.spiimpl.contentmapping;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.conmap.ContentMappingService;

import org.estatio.module.base.platform.applib.DtoFactory;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class EstatioContentMappingService implements ContentMappingService {

    /**
     * Implements chain of responsibility pattern.
     *
     * @param object
     * @param acceptableMediaTypes
     * @return
     */
    @Programmatic
    @Override
    public Object map(
            final Object object,
            final List<MediaType> acceptableMediaTypes) {

        for (final DtoFactory dtoFactory : dtoFactories) {
            if(dtoFactory.accepts(object, acceptableMediaTypes)) {
                return dtoFactory.newDto(object, acceptableMediaTypes);
            }
        }

        return null;
    }

    @javax.inject.Inject
    List<DtoFactory> dtoFactories;

}
