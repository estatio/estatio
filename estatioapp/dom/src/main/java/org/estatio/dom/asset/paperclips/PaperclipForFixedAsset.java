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
package org.estatio.dom.asset.paperclips;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.documents.dom.links.Paperclip;
import org.incode.module.documents.dom.links.PaperclipRepository;
import org.incode.module.documents.dom.links.T_paperclips;

import org.estatio.dom.asset.FixedAsset;

@javax.jdo.annotations.PersistenceCapable(
        identityType= IdentityType.DATASTORE
//        ,
//        schema = "estatioAssets"  // DN doesn't seem to allow this to be in a different schema to FixedAsset...
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@DomainObject(
        objectType = "estatioAssets.PaperclipForFixedAsset"
)
@DomainObjectLayout(
        titleUiEvent = Paperclip.TitleUiEvent.class,
        iconUiEvent = Paperclip.IconUiEvent.class,
        cssClassUiEvent = Paperclip.CssClassUiEvent.class,
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class PaperclipForFixedAsset extends Paperclip {

    //region > fixedAsset (property)
    private FixedAsset<?> fixedAsset;
    @Column(
            allowsNull = "false",
            name = "fixedAssetId"
    )
    public FixedAsset<?> getFixedAsset() {
        return fixedAsset;
    }

    public void setFixedAsset(final FixedAsset<?> fixedAsset) {
        this.fixedAsset = fixedAsset;
    }
    //endregion

    //region > attachedTo (hook, derived)
    @NotPersistent
    @Override
    public Object getAttachedTo() {
        return getFixedAsset();
    }

    @Override
    protected void setAttachedTo(final Object object) {
        setFixedAsset((FixedAsset<?>) object);
    }
    //endregion

    //region > SubtypeProvider SPI implementation
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SubtypeProvider extends PaperclipRepository.SubtypeProviderAbstract {
        public SubtypeProvider() {
            super(FixedAsset.class, PaperclipForFixedAsset.class);
        }
    }
    //endregion

    //region > mixins

    @Mixin
    public static class _paperclips extends T_paperclips<FixedAsset> {
        public _paperclips(final FixedAsset<?> fixedAsset) {
            super(fixedAsset);
        }
    }

    //endregion

}
