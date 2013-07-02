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
package org.estatio.dom.asset;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

@Hidden
public class FixedAssets extends EstatioDomainService<FixedAsset> {

    public FixedAssets() {
        super(FixedAssets.class, FixedAsset.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<FixedAsset> findAssetsByReferenceOrName(String searchPhrase) {
        return allMatches("findAssetsByReferenceOrName", "regex", StringUtils.wildcardToCaseInsensitiveRegex(searchPhrase));
    }

    // //////////////////////////////////////

    @Hidden
    public List<FixedAsset> autoComplete(String searchPhrase) {
        return findAssetsByReferenceOrName("*".concat(searchPhrase).concat("*"));
    }


}
