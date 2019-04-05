package org.estatio.module.coda.dom.doc;

import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public final class ErrorSet {

    private final List<String> commentList = Lists.newArrayList();

    public ErrorSet add(final String format, final Object... args) {
        if(format == null) {
            return this;
        }
        final String message = String.format(format, args);
        if(!Strings.isNullOrEmpty(message)) {
            commentList.add(message);
        }
        return this;
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

    public String toString() {
        return getText();
    }
}
