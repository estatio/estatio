/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.Query;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssetForTesting;

public class TagsTest_tagFor {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);


    private Tag tag;
    @Mock
    private EstatioDomainObject<?> mockCustomer;
    @Mock
    private DomainObjectContainer mockContainer;
    @Mock
    private BookmarkService mockBookmarkService;
    
    private Tags tags;
    
    @Before
    public void setup() {
        tag = new Tag();
        tag.setName("theme");
        
        tag.setObjectType("CUS");
        tag.setObjectIdentifier("123");
        tag.setValue("lightTheme");
        
        tags = new Tags();
        tags.setContainer(mockContainer);
        tags.injectBookmarkService(mockBookmarkService);
    }

    @Test
    public void whenTagNotNull_butTagValueIsNull_thenTagIsRemoved() {
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).remove(tag);
            }
        });
        
        tag = tags.tagFor(tag, mockCustomer, "someTag", null);
        assertThat(tag, is(nullValue()));
    }
    
    @Test
    public void whenTagNotNull_butTagValueIsEmptyString_thenTagIsRemoved() {
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).remove(tag);
            }
        });
        
        tag = tags.tagFor(tag, mockCustomer, "theme", "");
        assertThat(tag, is(nullValue()));
    }
    
    @Test
    public void whenTagNotNull_andTagValueIsNotNull_thenTagsValueIsUpdated() {
        tag = tags.tagFor(tag, mockCustomer, "theme", "darkTheme");
        assertThat(tag, is(not(nullValue())));
        assertThat(tag.getValue(), is("darkTheme"));
    }
    
    @Test
    public void whenTagIsNull_andTagValueIsNull_thenNothing() {
        tag = tags.tagFor(null, mockCustomer, "theme", null);
        assertThat(tag, is(nullValue()));
    }
    
    @Test
    public void whenTagIsNull_andTagValueIsEmptyString_thenNothing() {
        tag = tags.tagFor(null, mockCustomer, "theme", "");
        assertThat(tag, is(nullValue()));
    }
    
    @Test
    public void whenTagIsNull_andTagValueIsNotNull_thenTagCreatedAndSet() {
        final Tag newTag = new Tag();
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).newTransientInstance(Tag.class);
                will(returnValue(newTag));
                
                oneOf(mockBookmarkService).bookmarkFor(mockCustomer);
                will(returnValue(new Bookmark("CXS", "456")));
                oneOf(mockContainer).persist(newTag);
            }
        });
        tag = tags.tagFor(null, mockCustomer, "theme", "darkTheme");
        assertThat(tag, is(not(nullValue())));
        assertThat(tag.getObjectType(), is("CXS"));
        assertThat(tag.getObjectIdentifier(), is("456"));
        assertThat(tag.getName(), is("theme"));
        assertThat(tag.getValue(), is("darkTheme"));
    }
    
}
