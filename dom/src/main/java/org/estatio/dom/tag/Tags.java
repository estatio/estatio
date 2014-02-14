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
import java.util.TreeSet;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.bookmark.Bookmark;

import org.estatio.dom.EstatioDomainService;

public class Tags extends EstatioDomainService<Tag> {

    public Tags() {
        super(Tags.class, Tag.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<String> choices(final Taggable domainObject, final String tagName) {
        final List<Tag> tags = doChoices(domainObject, tagName);
        final Iterable<String> tagNames = Iterables.transform(tags, Tag.GET_VALUE);
        final TreeSet<String> uniqueSortedTagNames = Sets.newTreeSet(tagNames);
        return Lists.newArrayList(uniqueSortedTagNames);
    }

    protected List<Tag> doChoices(final Taggable taggable, final String tagName) {
        return allMatches(Tag.class, appliesTo(taggable, tagName));
    }

    private Predicate<Tag> appliesTo(final Taggable taggable, final String tagName) {
        return new Predicate<Tag>(){
            @Override
            public boolean apply(final Tag t) {
                final Bookmark bookmark = getBookmarkService().bookmarkFor(taggable);
                return Objects.equal(t.getObjectType(), bookmark.getObjectType()) &&
                       Objects.equal(t.getName(), tagName);
            }
        };
    }

    @Programmatic
    public Tag tagFor(
            final Tag tag, 
            final Taggable taggableDomainObject, 
            final String tagName, final String tagValue) {
        if(Strings.isNullOrEmpty(tagValue)) {
            if(tag != null) {
                // remove existing
                remove(tag);
            }
            return null;
        } else {
            if(tag == null) {
                // create new
                Tag newTag = newTransientInstance();
                newTag.setTaggable(taggableDomainObject);
                newTag.setObjectType(determineObjectTypeFor(taggableDomainObject));
                newTag.setName(tagName);
                newTag.setValue(tagValue);
                persist(newTag);
                return newTag;
            } else {
                // update existing
                tag.setValue(tagValue);
                return tag;
            }
        }
    }
    
    private String determineObjectTypeFor(final Taggable taggable) {
        if (taggable == null) {
            return null;
        } 
        final Bookmark bookmark = getBookmarkService().bookmarkFor(taggable);
        if (bookmark == null) {
            return null;
        }
        return bookmark.getObjectType();

    }

}
