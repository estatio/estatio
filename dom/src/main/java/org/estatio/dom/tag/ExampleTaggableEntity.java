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
package org.estatio.dom.tag;

import java.util.List;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;

public class ExampleTaggableEntity implements  Taggable {


    private static final String TAG_NAME_BRAND = "Brand";
    
    // //////////////////////////////////////

    private Tag brandTag;

    @javax.jdo.annotations.Column(name = "BRANDTAG_ID", allowsNull="true")
    @Hidden
    public Tag getBrandTag() {
        return brandTag;
    }

    public void setBrandTag(final Tag brandTag) {
        this.brandTag = brandTag;
    }

    @Optional
    public String getBrand() {
        final Tag existingTag = getBrandTag();
        return existingTag != null ? existingTag.getValue() : null;
    }

    public void setBrand(final String brand) {
        final Tag existingTag = getBrandTag();
        Tag tag = tags.tagFor(existingTag, this, TAG_NAME_BRAND, brand);
        setBrandTag(tag);
    }

    public List<String> choicesBrand() {
        return tags.choices(this, TAG_NAME_BRAND);
    }

    public ExampleTaggableEntity newBrand(@Named("Tag") @Optional final String brand) {
        setBrand(brand);
        return this;
    }

    public String default0NewBrand() {
        return getBrand();
    }


    // //////////////////////////////////////

    private Tags tags;

    public final void injectTags(final Tags tags) {
        this.tags = tags;
    }

}
