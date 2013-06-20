package org.estatio.dom.tag;

import java.util.List;
import java.util.TreeSet;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.filter.Filter;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.EstatioDomainService;

public class Tags extends EstatioDomainService<Tag> {

    public Tags() {
        super(Tags.class, Tag.class);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<String> choices(EstatioDomainObject<?> domainObject, String tagName) {
        final List<Tag> tags = doChoices(domainObject, tagName);
        final Iterable<String> tagNames = Iterables.transform(tags, Tag.GET_VALUE);
        final TreeSet<String> uniqueSortedTagNames = Sets.newTreeSet(tagNames);
        return Lists.newArrayList(uniqueSortedTagNames);
    }

    protected List<Tag> doChoices(EstatioDomainObject<?> domainObject, String tagName) {
        return allMatches(Tag.class, appliesTo(domainObject, tagName));
    }

    private Filter<Tag> appliesTo(final EstatioDomainObject<?> domainObject, final String tagName) {
        return new Filter<Tag>(){
            @Override
            public boolean accept(Tag t) {
                final Bookmark bookmark = bookmarkService.bookmarkFor(domainObject);
                return Objects.equal(t.getObjectType(), bookmark.getObjectType()) &&
                       Objects.equal(t.getName(), tagName);
            }
        };
    }

    @Programmatic
    public Tag tagFor(Tag tag, final EstatioDomainObject<?> domainObject, final String tagName, final String tagValue) {
        if(Strings.isNullOrEmpty(tagValue)) {
            if(tag != null) {
                remove(tag);
                tag = null;
            }
        } else {
            if(tag == null) {
                final Bookmark bookmark = bookmarkService.bookmarkFor(domainObject);
                tag = newTransientInstance();
                tag.setObjectType(bookmark.getObjectType());
                tag.setObjectIdentifier(bookmark.getIdentifier());
                tag.setName(tagName);
                persist(tag);
            }
            tag.setValue(tagValue);
        }
        return tag;
    }


    // //////////////////////////////////////

    private BookmarkService bookmarkService;

    public void injectBookmarkService(final BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }
 
}
