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
package org.estatio.dom.asset;

import java.util.List;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.incode.module.base.dom.utils.StringUtils;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = FixedAsset.class
)
public class FixedAssetRepository extends UdoDomainRepositoryAndFactory<FixedAsset> {

    public FixedAssetRepository() {
        super(FixedAssetRepository.class, FixedAsset.class);
    }

    public List<FixedAsset> matchAssetsByReferenceOrName(final String searchPhrase) {
        return allMatches("matchByReferenceOrName",
                "regex", StringUtils.wildcardToCaseInsensitiveRegex(searchPhrase));
    }

    /**
     * To support autoComplete on {@link FixedAsset} per {@link DomainObject#autoCompleteRepository()}.
     */
    public List<FixedAsset> autoComplete(final String searchPhrase) {
        return matchAssetsByReferenceOrName("*".concat(searchPhrase).concat("*"));
    }

}
