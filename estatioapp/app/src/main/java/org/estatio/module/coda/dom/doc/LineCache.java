package org.estatio.module.coda.dom.doc;

import java.util.SortedSet;

public interface LineCache {
    SortedSet<CodaDocLine> linesFor(CodaDocHead codaDocHead);

    public static LineCache DEFAULT = codaDocHead -> {
        return codaDocHead.getLines();
    };
}
