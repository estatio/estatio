package org.estatio.module.coda.dom.doc;

import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public final class ErrorSet {

    private final List<String> commentList = Lists.newArrayList();

    public ErrorSet addIfNotEmpty(final String text) {
        if(!Strings.isNullOrEmpty(text)) {
            commentList.add(text);
        }
        return this;
    }

    public ErrorSet add(final String format, final Object... args) {
        return addIfNotEmpty(String.format(format, args));
    }

    public ErrorSet merge(final ErrorSet other) {
        final ErrorSet errorSet = new ErrorSet();
        errorSet.addAll(this);
        errorSet.addAll(other);
        return errorSet;
    }

    private void addAll(final ErrorSet errorSet) {
        for (final String s : errorSet.commentList) {
            add(s);
        }
    }

    public String getText() {
        return String.join("\n", commentList);
    }
    public boolean isNotEmpty() {
        return ! isEmpty();
    }
    public boolean isEmpty() {
        return commentList.isEmpty();
    }
}
